package main

import (
	"encoding/json"
	"fmt"
	"io/fs"
	"math"
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
	return g.app.cwd + string(filepath.Separator), nil
}
func (g *Getters) GetDirName() (bool, error) {
	dirName := filepath.Base(g.app.cwd)
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
	var totalSize int64
	err := filepath.WalkDir(dirPath, func(path string, d fs.DirEntry, err error) error {
		if err != nil {
			return err
		}
		if !d.IsDir() {
			info, err := d.Info()
			if err != nil {
				return err
			}
			totalSize += info.Size()
		}
		return nil
	})
	if err != nil {
		return 0, err
	}
	return totalSize, nil
}

// This value is between 0 to 100
func (g *Getters) GetPercentOfDriveToAppDirSize() (int64, error) {
	driveSize, err := g.GetRootDiskSpace()
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return 0, err
	}
	appDirSize, err := g.GetTotalDirSize(g.app.cwd)
	if err != nil {
		fmt.Printf("app size err %s", err)
		return 0, err
	}
	percent := appDirSize / int64(driveSize) * 100
	fmt.Printf("percent of drive to appdir %d", percent)
	return percent, nil
}

// GetFormattedDriveSize and GetFormattedAppDirSize retrieves the available memory from the backend
// and only returns the formatted string, which is much faster than calculating it on the frontend
func (g *Getters) GetFormattedDriveSize() (string, error) {
	driveSize, err := g.GetRootDiskSpace()
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return "", err
	}
	return formatDirSize(int64(driveSize)), nil
}

func (g *Getters) GetFormattedAppDirSize() (string, error) {
	appDirSize, err := g.GetTotalDirSize(g.app.cwd)
	if err != nil {
		fmt.Printf("app size err %s", err)
		return "", err
	}
	return formatDirSize(appDirSize), nil
}

func (g *Getters) GetPercentOfDriveToDirIndexSize(dirIndex int) (int64, error) {
	driveSize, err := g.GetRootDiskSpace()
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return 0, err
	}
	appDirSize, err := g.GetTotalDirSize(g.app.directories[dirIndex])
	if err != nil {
		fmt.Printf("app size err %s", err)
		return 0, err
	}
	percent := appDirSize / int64(driveSize) * 100
	fmt.Printf("percent of drive to appdir %d", percent)
	return percent, nil
}

func (g *Getters) GetFormattedDirIndexSize(dirIndex int) (string, error) {
	appDirSize, err := g.GetTotalDirSize(g.app.directories[dirIndex])
	if err != nil {
		fmt.Printf("app size err %s", err)
		return "", err
	}
	return formatDirSize(appDirSize), nil
}

func (g *Getters) CheckRootFolderInCWD() (string, error) {
	dirPath, err := getEndPath(rootFolder)
	if err != nil {
		return "", fmt.Errorf("error checking  %s: %w", dirPath, err)
	}
	return dirPath, nil
}

func (g *Getters) CheckKeyFileInCWD() (string, error) {
	dirPath, err := getEndPath(keyFileName)
	if err != nil {
		return "", fmt.Errorf("error checking  %s: %w", dirPath, err)
	}
	return dirPath, nil
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

func (g *Getters) GetRootFolder() string {
	return rootFolder
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
		props.FileType = "file"
	}
	return props, nil
}

// Backend method for formatting directory size, being more
// performant as directories can be much larger than files.
func formatDirSize(fileByteSize int64) string {
	if fileByteSize < 1 {
		return "empty"
	}
	units := []string{"B", "KB", "MB", "GB", "TB", "EB", "ZB", "YB"}
	digits := int(math.Log(float64(fileByteSize)) / math.Log(1024))
	unitIndex := digits
	if unitIndex >= len(units) {
		unitIndex = len(units) - 1
	}
	sizeInUnit := float64(fileByteSize) / math.Pow(1024, float64(unitIndex))

	var formattedSize string
	switch {
	case sizeInUnit >= 100:
		formattedSize = fmt.Sprintf("%.0f", sizeInUnit)
	case sizeInUnit >= 10:
		formattedSize = fmt.Sprintf("%.1f", sizeInUnit)
	default:
		formattedSize = fmt.Sprintf("%.2f", sizeInUnit)
	}
	return fmt.Sprintf("%s%s", formattedSize, units[unitIndex])
}

func getEndPath(endPathName string) (string, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return "", fmt.Errorf("failed to get cwd: %w", err)
	}
	dirPath := fmt.Sprintf("%s%s%s", cwd, string(filepath.Separator), endPathName)
	fmt.Println("full path " + endPathName + " " + dirPath)
	_, err = os.Stat(dirPath)
	if os.IsNotExist(err) {
		return "", nil // Directory does not exist
	}
	if err != nil {
		return "", fmt.Errorf("error checking directory %s: %w", dirPath, err)
	}
	return dirPath, nil
}

func findEncryptedDuplicates(dir string) ([]string, error) {
	filesMap := make(map[string][]string)
	var duplicates []string
	err := filepath.WalkDir(dir, func(path string, d os.DirEntry, err error) error {
		if err != nil {
			return err // Propagate errors
		}
		if d.IsDir() {
			return nil // Skip directories
		}
		fileName := d.Name()
		ext := filepath.Ext(fileName)
		baseName := fileName[0 : len(fileName)-len(ext)] // Remove extension

		if ext == ".enc" {
			originalExt := filepath.Ext(baseName)
			baseName = baseName[0 : len(baseName)-len(originalExt)] // Remove original extension
		}
		filesMap[baseName] = append(filesMap[baseName], path)
		return nil
	})

	// Check for duplicates
	for _, paths := range filesMap {
		if len(paths) > 1 {
			duplicates = append(duplicates, paths...)
		}
	}
	return duplicates, err
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
