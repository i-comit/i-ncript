package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"os"
	"path/filepath"
	"strings"

	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/hex"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	ctx context.Context
}

func NewApp() *App {
	return &App{}
}

var directories = []string{"VAULT", "N-BOX", "O-BOX"}
var rootFolder = "i-ncript"

func (a *App) startup(ctx context.Context) {
	a.ctx = ctx
}

func (a *App) GetAppPath() (string, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return "", err
	}
	return cwd, nil
}
func (a *App) CheckDirName() (bool, error) {
	path, err := os.Getwd()
	if err != nil {
		return false, err
	}
	dirName := filepath.Base(path)
	match := (dirName == rootFolder)
	return match, nil
}
func (a *App) GetRootFolder() string {
	return rootFolder
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

func (a *App) Login(username string, password string) {
	if username == "" || password == "" {
		log.Println("Username or password is empty")
		return
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

	encryptedUsername, err := encrypt([]byte(username))
	if err != nil {
		log.Fatalf("Failed to encrypt username: %s", err)
	}
	encryptedPassword, err := encrypt([]byte(password))
	if err != nil {
		log.Fatalf("Failed to encrypt password: %s", err)
	}

	_, err = file.WriteString(encryptedUsername + "\n" + encryptedPassword + "\n")
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
	}

	log.Printf("File created: %s", filePath)
	if a.ctx != nil {
		a.ResizeWindow(500, 200, true)
	}
	for i, dir := range directories {
		directories[i] = cwd + string(os.PathSeparator) + dir
	}

	err = createDirectories(directories...)
	if err != nil {
		log.Fatal(err)
	}
	err = printFilesRecursively(directories...)
	if err != nil {
		log.Fatal(err)
	}

	// tree, err := buildFileTree(directories[0])
	// if err != nil {
	// 	fmt.Println("Error building file tree:", err)
	// 	return
	// }

	// data, err := json.MarshalIndent(tree, "", "  ")
	// if err != nil {
	// 	fmt.Println("Error marshaling tree to JSON:", err)
	// 	return
	// }

	// jsonStr := string(data)
	// fmt.Println(jsonStr)
}

func (a *App) GetDirectoryStructure(dirIndex int) (*Node, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(directories) {
		dirIndex = len(directories) - 1
	}
	tree, err := buildFileTree(directories[dirIndex])
	if err != nil {
		fmt.Println("Error building file tree:", err)
	}
	return tree, nil
}

func (a *App) ResizeWindow(width int, height int, recenter bool) {
	// center := false
	// if len(recenter) > 0 {
	// 	center = recenter[0]
	// }
	if a.ctx != nil {
		runtime.WindowSetSize(a.ctx, width, height)
		if recenter {
			runtime.WindowCenter(a.ctx)
		}
	}
}

func (a *App) EncryptString(stringToEncrypt string) string {
	encryptedString, _ := encrypt([]byte(stringToEncrypt))
	return encryptedString
}

func encrypt(data []byte) (string, error) {
	key := []byte("your-32-byte-long-aes-key-here..")

	block, err := aes.NewCipher(key)
	if err != nil {
		return "", err
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return "", err
	}

	nonce := make([]byte, aesGCM.NonceSize())
	if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
		return "", err
	}

	encrypted := aesGCM.Seal(nonce, nonce, data, nil)
	return hex.EncodeToString(encrypted), nil
}

func (a *App) MinimizeApp() {
	runtime.WindowMinimise(a.ctx)
}

func (a *App) CloseApp() {
	os.Exit(0)
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

func printFilesRecursively(dirs ...string) error {
	for _, dir := range dirs {
		err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
			if err != nil {
				return err
			}
			fmt.Println(path)
			return nil
		})
		if err != nil {
			return err
		}
	}
	return nil
}

type Logger struct{}

func (l *Logger) LogMessage(message string) {
	fmt.Println("Frontend says:", message)
}

type Node struct {
	Label    string  `json:"label"`
	Children []*Node `json:"children,omitempty"`
}

// Add or find a child node recursively
func addPath(node *Node, parts []string) {
	if len(parts) == 0 {
		return
	}

	for _, child := range node.Children {
		if child.Label == parts[0] {
			addPath(child, parts[1:])
			return
		}
	}

	newNode := &Node{Label: parts[0]}
	node.Children = append(node.Children, newNode)
	addPath(newNode, parts[1:])
}

func buildFileTree(rootDir string) (*Node, error) {
	rootDir = filepath.Clean(rootDir)
	// Initialize rootNode. It does not represent the rootDir itself but its contents.
	rootNode := &Node{Label: filepath.Base(rootDir), Children: []*Node{}}
	err := filepath.Walk(rootDir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		path = filepath.Clean(path)
		if path == rootDir {
			return nil
		}

		relativePath, err := filepath.Rel(rootDir, path)
		if err != nil {
			return err
		}
		// Split the relative path into parts
		parts := strings.Split(relativePath, string(filepath.Separator))
		addPath(rootNode, parts)
		return nil
	})

	if err != nil {
		return nil, err
	}
	return rootNode, nil
}
