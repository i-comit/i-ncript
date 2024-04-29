package main

import (
	"encoding/json"
	"fmt"
	"io"
	"io/fs"
	"math"
	"net/http"
	"os"
	"path/filepath"

	"github.com/wailsapp/wails/v2/pkg/runtime"

	"strings"
	"time"
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
	match := (dirName == strings.ToLower(rootFolder))
	return match, nil
}

func (g *Getters) GetFileTreePath(dirIndex int, relPath string) (string, error) {
	return g.directories[dirIndex] + string(filepath.Separator) + relPath, nil
}

func (g *Getters) GetFilesByType(dirIndex int, encryptOrDecrypt bool) ([]string, error) {
	filePaths, err := g.app.loadFilesRecursively(encryptOrDecrypt, g.directories[dirIndex])
	if err != nil {
		return nil, err
	}
	return filePaths, nil
}

// GetFormattedDriveSize and GetFormattedAppDirSize retrieves the available memory from the backend
// and only returns the formatted string, which is much faster than calculating it on the frontend
func (g *Getters) GetFormattedDiskSpace(totalOrFreeSpace bool) (string, error) {
	driveSize, err := g.getDiskSpace(totalOrFreeSpace)
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return "", err
	}
	return formatDirSize(int64(driveSize)), nil
}

func (g *Getters) GetDiskSpacePercent() (int, error) {
	totalDiskSpace, err := g.getDiskSpace(true)
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return 0, err
	}
	freeDiskSpace, err := g.getDiskSpace(false)
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return 0, err
	}
	percent := (float64(freeDiskSpace) / float64(totalDiskSpace)) * 100
	percentInt := int(percent)
	return percentInt, nil
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
		runtime.LogError(g.app.ctx, "Error getting dir size: "+err.Error())
		return 0, err
	}
	return totalSize, nil
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
	dirPath, err := getEndPath(strings.ToLower(rootFolder))
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

func (g *Getters) GetHeight() int {
	return _height
}

func (g *Getters) GetRootFolder() string {
	return rootFolder
}

type Release struct {
	TagName string `json:"tag_name"` // You can add more fields based on the JSON response
	HtmlUrl string `json:"html_url"` // URL pointing to the release on GitHub's website
}

func (g *Getters) GetLatestRelease() (*Release, error) {
	repo := "i-comit/i-ncript"
	url := fmt.Sprintf("https://api.github.com/repos/%s/releases/latest", repo)

	resp, err := http.Get(url)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("received non-200 response status: %d", resp.StatusCode)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	var release Release
	if err := json.Unmarshal(body, &release); err != nil {
		return nil, err
	}

	return &release, nil
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
		runtime.LogError(b.app.ctx, fmt.Sprintf("File access error: %v", err))
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

func (b *Getters) FindEncryptedDuplicates(dirIndex int) ([]string, error) {
	filesMap := make(map[string]map[string]string)
	var duplicates []string

	err := filepath.WalkDir(b.app.directories[dirIndex], func(path string, d os.DirEntry, err error) error {
		if err != nil {
			return err // Propagate errors
		}
		if d.IsDir() {
			return nil // Skip directories
		}
		fullPath := filepath.Dir(path)                // Directory of the file
		fileName := d.Name()                          // Full filename
		ext := filepath.Ext(fileName)                 // Extension including ".enc" if present
		baseName := fileName[:len(fileName)-len(ext)] // Base name without extension

		// Initialize map for directory if not already done
		if filesMap[fullPath] == nil {
			filesMap[fullPath] = make(map[string]string)
		}
		// Check if the file is an encrypted file
		if ext == ".enc" {
			// Extract original file extension from file like "example.txt.enc"
			originalExt := filepath.Ext(baseName)
			// Base name without original extension
			originalBaseName := baseName[:len(baseName)-len(originalExt)]

			// Check for presence of non-encrypted counterpart
			if _, ok := filesMap[fullPath][originalBaseName+originalExt]; ok {
				duplicates = append(duplicates, path) // Append the path of the .enc file if counterpart exists
			}
		} else {
			// Store non-encrypted file with its extension
			filesMap[fullPath][baseName+ext] = path
		}
		return nil
	})
	return duplicates, err
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
	_, err = os.Stat(dirPath)
	if os.IsNotExist(err) {
		return "", nil // Directory does not exist
	}
	if err != nil {
		return "", fmt.Errorf("error checking directory %s: %w", dirPath, err)
	}
	return dirPath, nil
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
	return filePathWithoutExtension
}
