package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"

	"strings"
)

var excludedDirsStartingIn []string
var excludedDirsContaining []string
var excludedPathsMatching []string

var excludedFiles []string
var excludedFilesPath []string

func checkExcludedDirAgainstPath(path string) bool {
	lowercasePath := strings.ToLower(path)
	var seprtr = string(filepath.Separator)
	for _, element := range excludedDirsStartingIn {
		splitPath := strings.SplitN(element, seprtr+"*"+seprtr, 2)
		if strings.HasPrefix(lowercasePath, splitPath[0]) &&
			strings.HasSuffix(lowercasePath, splitPath[1]) {
			fmt.Println("found excluded dir starting in " + element)
			return true
		}
	}
	for _, element := range excludedDirsContaining {
		if strings.Contains(lowercasePath, element) {
			fmt.Println("found excluded dir containing " + element)
			return true
		}
	}
	for _, element := range excludedPathsMatching {
		pathMatch := lowercasePath == element
		if pathMatch {
			fmt.Println("found excluded path matching " + element)
			return true
		}
	}
	return false
}

func checkExcludedFileAgainstPath(path string) bool {
	lowercasePath := strings.ToLower(path)
	for _, element := range excludedFiles {
		if strings.HasSuffix(lowercasePath, element) {
			fmt.Println("found excluded file ending in " + element)
			return true
		}
	}
	for _, element := range excludedFilesPath {
		splitPath := strings.SplitN(element, "*.", 2)
		if strings.HasPrefix(lowercasePath, splitPath[0]) &&
			strings.HasSuffix(lowercasePath, splitPath[1]) {
			fmt.Println("found excluded filePath ending in " + element)
			return true
		}
	}
	return false
}

func (f *FileUtils) SaveFileFilters(filterInputs string) error {
	savedExcludedInputs := strings.Split(filterInputs, "\n")
	keyFilePath, err := getEndPath(keyFileName)
	if err != nil {
		return err
	}
	_, err = os.Stat(keyFilePath)
	if os.IsNotExist(err) {
		if err != nil {
			fmt.Println("Key file does not exist")
			return err
		}
	}

	file, err := os.OpenFile(keyFilePath, os.O_RDWR|os.O_CREATE, 0666)
	if err != nil {
		return err
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	var lines []string
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}

	if err := scanner.Err(); err != nil {
		return err
	}
	jsonData, err := json.Marshal(savedExcludedInputs)
	if err != nil {
		return err
	}
	// Ensure there is a line to replace or append.
	if len(lines) < 2 {
		lines = append(lines, "") // Append empty line if < 2lines
	}
	lines[1] = string(jsonData)

	if err = file.Truncate(0); err != nil {
		return err
	}
	if _, err = file.Seek(0, 0); err != nil {
		return err
	}
	writer := bufio.NewWriter(file)
	for _, line := range lines {
		if _, err = writer.WriteString(line + "\n"); err != nil {
			return err
		}
	}
	if err = writer.Flush(); err != nil {
		return err
	}
	return nil
}

func (f *FileUtils) LoadFileFilters() ([]string, error) {
	keyFilePath, err := getEndPath(keyFileName)
	if err != nil {
		return nil, err
	}
	_, err = os.Stat(keyFilePath)
	if os.IsNotExist(err) {
		fmt.Println("Key file does not exist")
		return nil, err
	}
	file, err := os.Open(keyFilePath)
	if err != nil {
		fmt.Println("Error opening the file:", err)
		return nil, err
	}
	defer file.Close()
	scanner := bufio.NewScanner(file)

	if !scanner.Scan() && scanner.Err() != nil {
		fmt.Println("Failed to scan the first line:", scanner.Err())
		return nil, scanner.Err()
	}
	// Read the second line which should contain the JSON string array
	if !scanner.Scan() {
		if err := scanner.Err(); err != nil {
			fmt.Println("Failed to scan the second line:", err)
			return nil, err
		}
		fmt.Println("No second line in the file")
		return nil, fmt.Errorf("no second line in file")
	}
	jsonLine := scanner.Text()
	// Decode JSON string into a slice of strings
	var stringSlice []string
	if err := json.Unmarshal([]byte(jsonLine), &stringSlice); err != nil {
		fmt.Println("Error decoding JSON:", err)
		return nil, err
	}
	return stringSlice, nil
}

