//go:build windows
// +build windows

package main

import (
	"fmt"
	"os"

	"golang.org/x/sys/windows"
)

func (g *Getters) GetRootDiskSpace() (uint64, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return 0, fmt.Errorf("error getting current working directory: %w", err)
	}

	var root = cwd[:3] // Windows format, e.g., C:\
	fmt.Println("Root windows" + root)
	// Convert root path to UTF-16 pointer as required by the Windows API
	rootPtr, err := windows.UTF16PtrFromString(root)
	if err != nil {
		return 0, fmt.Errorf("error converting string to UTF-16 pointer: %w", err)
	}
	var freeBytes uint64 // Changed from int64 to uint64
	// Retrieve free disk space
	err = windows.GetDiskFreeSpaceEx(rootPtr, nil, nil, &freeBytes)
	if err != nil {
		return 0, fmt.Errorf("error getting disk space: %w", err)
	}
	return freeBytes, nil
}
