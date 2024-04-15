package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"time"

	"github.com/fsnotify/fsnotify"
	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	getters         *Getters
	ctx             context.Context
	directories     []string
	hotFilerEnabled bool
	done            chan bool
	watcher         *fsnotify.Watcher
	lastFilePath    string
	cwd             string
}

func NewApp() *App {
	return &App{
		getters:         &Getters{},
		directories:     []string{"VAULT", "M-BOX"},
		hotFilerEnabled: false,
	}
}

func (a *App) startup(ctx context.Context) {
	a.ctx = ctx
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get CWD: %s", err)
	}
	a.cwd = cwd
	fmt.Println("\033[32mCWD: ", a.cwd, "\033[0m")
	driveSize, err := a.getters.GetRootDiskSpace()
	if err != nil {
		fmt.Println("Error getting disk space:", err)
		return
	}
	fmt.Printf("Available disk space: %s\n", formatDirSize(int64(driveSize)))

	appDirSize, err := a.getters.GetTotalDirSize(cwd)
	if err != nil {
		fmt.Printf("dir size err %s", err)
	}
	fmt.Printf("Dir size %d\n", appDirSize)
}

func (a *App) InitializeRootFolder() error {
	executablePath, err := os.Executable()
	if err != nil {
		return err
	}
	executableDir := filepath.Dir(executablePath) // Directory of the executable
	newFolderPath := filepath.Join(executableDir, rootFolder)

	if _, err := os.Stat(newFolderPath); os.IsNotExist(err) {
		err = os.MkdirAll(newFolderPath, os.ModePerm)
		if err != nil {
			return err
		}
	} else if err != nil {
		return err
	}

	newFilePath := filepath.Join(newFolderPath, filepath.Base(executablePath))

	if _, err := os.Stat(newFilePath); err == nil {
		return nil // or return an appropriate message/error if needed
	} else if !os.IsNotExist(err) {
		return err
	}
	return nil
}

func (a *App) InterruptFileTask() error {
	if a.lastFilePath != "" {
		// Attempt to open the last file
		file, err := os.OpenFile(a.lastFilePath, os.O_RDWR|os.O_APPEND|os.O_CREATE, 0666)
		if err != nil {
			log.Printf("failed to open last file: %v", err)
			// Don't return here, just log the error
		} else {
			// If opening succeeds, close the file immediately
			file.Close()
			fmt.Println("\033[31minterrupted file task: ", filepath.Base(a.lastFilePath), "\033[0m")
			// Attempt to remove the file
			if err := os.Remove(a.lastFilePath); err != nil {
				fmt.Printf("last file remove failed %s", err)
				// Log the error but continue to close the interrupt channel
			}
		}
		a.lastFilePath = ""
	}
	select {
	case <-interrupt:
		// Channel already closed or has been read from
		return nil
	default:
		close(interrupt)
	}
	return nil
}

func (a *App) Login(username, password string) (int, error) {
	if username == "" || password == "" {
		fmt.Println("Username or password is empty")
		return -1, nil
	}
	filePath := filepath.Join(a.cwd, keyFileName)
	var loginStat int

	_hashedUsername, err := hashString(username)
	if err != nil {
		log.Printf("Failed to hash username %s", err)
		return -1, err
	}
	hashedUsername = _hashedUsername
	var shuffledCredentials = shuffleStrings(_hashedUsername, password)
	_hashedCredentials, err := hashString(shuffledCredentials)
	hashedCredentials = []byte(_hashedCredentials)

	loginStat = checkCredentials(_hashedCredentials)
	if err != nil {
		log.Fatalf("Failed to hash credentials: %s", err)
	}

	switch loginStat {
	case 0: //Key file does not exist
		file, err := os.Create(filePath)
		if err != nil {
			log.Fatalf("Failed to create file: %s", err)
		}
		defer file.Close()
		saveHashedCredentials(_hashedCredentials)
		a.grantAccessToApp()
	case 1: //File exists but is empty
		break
	case 2: //credentials match file hash
		a.grantAccessToApp()
	}
	return loginStat, nil
}

func (a *App) grantAccessToApp() {
	if a.ctx != nil {
		a.ResizeWindow(_width*2, _height)
	}
	for i, dir := range a.directories {
		a.directories[i] = a.cwd + string(os.PathSeparator) + dir
	}
	err := createDirectories(a.directories...)
	if err != nil {
		log.Fatal(err)
	}
	fileTree, err := a.BuildDirectoryFileTree(0)
	if err != nil {
		log.Fatalf("Failed to build fileTree: %s", err)
	}
	printFileTree(fileTree, true)
}

