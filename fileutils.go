package main

import (
	"archive/zip"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"sync/atomic"
	"time"

	stdRuntime "runtime"

	wailsRuntime "github.com/wailsapp/wails/v2/pkg/runtime" // This is an example path
)

type FileUtils struct {
	app *App
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
		for _, files := range filePaths {
			if targetPath == filepath.Dir(files) {
				continue
			}
			newFilePath := filepath.Join(targetPath, filepath.Base(files))
			// Move the file
			err := os.Rename(files, newFilePath)
			if err != nil {
				wailsRuntime.LogError(f.app.ctx, "Error moving file: "+err.Error())
				continue
			}
			wailsRuntime.LogError(f.app.ctx, "Successfully moved file to: "+newFilePath)
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
	var separator = string(filepath.Separator)
	rootDir = filepath.Clean(rootDir)
	rootNode := &FileNode{RelPath: rootDir + separator, Children: []*FileNode{}}
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
		parts := strings.Split(relativePath, separator)
		addPath(rootNode, parts, "")
		return nil
	})

	if err != nil {
		return nil, err
	}
	return rootNode, nil
}

func (f *FileUtils) PackFilesForENCP(receiverUsername, receiverPassword string, filePaths []string) (string, error) {
	f.app.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	lastFilePath := filePaths[len(filePaths)-1]
	filenameWithoutExtension := removeFileExtension(lastFilePath)
	zipFilePath := filenameWithoutExtension
	// Create the zip file
	zipFile, err := os.Create(zipFilePath)
	if err != nil {
		return "", err
	}
	defer zipFile.Close()
	// Create a new zip writer
	zipWriter := zip.NewWriter(zipFile)
	defer zipWriter.Close()
	for _, filePath := range filePaths {
		select {
		case <-interrupt: // Check if there's an interrupt signal
			fmt.Printf("packing interrupted")
			f.app.lastFilePath = filePath
			return "", nil
		default:
			if err := f.addFileToZip(zipWriter, filePath); err != nil {
				return "", nil
			}
		}
	}
	if err := zipWriter.Close(); err != nil {
		return "", fmt.Errorf("error closing zip writer: %s", err)
	}
	zipFile.Close()
	return zipFile.Name(), nil
}

func (f *FileUtils) AuthenticateENCPFile(password string, encFilePath string) (bool, error) {
	var shuffledCredentials = shuffleStrings(hashedUsername, password)
	_hashedCredentials, err := hashString(shuffledCredentials)
	if err != nil {
		return false, fmt.Errorf("error hashing user credentials: %s", err)
	}
	decryptedZipFile, err := f.app.decryptENCPFile([]byte(_hashedCredentials), encFilePath)
	if err != nil {
		fmt.Printf("error decrypting zip file: %s", err)
		return false, fmt.Errorf("error writing decrypted encp to file: %s", err)
	}
	return decryptedZipFile, nil
}

func (f *FileUtils) GetAppendedFileBytes(filePath string) error {
	file, err := os.Open(filePath)
	if err != nil {
		return err
	}
	defer file.Close()
	info, err := file.Stat()
	if err != nil {
		return err
	}
	// Check if the file size is more than 10 bytes
	if info.Size() > 10 {
		startPos := info.Size() - 5
		// Seek to the start position
		_, err := file.Seek(startPos, 0)
		if err != nil {
			return err
		}
		// Read the last 5 bytes
		buf := make([]byte, 5)
		_, err = file.Read(buf)
		if err != nil {
			return err
		}
		fmt.Println("Last 5 bytes:", buf)
	}
	return nil
}

func (f *FileUtils) SaveLogEntries(files, timestamps []string) {

}

func formatTime(isoString string) string {
	// Parse the ISO string
	t, err := time.Parse(time.RFC3339, isoString)
	if err != nil {
		fmt.Println("Error parsing time:", err)
		return ""
	}

	return t.Format("01-02-06 03:04 PM")
}

func (f *FileUtils) addFileToZip(zipWriter *zip.Writer, zipFilePath string) error {
	fileToZip, err := os.Open(zipFilePath)
	if err != nil {
		return err
	}
	defer fileToZip.Close()
	info, err := fileToZip.Stat()
	if err != nil {
		return err
	}
	header, err := zip.FileInfoHeader(info)
	if err != nil {
		return err
	}
	header.Name = filepath.Base(zipFilePath)
	// Create the writer for the file
	writer, err := zipWriter.CreateHeader(header)
	if err != nil {
		return err
	}

	const thresholdFileSize = 50 //if file is more than 50MB
	if info.Size() > int64(thresholdFileSize*1024*1024) {
		err = f.app.checkLargeZipFileTicker(writer, fileToZip)
	} else {
		_, err = io.Copy(writer, fileToZip)
	}
	fmt.Printf("Copied %s to zip file \n", zipFilePath)
	return err
}

func (a *App) checkLargeZipFileTicker(writer io.Writer, fileToZip *os.File) error {
	fileSize, err := fileToZip.Seek(0, io.SeekEnd) // Get the file size
	if err != nil {
		return err
	}
	_, err = fileToZip.Seek(0, io.SeekStart) // Reset file pointer
	if err != nil {
		return err
	}

	var writtenBytes int64 = 0
	var lastpercentInt = 0
	buf := make([]byte, 2096) // 4KB buffer
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
		percent := float64(writtenBytes) / float64(fileSize) * 100
		percentInt := int(percent + 0.5)
		if lastpercentInt != percentInt {
			wailsRuntime.EventsEmit(a.ctx, largeFilePercent, percentInt)
			fmt.Printf("Zip Progress: %.2f%%\n", percent)
		}
		lastpercentInt = percentInt
		if err == io.EOF {
			break
		}
	}
	wailsRuntime.EventsEmit(a.ctx, largeFilePercent, 0)
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
