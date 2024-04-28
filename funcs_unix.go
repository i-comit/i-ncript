//go:build linux || darwin
// +build linux darwin

package main

import (
	"fmt"
	"path/filepath"
	"strings"

	"golang.org/x/sys/unix"
)

func (g *Getters) getDiskSpace(totalOrFreeSpace bool) (int64, error) {
	var root = "/" // UNIX-like, simplified assumption
	var stat unix.Statfs_t
	if err := unix.Statfs(root, &stat); err != nil {
		return 0, fmt.Errorf("failed to get filesystem stats: %w", err)
	}
	var bytes uint64
	if totalOrFreeSpace { // True for total space, False for available space
		bytes = stat.Blocks * uint64(stat.Bsize) // Total bytes
	} else {
		bytes = stat.Bavail * uint64(stat.Bsize) // Available bytes
	}
	return int64(bytes), nil
}

func hideFile(filename string) error {
	return nil
}

func replaceSeparator(filePath string) string {
	return strings.ReplaceAll(filePath, "\\", string(filepath.Separator))
}