func (f *FileUtils) ClearExcludedSlices() {
	excludedDirsStartingIn = nil
	excludedDirsContaining = nil
	excludedPathsMatching = nil
	excludedFiles = nil
	excludedFilesPath = nil
}

func (f *FileUtils) AddInputToFilterTemplate(input string) {
	input = replaceSeparator(input)
	var seprtr = string(filepath.Separator)
	// Check if input starts with '*/' and ends with '/*'
	if strings.HasPrefix(input, "*"+seprtr) && strings.HasSuffix(input, seprtr+"*") {
		f.app.excludePathContaining(input)
		fmt.Println("excludePathContaining " + input)
		// Check if input starts with a folder name and ends with a folder name without wildcard '*'
	} else if !strings.Contains(input, "*") && strings.Contains(input, seprtr) {
		f.app.excludePathMatching(input)
		fmt.Println("excludePathMatching " + input)
		// Check if input starts with a folder name and ends with a folder name but contains wildcard '*'
	} else if !strings.HasPrefix(input, "*") &&
		strings.Contains(input, seprtr+"*"+seprtr) {
		f.app.excludePathStartingIn(input)
		fmt.Println("excludeInputStartingIn " + input)
		// Check if input starts with wildcard '*' and has no separator
	} else if strings.HasPrefix(input, "*") && !strings.Contains(input, seprtr) {
		f.app.excludeFile(input)
		// Check if input starts with a folder name and ends in a wildcard file extension
	} else if strings.Contains(filepath.Base(input), "*.") &&
		!strings.HasPrefix(input, "*") {
		f.app.excludeFilePath(input)
		fmt.Println("excludeFileExtension " + input)
	}
}

func (a *App) excludePathContaining(input string) {
	//example: */.git/objects/*
	input = strings.ReplaceAll(input, "*", "")
	input = replaceSeparator(input)
	fmt.Println("excluded dir containing: " + input)
	if !contains(excludedDirsContaining, strings.ToLower(input)) {
		excludedDirsContaining = append(excludedDirsContaining, strings.ToLower(input))
	}
}

func (a *App) excludePathMatching(input string) {
	//example: VAULT/CODE/.git/objects/ or VAULT/CODE/Thumbs.db
	input = a.formatExcludePath(input)
	if !contains(excludedPathsMatching, input) {
		excludedPathsMatching = append(excludedPathsMatching, input)
	}
}

func (a *App) excludePathStartingIn(input string) {
	//example: VAULT/CODE/*/.git/objects/ or VAULT/MISC/*/thumbs.txt
	var seprtr = string(filepath.Separator)
	splitPath := strings.SplitN(input, seprtr+"*"+seprtr, 2)
	splitPath[0] = a.formatExcludePath(splitPath[0])
	joinedPath := strings.Join(splitPath, seprtr+"*"+seprtr)
	if !contains(excludedDirsStartingIn, joinedPath) {
		excludedDirsStartingIn = append(excludedDirsStartingIn, joinedPath)
	}
}

func (a *App) excludeFile(input string) {
	//example : *Thumbs.db || *.txt
	input = strings.TrimPrefix(input, "*")
	if !contains(excludedFiles, strings.ToLower(input)) {
		excludedFiles = append(excludedFiles, strings.ToLower(input))
	}
}

func (a *App) excludeFilePath(input string) {
	//example: VAULT/images/nsfw/*.png
	splitPath := strings.SplitN(input, "*.", 2)
	splitPath[0] = a.formatExcludePath(splitPath[0]) + string(filepath.Separator)
	joinedPath := strings.ToLower(strings.Join(splitPath, "*."))
	if !contains(excludedFilesPath, joinedPath) {
		excludedFilesPath = append(excludedFilesPath, joinedPath)
	}
}

func (a *App) formatExcludePath(input string) string {
	input = filepath.Clean(input) // Clean the input path to normalize it
	input = replaceSeparator(input)
	if !strings.HasPrefix(input, a.cwd) {
		input = filepath.Join(a.cwd, input)
	}
	return strings.ToLower(input) // Return the possibly modified input
}

// contains checks if a slice contains a particular string.
func contains(slice []string, str string) bool {
	for _, v := range slice {
		if v == str {
			return true
		}
	}
	return false
}
