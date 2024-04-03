package main

import (
	"context"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"strings"

	// "sync"
	// "sync/atomic"

	"time"

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
	RelPath string `json:"relPath"`
	// IsDir  bool      `json:"isDir"`
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

func (f *FileUtils) FilesDragNDrop(fileBytes []byte, fileName string, modifiedDate int64, pathToMoveTo string) error {
	if f.ctx != nil {
		if _, err := os.Stat(pathToMoveTo); os.IsNotExist(err) {
			err := os.MkdirAll(pathToMoveTo, os.ModePerm)
			if err != nil {
				return fmt.Errorf("failed to create target directory: %w", err)
			}
		}
		fullPath := filepath.Join(pathToMoveTo, fileName)
		file, err := os.OpenFile(fullPath, os.O_CREATE|os.O_WRONLY, 0644)
		if err != nil {
			return fmt.Errorf("failed to open file for writing: %w", err)
		}
		defer file.Close()

		var writtenBytes int64
		done := make(chan bool)

		// if len(fileBytes) > 5*1024*1024 {
		// 	go func() {
		// 		ticker := time.NewTicker(time.Millisecond) // Slower ticker to reduce console spam
		// 		defer ticker.Stop()
		// 		for {
		// 			select {
		// 			case <-ticker.C:
		// 				fmt.Printf("Current size of %s: %d bytes\n", fileName, writtenBytes) // Direct access under mutex protection
		// 			case <-done:
		// 				fmt.Printf("Final size of %s: %d bytes\n", fileName, writtenBytes)
		// 				return
		// 			}
		// 		}
		// 	}()
		// }
		chunkSize := 1024 // Define a suitable chunk size
		for i := 0; i < len(fileBytes); i += chunkSize {
			end := i + chunkSize
			if end > len(fileBytes) {
				end = len(fileBytes)
			}
			n, err := file.Write(fileBytes[i:end])
			if err != nil {
				close(done) // Ensure to signal the goroutine to stop in case of an error
				return fmt.Errorf("failed to write file: %w", err)
			}
			writtenBytes += int64(n) // Safe to update within the mutex-protected block
			// atomic.AddInt64(&writtenBytes, int64(n)) // Atomic version
		}
		close(done) // Signal the completion of file writing to the goroutine
		modTime := time.UnixMilli(modifiedDate)

		// Set the last modified time of the file
		if err := os.Chtimes(fullPath, modTime, modTime); err != nil {
			return fmt.Errorf("failed to set last modified date: %w", err)
		}
		wailsRuntime.EventsEmit(f.app.ctx, rebuildFileTree)
		f.app.SetIsInFileTask(false)
		return nil
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
