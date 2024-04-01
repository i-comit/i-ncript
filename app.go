package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"time"

	"encoding/json"

	"github.com/fsnotify/fsnotify"
	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	ctx         context.Context
	directories []string
	done        chan bool
	watcher     *fsnotify.Watcher
}

func NewApp() *App {
	return &App{
		directories: []string{"VAULT", "N-BOX", "O-BOX"},
	}
}

func (a *App) startup(ctx context.Context) {
	a.ctx = ctx
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

// func (b *App) shutdown(ctx context.Context) {
// }
func (a *App) Login(username string, password string) (int, error) {
	if username == "" || password == "" {
		fmt.Println("Username or password is empty")
		return 0, nil
	}
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get CWD: %s", err)
	}
	filePath := filepath.Join(cwd, keyFileName)
	var credentials = username + password
	var loginStat int
	loginStat = checkCredentials(credentials)

	switch loginStat {
	case 0: //Key file does not exist
		file, err := os.Create(filePath)
		if err != nil {
			log.Fatalf("Failed to create file: %s", err)
		}
		defer file.Close()
		a.grantAccessToApp(file, credentials)
	case 1: //File exists but is empty
		break
	case 2: //credentials match file hash
		if a.ctx != nil {
			a.ResizeWindow(_width*2, _height+15)
		}
		for i, dir := range a.directories {
			a.directories[i] = cwd + string(os.PathSeparator) + dir
		}
		err = createDirectories(a.directories...)
		if err != nil {
			log.Fatal(err)
		}
		tree, err := a.BuildDirectoryFileTree(0)
		if err != nil {
			log.Fatalf("Failed to write to file: %s", err)
		}
		printFileTree(tree, false)
	}
	return loginStat, nil
}

func (a *App) grantAccessToApp(file *os.File, credentials string) {
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get CWD: %s", err)
	}
	_hashedCredentials, err := hashCredentials(credentials)
	if err != nil {
		log.Fatalf("Failed to encrypt: %s", err)
	}
	_, err = file.WriteString(_hashedCredentials)
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
	}
	fmt.Printf("File created: %s", cwd+file.Name())
	hashedCredentials = []byte(_hashedCredentials)
	if a.ctx != nil {
		a.ResizeWindow(_width*2, _height+15)
	}
	for i, dir := range a.directories {
		a.directories[i] = cwd + string(os.PathSeparator) + dir
	}
	err = createDirectories(a.directories...)
	if err != nil {
		log.Fatal(err)
	}
	fileTree, err := a.BuildDirectoryFileTree(0)
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
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
	a.InterruptEncryption()
	os.Exit(0)
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

func printFileTree(fileTree *FileNode, print bool) error {
	if !print {
		return nil
	}
	data, err := json.MarshalIndent(fileTree, "", "  ")
	if err != nil {
		fmt.Println("Error marshaling tree to JSON:", err)
		return err
	}
	jsonStr := string(data)
	fmt.Println(jsonStr)
	return nil
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
	// if currentIndex == dirIndex {
	// 	fmt.Println("Directory already being watched.")
	// 	return // Directory is already being watched, do nothing
	// }
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
				if !debounceTimer.Stop() {
					select {
					case <-debounceTimer.C: // Try to drain the channel
					default:
					}
				}
				debounceTimer.Reset(delayDuration)
			case <-debounceTimer.C:
				fmt.Println("Handling event after debounce.")
				if a.ctx != nil {
					if !isInFileTask {
						fmt.Println("Emitting rebuildFileTree event...")
						// runtime.EventsOn(a.ctx, rebuildFileTree)
						runtime.EventsEmit(a.ctx, rebuildFileTree)
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
	s := fmt.Sprintf("now watching %s directory", filepath.Base(a.directories[lastDirIndex]))
	fmt.Println("\033[32m", s, "\033[0m")
	lastDirIndex = dirIndex // Update the current index
	<-a.done                // Keep the watcher goroutine alive
}
