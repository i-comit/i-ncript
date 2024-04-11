package main

import (
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"time"
	"unicode/utf8"
)

// GETTER STRUCT
type Getters struct {
	app         *App
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

func (g *Getters) GetFileTreePath(dirIndex int, relPath string) (string, error) {
	return g.directories[dirIndex] + string(filepath.Separator) + relPath, nil
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

func (g *Getters) GetTotalDirSize(dirPath string) (int64, error) {
	var totalSize int64 // This will hold the total size of all files
	err := filepath.Walk(dirPath, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err // Propagate the error
		}
		if !info.IsDir() {
			totalSize += info.Size() // Add file size, ignoring directories
		}
		return nil
	})
	if err != nil {
		return 0, err // Return the error if something went wrong during the walk
	}
	return totalSize, nil // Return the total size of the files
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

func getKeyFilePath() string {
	cwd, err := os.Getwd()
	if err != nil {
		fmt.Printf("Failed to get current working directory: %s", err)
		return ""
	}
	keyFilePath := filepath.Join(cwd, keyFileName)
	fmt.Println("path to keyFile " + keyFilePath)

	file, err := os.Open(keyFilePath)
	if err != nil {
		fmt.Println("Key file doesn't exist", err)
		return ""
	}
	defer file.Close()
	return keyFilePath
}

func printFileTree(fileTree *FileNode, print bool) error {
	if !print {
		return nil
	}
	data, err := json.MarshalIndent(fileTree, "", "  ")
	if err != nil {
		fmt.Println("Error marshaling tree to JSON:", err)
		return err
	}
	jsonStr := string(data)
	fmt.Println(jsonStr)
	return nil
}

func shuffleStrings(str1, str2 string) string {
	// Convert strings to rune slices to properly handle multi-byte characters.
	runes1 := []rune(str1)
	runes2 := []rune(str2)
	var result []rune
	// Iterate over both rune slices and append alternately to the result slice.
	maxLen := len(runes1)
	if len(runes2) > maxLen {
		maxLen = len(runes2)
	}

	for i := 0; i < maxLen; i++ {
		if i < len(runes1) {
			result = append(result, runes1[i])
		}
		if i < len(runes2) {
			result = append(result, runes2[i])
		}
	}
	fmt.Println("shuffled " + string(result))
	return string(result)
}

func removeFileExtension(_filePath string) string {
	extension := filepath.Ext(_filePath)
	filePathWithoutExtension := strings.TrimSuffix(_filePath, extension)
	fmt.Println("file w/o extension " + filePathWithoutExtension)
	return filePathWithoutExtension
}

func bytesToRunes(b []byte) []rune {
	var r []rune
	for len(b) > 0 {
		runeValue, size := utf8.DecodeRune(b)
		r = append(r, runeValue)
		b = b[size:]
	}
	return r
}
func runesToBytes(r []rune) []byte {
	var b []byte
	for _, runeVal := range r {
		b = append(b, byte(runeVal))
	}
	return b
}
