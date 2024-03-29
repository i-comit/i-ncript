package main

import (
	"context"
	"fmt"
	"os"
	"path/filepath"
	"time"
)

// GETTER STRUCT
type Getters struct {
	ctx         context.Context
	directories []string
}

func (g *Getters) GetAppPath() (string, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return "", err
	}
	return cwd + string(filepath.Separator), nil
}
func (b *Getters) GetDirName() (bool, error) {
	path, err := os.Getwd()
	if err != nil {
		return false, err
	}
	dirName := filepath.Base(path)
	match := (dirName == rootFolder)
	return match, nil
}

func (g *Getters) GetFilesByType(dirIndex int, getFileType bool) ([]string, error) {
	filePaths, err := getFilesRecursively(g.directories[dirIndex])
	if err != nil {
		return nil, err
	}

	var files []string
	for _, filePath := range filePaths {
		isEncrypted := filepath.Ext(filePath) == ".enc"
		if getFileType && isEncrypted {
			// If looking for encrypted files and the file is encrypted
			files = append(files, filePath)
		} else if !getFileType && !isEncrypted {
			// If looking for unencrypted files and the file is not encrypted
			files = append(files, filePath)
		}
	}
	return files, nil
}

func (g *Getters) GetDirectoryPath(dirIndex int) (string, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(g.directories) {
		dirIndex = len(g.directories) - 1
	}
	return g.directories[dirIndex] + string(filepath.Separator), nil
}

func (g *Getters) GetDirectoryFileCt(dirIndex int) (int, error) {
	fileCount := 0
	err := filepath.Walk(g.directories[dirIndex], func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			fileCount++
		}
		return nil
	})
	if err != nil {
		return 0, err
	}
	fmt.Printf("\033[33mdirectories[dirIndex]: %s %d \033[0m\n", g.directories[dirIndex], fileCount)
	return fileCount, nil
}

type FileProperties struct {
	ModifiedDate string `json:"modifiedDate"`
	FileSize     int64  `json:"fileSize"`
	FileType     string `json:"fileType"`
}

func (b *Getters) GetFileProperties(filePath string) (FileProperties, error) {
	var props FileProperties
	fileInfo, err := os.Stat(filePath)
	if err != nil {
		fmt.Println("\033[33mError accessing file:", err, "\033[0m")
		return props, err
	}

	// Convert time.Time to RFC3339 formatted string
	props.ModifiedDate = fileInfo.ModTime().Format(time.DateOnly) + " " + fileInfo.ModTime().Format(time.Kitchen)
	props.FileSize = fileInfo.Size()

	if fileInfo.IsDir() {
		props.FileType = "dir"
	} else {
		// fmt.Println("\033[33mFileType:", fileInfo.Name(), "\033[0m")
		// switch(filepath.Ext(fileInfo.Name()))

		props.FileType = "file"
	}
	return props, nil
}

func (g *Getters) GetRootFolder() string {
	return rootFolder
}