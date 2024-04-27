package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"
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
	cwd             string
	fileTaskSize    int64
	lastFilePath    string
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
		runtime.LogFatal(a.ctx, fmt.Sprintf("Failed to get CWD: %v", err))
	}
	a.cwd = cwd
	runtime.LogInfo(a.ctx, "CWD "+a.cwd)
}

func (a *App) InitializeRootFolder() error {
	executablePath, err := os.Executable()
	if err != nil {
		return err
	}
	executableDir := filepath.Dir(executablePath) // Directory of the executable
	newFolderPath := filepath.Join(executableDir, strings.ToLower(rootFolder))

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
		file, err := os.OpenFile(a.lastFilePath, os.O_RDWR|os.O_APPEND|os.O_CREATE, 0666)
		if err != nil {
			runtime.LogWarning(a.ctx, fmt.Sprintf("Last file null or error: %v", err))
			// Don't return here, just log the error
		} else {
			// If opening succeeds, close the file immediately
			file.Close()
			runtime.LogError(a.ctx, "interrupted file task: "+filepath.Base(a.lastFilePath))
			// Attempt to remove the file
			if err := os.Remove(a.lastFilePath); err != nil {
				runtime.LogError(a.ctx, fmt.Sprintf("Failed to remove last file: %v", err))
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
		return -1, nil
	}
	filePath := filepath.Join(a.cwd, keyFileName)
	var loginStat int

	_hashedUsername, err := hashString(username)
	if err != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to hash username: %v", err))
		return -1, err
	}
	hashedUsername = _hashedUsername
	var shuffledCredentials = shuffleStrings(_hashedUsername, password)
	_hashedCredentials, err := hashString(shuffledCredentials)
	hashedCredentials = []byte(_hashedCredentials)

	loginStat = checkCredentials(_hashedCredentials)
	if err != nil {
		runtime.LogFatal(a.ctx, fmt.Sprintf("Failed to hash credentials: %v", err))
	}

	switch loginStat {
	case 0: //Key file does not exist
		file, err := os.Create(filePath)
		if err != nil {
			runtime.LogFatal(a.ctx, fmt.Sprintf("Failed to create file: %v", err))
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
	for i, dir := range a.directories {
		a.directories[i] = a.cwd + string(os.PathSeparator) + dir
	}
	err := createDirectories(a.directories...)
	if err != nil {
		log.Fatal(err)
	}
	fileTree, err := a.BuildDirectoryFileTree(0)
	if err != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to build fileTree: %v", err))
	}
	printFileTree(fileTree, false)
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

			return err
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
		runtime.LogWarning(a.ctx, "stopped watching "+filepath.Base(a.directories[lastDirIndex]))
		a.watcher.Close() // Close the current watcher to clean up resources
	}
}

func (a *App) resetDirectoryWatcher(dirIndex int) {
	var err error
	a.watcher, err = fsnotify.NewWatcher()
	if err != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to create watcher: %v", err))
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
				runtime.LogDebug(a.ctx, "Handling event after debounce")
				if !isInFileTask {
					runtime.LogInfo(a.ctx, "Emitting rebuildFileTree event")
					runtime.EventsEmit(a.ctx, rebuildFileTree)
					if a.hotFilerEnabled {
						a.EncryptFilesInDir(0)
					}
				}

			case err, ok := <-a.watcher.Errors:
				if !ok {
					return
				}
				runtime.LogError(a.ctx, fmt.Sprintf("Watcher error: %v", err))
			}
		}
	}()
	err := a.watcher.Add(a.directories[dirIndex])
	if err != nil {
		log.Fatal(err)
	}
	runtime.LogDebug(a.ctx, "began watching "+filepath.Base(a.directories[lastDirIndex]))
	lastDirIndex = dirIndex // Update the current index
	<-a.done                // Keep the watcher goroutine alive
}
