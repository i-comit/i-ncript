package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"strings"
	"time"

	"encoding/json"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	ctx     context.Context
	getters *Getters
}

type Getters struct {
	ctx context.Context
}

func NewApp() *App {
	return &App{}
}

var directories = []string{"VAULT", "N-BOX", "O-BOX"}
var rootFolder = "i-ncript"
var encryptedUsername = ""
var encryptedPassword = ""

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
func (a *App) Login(username string, password string) (bool, error) {
	if username == "" || password == "" {
		log.Println("Username or password is empty")
		return false, nil
	}
	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get current working directory: %s", err)
	}
	keyFile := ".➫.🔑"
	filePath := filepath.Join(cwd, keyFile)

	file, err := os.Create(filePath)
	if err != nil {
		log.Fatalf("Failed to create file: %s", err)
	}
	defer file.Close()

	_encryptedUsername, err := encryptString([]byte(username))
	if err != nil {
		log.Fatalf("Failed to encrypt username: %s", err)
	}
	_encryptedPassword, err := encryptString([]byte(password))
	if err != nil {
		log.Fatalf("Failed to encrypt password: %s", err)
	}
	encryptedUsername = _encryptedUsername
	encryptedPassword = _encryptedPassword

	_, err = file.WriteString(encryptedUsername + "\n" + encryptedPassword + "\n")
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
	}

	log.Printf("File created: %s", filePath)
	if a.ctx != nil {
		a.ResizeWindow(_width*2, _height+25, false)
	}
	for i, dir := range directories {
		directories[i] = cwd + string(os.PathSeparator) + dir
	}

	err = createDirectories(directories...)
	if err != nil {
		log.Fatal(err)
	}

	tree, err := a.BuildDirectoryFileTree(0)
	if err != nil {
		fmt.Println("Error building file tree:", err)
		return false, err
	}

	data, err := json.MarshalIndent(tree, "", "  ")
	if err != nil {
		fmt.Println("Error marshaling tree to JSON:", err)
		return false, err
	}

	jsonStr := string(data)
	fmt.Println(jsonStr)
	return true, nil
}

func (a *App) ResizeWindow(width int, height int, recenter bool) {
	if a.ctx != nil {
		runtime.WindowSetSize(a.ctx, width, height)
		if recenter {
			runtime.WindowCenter(a.ctx)
		}
	}
}

func (a *App) MinimizeApp() {
	runtime.WindowMinimise(a.ctx)
}

func (a *App) CloseApp() {
	os.Exit(0)
}

type FileNode struct {
	RelPath  string      `json:"relPath"`
	Children []*FileNode `json:"children,omitempty"`
}

func (a *App) BuildDirectoryFileTree(dirIndex int) (*FileNode, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(directories) {
		dirIndex = len(directories) - 1
	}
	var rootDir = directories[dirIndex]
	rootDir = filepath.Clean(rootDir)
	// Initialize rootNode. It does not represent the rootDir itself but its contents.
	rootNode := &FileNode{RelPath: rootDir, Children: []*FileNode{}}
	err := filepath.Walk(rootDir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		path = filepath.Clean(path)
		if path == rootDir {
			fmt.Println("ROOT PATH " + path)
			return nil
		}

		relativePath, err := filepath.Rel(rootDir, path)
		if err != nil {
			return err
		}
		parts := strings.Split(relativePath, string(filepath.Separator))
		addPath(rootNode, parts, "")
		return nil
	})

	if err != nil {
		return nil, err
	}
	return rootNode, nil
}

func addPath(node *FileNode, parts []string, currentPath string) {
	if len(parts) == 0 {
		return
	}
	if currentPath != "" {
		currentPath += string(filepath.Separator) + parts[0]
	} else {
		currentPath = parts[0]
	}

	for _, child := range node.Children {
		if filepath.Base(child.RelPath) == parts[0] {
			// If the child matches the next part, recursively call addPath on the child.
			addPath(child, parts[1:], currentPath)
			return
		}
	}
	newNode := &FileNode{
		RelPath:  currentPath,
		Children: []*FileNode{},
	}
	node.Children = append(node.Children, newNode)
	addPath(newNode, parts[1:], currentPath)
}

// GETTER STRUCT
func (b *Getters) GetAppPath() (string, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return "", err
	}
	return cwd + string(filepath.Separator), nil
}
func (b *Getters) GetDirName() (bool, error) {
	path, err := os.Getwd()
	if err != nil {
		return false, err
	}
	dirName := filepath.Base(path)
	match := (dirName == rootFolder)
	return match, nil
}
func (b *Getters) GetDirectoryPath(dirIndex int) (string, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(directories) {
		dirIndex = len(directories) - 1
	}
	return directories[dirIndex] + string(filepath.Separator), nil
}

func (b *Getters) GetDirectoryFileCt(dirIndex int) (int, error) {
	fileCount := 0
	err := filepath.Walk(directories[dirIndex], func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			fileCount++
		}
		return nil
	})
	if err != nil {
		return 0, err
	}
	fmt.Printf("\033[33mdirectories[dirIndex]: %s %d \033[0m\n", directories[dirIndex], fileCount)
	return fileCount, nil
}

type FileProperties struct {
	ModifiedDate time.Time
	Size         int64
	FileType     string
}

func (b *Getters) GetFileProperties(filePath string) (FileProperties, error) {
	var props FileProperties
	fileInfo, err := os.Stat(filePath)
	// fmt.Println("\033[33mFilePath", filePath, "\033[0m")
	if err != nil {
		fmt.Println("\033[33mError accessing file:", err, "\033[0m") // This will print the error in yellow
		return props, err                                            // Return zero value of props and the error
	}
	props.ModifiedDate = fileInfo.ModTime()
	props.Size = fileInfo.Size()

	if fileInfo.IsDir() {
		props.FileType = "directory"
	} else {
		props.FileType = "file"
	}
	return props, nil
}

func (g *Getters) GetRootFolder() string {
	return rootFolder
}

func createDirectories(dirs ...string) error {
	for _, dir := range dirs {
		if _, err := os.Stat(dir); os.IsNotExist(err) {
			err := os.Mkdir(dir, 0755)
			if err != nil {
				return err
			}
		}
	}
	return nil
}
