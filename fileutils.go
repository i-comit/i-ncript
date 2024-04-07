package main

import (
	"archive/zip"
	"context"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"
	"sync/atomic"

	stdRuntime "runtime"

	wailsRuntime "github.com/wailsapp/wails/v2/pkg/runtime" // This is an example path
)

type FileUtils struct {
	app *App
	ctx context.Context
}

var isInFileTask = false
var rebuildFileTree = "rebuildFileTree"

func (f *FileUtils) CheckIfPathIsFile(filePath string) (bool, error) {
	info, err := os.Stat(filePath)
	if err != nil {
		return false, err
	}
	return !info.IsDir(), nil
}

func (f *FileUtils) MoveFilesToPath(filePaths []string, targetPath string) {
	if f.app.ctx != nil {
		for i, files := range filePaths {
			if targetPath == filepath.Dir(files)+string(filepath.Separator) {
				// runtime.LogError(f.app.ctx, "This file already belongs in the targetPath")
				continue
			}
			newFilePath := filepath.Join(targetPath, filepath.Base(files))
			// Move the file
			err := os.Rename(files, newFilePath)
			if err != nil {
				// Handle errors (e.g., file not found, permission issues, etc.)
				wailsRuntime.LogError(f.app.ctx, "Error moving file: "+err.Error())
				continue
			}
			wailsRuntime.LogError(f.app.ctx, "Successfully moved file to: "+newFilePath+" "+strconv.Itoa(i))
		}
		wailsRuntime.EventsEmit(f.app.ctx, rebuildFileTree)
		f.app.SetIsInFileTask(false)
	}
}

func (f *FileUtils) OpenFile(filePath string) error {
	var cmd *exec.Cmd
	switch stdRuntime.GOOS {
	case "windows":
		cmd = exec.Command("cmd", "/c", "start", "", filePath)
	case "darwin":
		cmd = exec.Command("open", filePath)
	case "linux":
		cmd = exec.Command("xdg-open", filePath)
	default:
		return fmt.Errorf("unsupported platform")
	}

	return cmd.Start()
}

func (f *FileUtils) OpenDirectory(_filePath string) error {
	// Determine the directory path from the filePath
	dirPath := _filePath
	fileInfo, err := os.Stat(_filePath)
	if err != nil {
		return err
	}
	if !fileInfo.IsDir() {
		dirPath = filepath.Dir(_filePath) + string(filepath.Separator)
	}
	fmt.Println("DirPath open directory " + dirPath)
	// Open the directory based on the operating system
	var cmd *exec.Cmd
	switch stdRuntime.GOOS {
	case "windows":
		cmd = exec.Command("explorer", dirPath)
	case "darwin":
		cmd = exec.Command("open", dirPath)
	case "linux":
		cmd = exec.Command("xdg-open", dirPath)
	default:
		return fmt.Errorf("unsupported platform")
	}
	return cmd.Start()
}

type FileNode struct {
	RelPath  string      `json:"relPath"`
	Children []*FileNode `json:"children,omitempty"`
}

func (a *App) BuildDirectoryFileTree(dirIndex int) (*FileNode, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(a.directories) {
		dirIndex = len(a.directories) - 1
	}
	var rootDir = a.directories[dirIndex]
	rootDir = filepath.Clean(rootDir)
	rootNode := &FileNode{RelPath: rootDir, Children: []*FileNode{}}
	err := filepath.Walk(rootDir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		path = filepath.Clean(path)
		if path == rootDir {
			// fmt.Println("ROOT PATH " + path)
			return nil
		}

		relativePath, err := filepath.Rel(rootDir, path)
		if err != nil {
			return err
		}
		parts := strings.Split(relativePath, string(filepath.Separator))
		addPath(rootNode, parts, "")
		return nil
	})

	if err != nil {
		return nil, err
	}
	return rootNode, nil
}

