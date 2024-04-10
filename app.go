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
	ctx             context.Context
	directories     []string
	hotFilerEnabled bool
	done            chan bool
	watcher         *fsnotify.Watcher
	lastFilePath    string
}

func NewApp() *App {
	return &App{
		directories:     []string{"VAULT", "M-BOX"},
		hotFilerEnabled: false,
	}
}

func (a *App) startup(ctx context.Context) {
	a.ctx = ctx
	log.Print("app has started")
	keyFilePath := getKeyFilePath()
	if keyFilePath != "" {

	}
	combinedBytes1 := insertBytesProcedurally([]byte("1234123312213d5678"), []byte("abcd"))
	fmt.Println("Combined string 1:", string(combinedBytes1)) //1a23b45c67d8
	// fmt.Println("Match result 1:", matchBytesProcedurally(combinedBytes1, []byte("abcd")))               // Expected: true
	// fmt.Println("Match result 2:", matchBytesProcedurally(combinedBytes1, []byte("abce")))               // Expected: false
	// fmt.Println("Match result 3:", matchBytesProcedurally(combinedBytes1, []byte("1234123312213d5678"))) // Expected: false

	combinedBytes2 := insertBytesProcedurally([]byte("2138nyhon21;o23xasdadsds"), []byte("fjnhdsa5d"))
	fmt.Println("Combined string 2:", string(combinedBytes2)) //a12b34c56d78
	// fmt.Println("Match result 4:", matchBytesProcedurally(combinedBytes2, []byte("2138nyhon21;o23xasdadsds"))) // Expected: true
	// fmt.Println("Match result 5:", matchBytesProcedurally(combinedBytes2, []byte("fjnhdsa5d")))                // Expected: false
	// fmt.Println("Match result 6:", matchBytesProcedurally(combinedBytes2, []byte("abcd")))                     // Expected: false

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

func (a *App) Login(username, password string) (int, error) {
	if username == "" || password == "" {
		fmt.Println("Username or password is empty")
		return -1, nil
	}
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get CWD: %s", err)
	}
	filePath := filepath.Join(cwd, keyFileName)
	var loginStat int

	_hashedUsername, err := hashString(username)
	if err != nil {
		log.Printf("Failed to hash username %s", err)
		return -1, err
	}
	hashedUsername = _hashedUsername
	var shuffledCredentials = shuffleStrings(_hashedUsername, password)
	loginStat = checkCredentials(shuffledCredentials)
	_hashedCredentials, err := hashString(shuffledCredentials)
	if err != nil {
		log.Fatalf("Failed to hash credentials: %s", err)
	}
	hashedCredentials = []byte(_hashedCredentials)

	switch loginStat {
	case 0: //Key file does not exist
		file, err := os.Create(filePath)
		if err != nil {
			log.Fatalf("Failed to create file: %s", err)
		}
		defer file.Close()
		a.grantAccessToApp(file, _hashedCredentials)
	case 1: //File exists but is empty
		break
	case 2: //credentials match file hash
		if a.ctx != nil {
			a.ResizeWindow(_width*2, _height)
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

func (a *App) grantAccessToApp(file *os.File, _hashedCredentials string) {
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get CWD: %s", err)
	}
	_, err = file.WriteString(_hashedCredentials)
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
	}
	if a.ctx != nil {
		a.ResizeWindow(_width*2, _height)
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
		a.InterruptEncryption()
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
						runtime.EventsEmit(a.ctx, rebuildFileTree)
						if a.hotFilerEnabled {
							a.EncryptFilesInDir(0)
						}
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
