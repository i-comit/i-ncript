package main

import (
	"fmt"
	// "os"
	"path/filepath"
	"strings"
)

// Wild cards may be used:   	* means zero or more characters
// 								? represents exactly one character
// 								?* matches one or more characters

var excludedDirsStartingIn []string
var excludedDirsContaining []string
var excludedDirsMatching []string

var excludedFilesEndingIn []string

func (a *App) checkExcludedDirsAgainstPath(path string) bool {
	// pathInfo, err := os.Stat(path)
	// if err != nil {
	// 	fmt.Println("Error accessing path:", err)
	// 	return true
	// }
	// if pathInfo.IsDir() {
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

	for _, element := range excludedFilesEndingIn {
		if strings.HasSuffix(lowercasePath, element) {
			fmt.Println("found excluded file ending in " + element)
			return true
		}
	}

	// } else {
	// 	return true
	// }
	return false

}

func (f *FileUtils) AddInputToFilterTemplate(input string) {
	// Check if input starts with '*/', ends with '/*'
	if strings.HasPrefix(input, "*/") && strings.HasSuffix(input, "/*") {
		f.app.excludePathContaining(input)
		fmt.Println("excludePathContaining " + input)
		// Check if input starts with a folder name and ends with a folder name without wildcard '*'
	} else if !strings.Contains(input, "*") && strings.Contains(input, string(filepath.Separator)) {
		excludePathMatching(input)
		fmt.Println("excludePathMatching " + input)
		// Check if input starts with a folder name and ends with a folder name but contains wildcard '*'
	} else if strings.Contains(input, "*") && !strings.HasPrefix(input, "*") && !strings.HasSuffix(input, "*") {
		f.app.excludeInputStartingIn(input)
		fmt.Println("excludeInputStartingIn " + input)
		// Check if input starts with wildcard '*' and has no separator
	} else if strings.HasPrefix(input, "*") && !strings.Contains(input, string(filepath.Separator)) {
		f.app.excludeFileEndingIn(input)
		//Check if input starts with a folder name, ends with a
	}
	// else if !strings.HasPrefix(input, "*") && !strings.Contains(input, string(filepath.Separator)) {
	// 	f.app.excludeFileEndingIn(input)
	// }
}

// Folder filters
func (a *App) excludePathContaining(input string) {
	//example: */.git/objects/*
	input = strings.ReplaceAll(input, "*", "")
	input = replaceSeparator(input)
	fmt.Println("excluded dir containing: " + input)
	excludedDirsContaining = append(excludedDirsContaining, strings.ToLower(input))

}

func excludePathMatching(input string) {
	//example: VAULT/CODE/.git/objects/ or VAULT/CODE/Thumbs.db
	input = replaceSeparator(input)
	excludedDirsMatching = append(excludedDirsMatching, strings.ToLower(input))
}

func (a *App) excludeInputStartingIn(input string) string {
	//example: VAULT/MISC/* or VAULT/MISC/thumbs.txt*
	input = strings.TrimSuffix(input, "*")
	input = a.formatExcludePath(input)
	fmt.Println("exclude dirs starting in: ", input) // Print the final path to debug
	excludedDirsStartingIn = append(excludedDirsStartingIn, input)
	return input
}

func (a *App) excludePathStartingEndingIn(input string) {
	//example: VAULT/CODE/*/frontend/dist/ or VAULT/CODE/*/Filename.txt
}

func (a *App) excludeFileEndingIn(input string) {
	//example : *Thumbs.db || *.txt
	input = strings.TrimPrefix(input, "*")
	excludedFilesEndingIn = append(excludedFilesEndingIn, strings.ToLower(input))

}

func (a *App) formatExcludePath(input string) string {
	input = filepath.Clean(input) // Clean the input path to normalize it
	input = replaceSeparator(input)
	if !strings.HasPrefix(input, a.cwd) {
		input = filepath.Join(a.cwd, input) // Prepend the cwd only if it's not already part of the input
	}
	return strings.ToLower(input) // Return the possibly modified input
}

func isDirectory(path string) bool {
	cleanedPath := filepath.Clean(path)
	cleanedPath = replaceSeparator(cleanedPath)

	if len(cleanedPath) > 0 && strings.HasSuffix(cleanedPath, string(filepath.Separator)) {
		cleanedPath = cleanedPath[:len(cleanedPath)-1]
	}
	_, file := filepath.Split(cleanedPath)

	if strings.Contains(file, ".") && !strings.HasSuffix(file, ".") {
		return false // it's likely a file
	}
	return true
}
