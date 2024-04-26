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

var excludedFilesEndingIn []string

func checkExcludedDirAgainstPath(path string) bool {
	lowercasePath := strings.ToLower(path)
	for _, element := range excludedDirsStartingIn {
		if strings.HasPrefix(lowercasePath, element) {
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
	for _, element := range excludedFilesEndingIn {
		if strings.HasSuffix(lowercasePath, element) {
			fmt.Println("found excluded file ending in " + element)
			return true
		}
	}
	for _, element := range excludedPathsMatching {
		pathMatch := lowercasePath == element
		if pathMatch {
			fmt.Println("found excluded path matching file" + element)
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
	excludedFilesEndingIn = nil
}

func (f *FileUtils) AddInputToFilterTemplate(input string) {
	input = replaceSeparator(input)
	var separator = string(filepath.Separator)
	// Check if input starts with '*/', ends with '/*'
	if strings.HasPrefix(input, "*"+separator) && strings.HasSuffix(input, separator+"*") {
		f.app.excludePathContaining(input)
		fmt.Println("excludePathContaining " + input)
		// Check if input starts with a folder name and ends with a folder name without wildcard '*'
	} else if !strings.Contains(input, "*") && strings.Contains(input, separator) {
		f.app.excludePathMatching(input)
		fmt.Println("excludePathMatching " + input)
		// Check if input starts with a folder name and ends with a folder name but contains wildcard '*'
	} else if strings.Contains(input, separator) &&
		!strings.HasPrefix(input, "*") &&
		strings.HasSuffix(input, "*") {
		f.app.excludeInputStartingIn(input)
		fmt.Println("excludeInputStartingIn " + input)
		// Check if input starts with wildcard '*' and has no separator
	} else if strings.HasPrefix(input, "*") && !strings.Contains(input, separator) && strings.Contains(input, ".") {
		f.app.excludeFileEndingIn(input)
	} else if !strings.HasPrefix(input, "*") && !strings.Contains(input, separator) && strings.Contains(input, ".") {
		f.app.excludePathMatching(input)
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

func (a *App) excludeInputStartingIn(input string) {
	//example: VAULT/MISC/* or VAULT/MISC/thumbs.txt*
	input = strings.TrimSuffix(input, "*")
	input = a.formatExcludePath(input)
	fmt.Println("exclude dirs starting in: ", input) // Print the final path to debug
	if !contains(excludedDirsStartingIn, input) {
		excludedDirsStartingIn = append(excludedDirsStartingIn, input)
	}
}

func (a *App) excludeFileEndingIn(input string) {
	//example : *Thumbs.db || *.txt
	input = strings.TrimPrefix(input, "*")
	excludedFilesEndingIn = append(excludedFilesEndingIn, strings.ToLower(input))
}

func (a *App) excludePathMatching(input string) {
	//example: VAULT/CODE/.git/objects/ or VAULT/CODE/Thumbs.db
	input = a.formatExcludePath(input)
	if !contains(excludedPathsMatching, input) {
		excludedPathsMatching = append(excludedPathsMatching, input)
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

func contains(slice []string, str string) bool {
	for _, v := range slice {
		if v == str {
			return true
		}
	}
	return false
}
