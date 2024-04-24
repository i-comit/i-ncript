package main

import (
	"fmt"
	"path/filepath"
	"strings"
)

// Wild cards may be used:   	* means zero or more characters
// 								? represents exactly one character
// 								?* matches one or more characters

func excludeAllFoldersMatching(input string) {
}

func excludeAllFilesMatching(input string) {

}

// func excludeAllInEndpathMatching(input string) map[string]bool {
// 	//example:VAULT/MISC/*
// 	// sep := string(filepath.Separator)
// 	// Trim the wildcard at the end and clean the path
// 	cleanInput := filepath.Clean(strings.TrimSuffix(input, "*"))
// 	exclusionMap := make(map[string]bool)
// 	exclusionMap[cleanInput] = true
// 	return exclusionMap

// }

func (a *App) excludeAllInEndpathMatching(input string) string {
	input = strings.TrimSuffix(input, "*") // Remove wildcard suffix first
	input = filepath.Clean(input)          // Clean the input path to normalize it
	if !strings.HasPrefix(input, a.cwd) {
		input = filepath.Join(a.cwd, input)
	}
	fmt.Println("clean input: ", input) // Print the final path to debug
	return input
}

func excludeAllFilesInFolderMatching(input string) {

}
func excludeAllFileExtensionsMatching(input string) {

}

func excludeFolderMatching(input string) {

}

func excludeFileMatching(input string) {

}
