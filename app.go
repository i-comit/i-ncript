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

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type App struct {
	ctx context.Context
}

func NewApp() *App {
	return &App{}
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
	// return "Login successful"
}

func (a *App) ResizeWindow(width int, height int) {
	if a.ctx != nil {
		runtime.WindowSetSize(a.ctx, width, height)
	}
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

func (a *App) GetUsername(username string) string {
	// fmt.Printf("Username: %s, Password: %s\n", username, password)
	return fmt.Sprintf("username %s", username)
}
func (a *App) GetPassword(password string) string {
	// fmt.Printf("Username: %s, Password: %s\n", username, password)
	return fmt.Sprintf("password %s", password)
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

// Greet returns a greeting for the given name
