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

func (g *Getters) getDiskSpace(totalOrFreeSpace bool) (uint64, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return 0, fmt.Errorf("error getting current working directory: %w", err)
	}
	var root = cwd[:3] // Windows format, e.g., C:\
	rootPtr, err := windows.UTF16PtrFromString(root)
	if err != nil {
		return 0, fmt.Errorf("error converting string to UTF-16 pointer: %w", err)
	}
	var bytes uint64
	if totalOrFreeSpace { // True for total space, False for free space
		err = windows.GetDiskFreeSpaceEx(rootPtr, nil, &bytes, nil)
		if err != nil {
			return 0, fmt.Errorf("error getting total disk space: %w", err)
		}
	} else {
		err = windows.GetDiskFreeSpaceEx(rootPtr, &bytes, nil, nil)
		if err != nil {
			return 0, fmt.Errorf("error getting free disk space: %w", err)
		}
	}
	return bytes, nil
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