func (a *App) ResizeWindow(width int, height int) {
	if a.ctx != nil {
		runtime.WindowSetSize(a.ctx, width, height)
	}
}

func (a *App) MinimizeApp() {
	runtime.WindowMinimise(a.ctx)
}

func (a *App) CloseApp() {
	if a.lastFilePath != "" {
		a.InterruptFileTask()
	}
	os.Exit(0)
}

func (a *App) SetIsHotFilerEnabled(b bool) {
	// fmt.Printf("hot Filer set %s", b)
	a.hotFilerEnabled = b
}

func (a *App) SetIsInFileTask(b bool) bool {
	isInFileTask = b
	if isInFileTask {
		a.closeDirectoryWatcher()
	} else {
		a.DirectoryWatcher(lastDirIndex)
	}
	return isInFileTask
}

func MoveFiles(srcPaths []string, destDir string) error {
	for _, srcPath := range srcPaths {
		filename := filepath.Base(srcPath)
		destPath := filepath.Join(destDir, filename)

		// Perform the move operation
		err := os.Rename(srcPath, destPath)
		if err != nil {
			return fmt.Errorf("failed to move %s to %s: %w", srcPath, destPath, err)
		}
	}
	return nil
}

func (a *App) closeDirectoryWatcher() {
	if a.watcher != nil {
		// Remove the current directory from being watched if applicable
		if lastDirIndex >= 0 {
			err := a.watcher.Remove(a.directories[lastDirIndex])
			if err != nil {
				fmt.Println("\033[31m", "Failed to remove directory from watcher:", "\033[0m")
			}
		}
		s := fmt.Sprintf("stopped watching %s directory", filepath.Base(a.directories[lastDirIndex]))
		fmt.Println("\033[33m", s, "\033[0m")
		a.watcher.Close() // Close the current watcher to clean up resources
	}
}

func (a *App) resetDirectoryWatcher(dirIndex int) {
	var err error
	a.watcher, err = fsnotify.NewWatcher()
	if err != nil {
		log.Println("Failed to create watcher:", err)
		return
	}

	a.done = make(chan bool)
	lastDirIndex = dirIndex // Initialize with an invalid index
}

func (a *App) DirectoryWatcher(dirIndex int) {
	a.closeDirectoryWatcher()
	a.resetDirectoryWatcher(dirIndex)

	delayDuration := time.Second
	debounceTimer := time.NewTimer(delayDuration)
	if !debounceTimer.Stop() {
		<-debounceTimer.C // Drain the timer if it fired before being stopped
	}

	go func() {
		for {
			select {
			case event, ok := <-a.watcher.Events:
				if !ok {
					return
				}
				fmt.Printf("DirectoryWatcherEvent: %s\n", event)
				switch {
				case event.Op&fsnotify.Write == fsnotify.Write:
					runtime.EventsEmit(a.ctx, refreshDirSize)
				case event.Op&fsnotify.Remove == fsnotify.Remove:
					runtime.EventsEmit(a.ctx, refreshDirSize)
				}
				if !debounceTimer.Stop() {
					select {
					case <-debounceTimer.C: // Try to drain the channel
					default:
					}
				}
				debounceTimer.Reset(delayDuration)
			case <-debounceTimer.C:
				fmt.Println("Handling event after debounce.")
				if !isInFileTask {
					fmt.Println("Emitting rebuildFileTree event")
					runtime.EventsEmit(a.ctx, rebuildFileTree)
					if a.hotFilerEnabled {
						a.EncryptFilesInDir(0)
					}
				}

			case err, ok := <-a.watcher.Errors:
				if !ok {
					return
				}
				fmt.Println("Error:", err)
			}
		}
	}()
	err := a.watcher.Add(a.directories[dirIndex])
	if err != nil {
		log.Fatal(err)
	}
	s := fmt.Sprintf("began watching %s directory", filepath.Base(a.directories[lastDirIndex]))
	fmt.Println("\033[32m", s, "\033[0m")
	lastDirIndex = dirIndex // Update the current index
	<-a.done                // Keep the watcher goroutine alive
}