func (f *FileUtils) PackFilesForENCP(filePaths []string) error {
	f.app.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	lastFilePath := filePaths[len(filePaths)-1]
	zipFilePath := lastFilePath + ".zip"
	// Create the zip file
	zipFile, err := os.Create(zipFilePath)
	if err != nil {
		return err
	}
	defer zipFile.Close()
	// Create a new zip writer
	zipWriter := zip.NewWriter(zipFile)
	defer zipWriter.Close()
	for _, filePath := range filePaths {
		if err := f.addFileToZip(zipWriter, filePath); err != nil {
			return err
		}
	}
	return nil
}

func (f *FileUtils) addFileToZip(zipWriter *zip.Writer, filePath string) error {
	fileToZip, err := os.Open(filePath)
	if err != nil {
		return err
	}
	defer fileToZip.Close()
	// Get the info about the file
	info, err := fileToZip.Stat()
	if err != nil {
		return err
	}
	// Create a header based on the file info
	header, err := zip.FileInfoHeader(info)
	if err != nil {
		return err
	}
	// Use the basename of the filePath for the file name in the zip
	header.Name = filepath.Base(filePath)
	// Create the writer for the file
	writer, err := zipWriter.CreateHeader(header)
	if err != nil {
		return err
	}

	const thresholdFileSize = 50 //if file is more than 50MB
	if info.Size() > int64(thresholdFileSize*1024*1024) {
		err = f.checkLargeZipFileTicker(writer, fileToZip)
	} else {
		_, err = io.Copy(writer, fileToZip)
	}
	fmt.Printf("Copied %s to zip file \n", filePath)
	return err
}

func (f *FileUtils) checkLargeZipFileTicker(writer io.Writer, fileToZip *os.File) error {
	fileSize, err := fileToZip.Seek(0, io.SeekEnd) // Get the file size
	if err != nil {
		return err
	}
	_, err = fileToZip.Seek(0, io.SeekStart) // Reset file pointer
	if err != nil {
		return err
	}

	var writtenBytes int64 = 0
	// Initialize your progress tracking here, if needed
	buf := make([]byte, 4096) // 4KB buffer
	for {
		n, err := fileToZip.Read(buf)
		if err != nil && err != io.EOF {
			return err
		}
		if n == 0 {
			break
		}
		wn, err := writer.Write(buf[:n])
		if err != nil {
			return err
		}
		atomic.AddInt64(&writtenBytes, int64(wn))

		// Update progress here using writtenBytes and fileSize
		percent := float64(writtenBytes) / float64(fileSize) * 100
		fmt.Printf("Progress: %.2f%%\n", percent) // Replace with your event emission

		if err == io.EOF {
			break
		}
	}
	return nil
}

func addPath(node *FileNode, parts []string, currentPath string) {
	if len(parts) == 0 {
		return
	}
	if currentPath != "" {
		currentPath += string(filepath.Separator) + parts[0]
	} else {
		currentPath = parts[0]
	}

	for _, child := range node.Children {
		if filepath.Base(child.RelPath) == parts[0] {
			// If the child matches the next part, recursively call addPath on the child.
			addPath(child, parts[1:], currentPath)
			return
		}
	}
	newNode := &FileNode{
		RelPath:  currentPath,
		Children: []*FileNode{},
	}
	node.Children = append(node.Children, newNode)
	addPath(newNode, parts[1:], currentPath)
}

func createDirectories(dirs ...string) error {
	for _, dir := range dirs {
		if _, err := os.Stat(dir); os.IsNotExist(err) {
			err := os.Mkdir(dir, 0755)
			if err != nil {
				return err
			}
		}
	}
	return nil
}

func getFilesRecursively(dirs ...string) ([]string, error) {
	var files []string
	for _, dir := range dirs {
		err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
			if err != nil {
				return err
			}
			if !info.IsDir() { // Ensure we're only appending files, not directories
				if filepath.Base(path) != keyFileName {
					files = append(files, path)
				}
			}
			return nil
		})
		if err != nil {
			return nil, err
		}
	}
	return files, nil
}
