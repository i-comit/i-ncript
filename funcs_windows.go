//go:build windows
// +build windows

package main

import (
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"syscall"

	"golang.org/x/sys/windows"
)

const _height = 145

func (g *Getters) GetRootDiskSpace() (uint64, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return 0, fmt.Errorf("error getting current working directory: %w", err)
	}
	var root = cwd[:3] // Windows format, e.g., C:\
	// Convert root path to UTF-16 pointer as required by the Windows API
	rootPtr, err := windows.UTF16PtrFromString(root)
	if err != nil {
		return 0, fmt.Errorf("error converting string to UTF-16 pointer: %w", err)
	}
	var freeBytes uint64 // Changed from int64 to uint64
	err = windows.GetDiskFreeSpaceEx(rootPtr, nil, nil, &freeBytes)
	if err != nil {
		return 0, fmt.Errorf("error getting disk space: %w", err)
	}
	return freeBytes, nil
}

func hideFile(filename string) error {
	path, err := syscall.UTF16PtrFromString(filename)
	if err != nil {
		return err
	}
	return syscall.SetFileAttributes(path, syscall.FILE_ATTRIBUTE_HIDDEN)
}

func replaceSeparator(filePath string) string {
	return strings.ReplaceAll(filePath, "/", string(filepath.Separator))
}
