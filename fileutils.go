package main

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type FileUtils struct {
	directories []string // Your directories list
}

var isInFileTask = false
var rebuildFileTree = "rebuildFileTree"

func (f *FileUtils) SetIsInFileTask(b bool) bool {
	isInFileTask = b
	return isInFileTask
}

func getFilesRecursively(dirs ...string) ([]string, error) {
	var files []string
	for _, dir := range dirs {
		err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
			if err != nil {
				return err
			}
			if !info.IsDir() { // Ensure we're only appending files, not directories
				files = append(files, path)
			}
			return nil
		})
		if err != nil {
			return nil, err
		}
	}
	return files, nil
}

// APP STRUCT
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
	// Initialize rootNode. It does not represent the rootDir itself but its contents.
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

func (a *App) FilesDragNDrop(fileBytes []byte, fileName string, modifiedDate int64, pathToMoveTo string) error {
	if a.ctx != nil {
		if _, err := os.Stat(pathToMoveTo); os.IsNotExist(err) {
			err := os.MkdirAll(pathToMoveTo, os.ModePerm)
			if err != nil {
				return fmt.Errorf("failed to create target directory: %w", err)
			}
		}

		fullPath := filepath.Join(pathToMoveTo, fileName)
		if len(fileBytes) > 10*1024*1024 {
			// Start a goroutine to log file size during writing
			go func() {
				ticker := time.NewTicker(1 * time.Second) // Adjust the frame interval as needed
				defer ticker.Stop()

				for range ticker.C {
					if info, err := os.Stat(fullPath); err == nil {
						fmt.Printf("Current size of %s: %d bytes\n", fileName, info.Size())
					}
				}
			}()
		}
		err := os.WriteFile(fullPath, fileBytes, 0644) // 0644 provides read/write permissions to the owner and read-only for others
		if err != nil {
			return fmt.Errorf("failed to write file: %w", err)
		}

		// Convert the modifiedDate from milliseconds to time.Time
		modTime := time.UnixMilli(modifiedDate)

		// Set the last modified time of the file
		if err := os.Chtimes(fullPath, modTime, modTime); err != nil {
			return fmt.Errorf("failed to set last modified date: %w", err)
		}
		runtime.EventsEmit(a.ctx, rebuildFileTree)
		return nil
	}
	return nil
}
