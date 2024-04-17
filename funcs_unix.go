//go:build linux || darwin
// +build linux darwin

package main

import (
	"fmt"

	"golang.org/x/sys/unix"
)

const _height = 120

func (g *Getters) GetRootDiskSpace() (int64, error) {
	// Extract the root path from the current working directory.
	var root = "/" // UNIX-like, simplified assumption
	var stat unix.Statfs_t
	if err := unix.Statfs(root, &stat); err != nil {
		return 0, fmt.Errorf("failed to get filesystem stats: %w", err)
	}
	availableBytes := stat.Bavail * uint64(stat.Bsize)
	return int64(availableBytes), nil
}

func hideFile(filename string) error {
	return nil
}
