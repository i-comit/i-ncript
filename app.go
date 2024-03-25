package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"os"
	"path/filepath"

	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	ctx context.Context
}

func NewApp() *App {
	return &App{}
}

var dirsToCreate = []string{"VAULT", "N-BOX", "O-BOX"}

func (a *App) GetAppPath() (string, error) {
	cwd, err := os.Getwd()
	if err != nil {
		return "", err
	}
	return cwd, nil
}

// WailsInit is called to pass the runtime to your application
func (a *App) startup(ctx context.Context) {
	a.ctx = ctx
}

func (b *App) shutdown(ctx context.Context) {}

func (a *App) Login(username string, password string) {
	if username == "" || password == "" {
		log.Println("Username or password is empty")
		return
	}

	cwd, err := os.Getwd()
	if err != nil {
		log.Fatalf("Failed to get current working directory: %s", err)
	}
	keyFile := "➫.🔑"
	filePath := filepath.Join(cwd, keyFile)

	file, err := os.Create(filePath)
	if err != nil {
		log.Fatalf("Failed to create file: %s", err)
	}
	defer file.Close()

	// Encrypt the username and password
	encryptedUsername, err := encrypt([]byte(username))
	if err != nil {
		log.Fatalf("Failed to encrypt username: %s", err)
	}
	encryptedPassword, err := encrypt([]byte(password))
	if err != nil {
		log.Fatalf("Failed to encrypt password: %s", err)
	}

	// Write the encrypted values to the file
	_, err = file.WriteString(encryptedUsername + "\n" + encryptedPassword + "\n")
	if err != nil {
		log.Fatalf("Failed to write to file: %s", err)
	}

	log.Printf("File created: %s", filePath)
	if a.ctx != nil {
		a.ResizeWindow(500, 155)
	}
	for i, dir := range dirsToCreate {
		dirsToCreate[i] = cwd + string(os.PathSeparator) + dir
	}

	err = createDirectories(dirsToCreate...)
	if err != nil {
		log.Fatal(err)
	}
	err = printFilesRecursively(dirsToCreate...)
	if err != nil {
		log.Fatal(err)
	}

	tree, err := buildFileTree(dirsToCreate[0])
	if err != nil {
		fmt.Println("Error building file tree:", err)
		return
	}
	treeJSON, err := json.MarshalIndent(tree, "", "  ")
	if err != nil {
		fmt.Println("Error marshalling tree to JSON:", err)
		return
	}
	fmt.Println(string(treeJSON))

}

func (a *App) GetDirectoryStructure() (*Node, error) {
	return buildFileTree(dirsToCreate[0])
}

func (a *App) ResizeWindow(width int, height int) {
	if a.ctx != nil {
		runtime.WindowSetSize(a.ctx, width, height)
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

func (a *App) Greet(name string) string {
	return fmt.Sprintf("Hello %s, It's show time!", name)
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

type Node struct {
	Label    string  `json:"label"`
	Children []*Node `json:"children,omitempty"`
}

func buildFileTree(rootDir string) (*Node, error) {
	rootNode := &Node{Label: filepath.Base(rootDir)}
	err := filepath.Walk(rootDir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		// Skip the root directory since it's already added as the rootNode
		if path == rootDir {
			return nil
		}
		relativePath, err := filepath.Rel(rootDir, path)
		if err != nil {
			return err
		}
		parts := filepath.SplitList(relativePath)
		currentNode := rootNode
		for _, part := range parts {
			found := false
			for _, child := range currentNode.Children {
				if child.Label == part {
					currentNode = child
					found = true
					break
				}
			}
			if !found {
				newNode := &Node{Label: part}
				currentNode.Children = append(currentNode.Children, newNode)
				currentNode = newNode
			}
		}
		return nil
	})
	if err != nil {
		return nil, err
	}
	return rootNode, nil
}
