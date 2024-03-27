package main

import (
	"context"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"io"
	"log"
	"strings"
	"time"

	// "io"
	// "log"
	"os"
	"path/filepath"

	"github.com/fsnotify/fsnotify"
	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type Encryptr struct {
	ctx context.Context
}

// EVENT consts
var fileProcessed = "fileProcessed"
var fileCt = "fileCount"

func (b *Encryptr) EncryptString(stringToEncrypt string) string {
	encryptedString, _ := encryptString([]byte(stringToEncrypt))
	return encryptedString
}

func (b *Encryptr) GetDecryptedFiles(dirIndex int) ([]string, error) {
	filePaths, err := getFilesRecursively(directories[dirIndex])
	if err != nil {
		return nil, err
	}
	var unencryptedFiles []string
	for _, filePath := range filePaths {
		if filepath.Ext(filePath) != ".enc" {
			unencryptedFiles = append(unencryptedFiles, filePath)
		}
	}
	// fmt.Printf("\033[32mUnencrypted files in %s: %v\033[0m\n", directories[dirIndex], unencryptedFiles)
	return unencryptedFiles, nil
}

func directoryWatcher() {
	watcher, err := fsnotify.NewWatcher()
	if err != nil {
		fmt.Println(err)
	}
	defer watcher.Close()

	done := make(chan bool)
	go func() {
		for {
			select {
			case event, ok := <-watcher.Events:
				if !ok {
					return
				}
				fmt.Printf("Event: %s\n", event)
				if event.Op&fsnotify.Write == fsnotify.Write {
					fmt.Printf("Modified file: %s\n", event.Name)
				} else if event.Op&fsnotify.Create == fsnotify.Create {
					fmt.Printf("Created file: %s\n", event.Name)
				} else if event.Op&fsnotify.Remove == fsnotify.Remove {
					fmt.Printf("Deleted file: %s\n", event.Name)
				}
				// Add more conditions here for other types of events.

				// Place your event handling logic here.
				// To ensure an event only executes after all file modifications are complete,
				// you might need to implement a delay or a way to 'debounce' the event
				// processing, so it waits for a pause in changes.

			case err, ok := <-watcher.Errors:
				if !ok {
					return
				}
				fmt.Println(err)
			}
		}
	}()

	// Replace the path below with the directory you want to watch
	err = watcher.Add("/path/to/directory")
	if err != nil {
		log.Fatal(err)
	}
	<-done // Keep the program alive
}

func (b *Encryptr) GetEncryptedFiles(dirIndex int) ([]string, error) {
	filePaths, err := getFilesRecursively(directories[dirIndex])
	if err != nil {
		return nil, err
	}
	var encryptedFiles []string
	for _, filePath := range filePaths {
		if filepath.Ext(filePath) == ".enc" {
			encryptedFiles = append(encryptedFiles, filePath)
		}
	}
	return encryptedFiles, nil
}

func getFilesRecursively(dirs ...string) ([]string, error) {
	var files []string
	for _, dir := range dirs {
		err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
			if err != nil {
				return err
			}
			if !info.IsDir() { // Ensure we're only appending files, not directories
				files = append(files, path)
			}
			return nil
		})
		if err != nil {
			return nil, err
		}
	}
	return files, nil
}

func (a *App) EncryptFilesInDir(dirIndex int) (bool, error) {
	filePaths, err := getFilesRecursively(directories[0])
	fmt.Println("\033[32mdirectories[0] ", directories[0], "\033[0m")
	if err != nil {
		return false, err
	}
	var fileIter = 0
	//
	for i, filePath := range filePaths {

		if strings.HasSuffix(filePath, ".enc") {
			continue
		}

		encryptedFile, err := encryptFile(filePath)
		if err != nil {
			continue
		}
		defer encryptedFile.Close()
		fileIter++
		if a.ctx != nil {
			runtime.EventsEmit(a.ctx, fileProcessed, i+1)
			// fmt.Println("\033[31mfileCount ", dirFileCt, "\033[0m")
		}
		// fmt.Println("Encrypted file created:", encryptedFile.Name())
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.reverseProgress()
			return false, err
		}
	} else {
		return true, err
	}
	return false, err
}

func (a *App) DecryptFilesInDir() error {
	filePaths, err := getFilesRecursively(directories[0])
	fmt.Println("\033[32mdirectories[0] ", directories[0], "\033[0m")
	if err != nil {
		return err
	}
	var fileIter = 0
	for i, filePath := range filePaths {
		if err != nil {
			return err
		}
		if !strings.HasSuffix(filePath, ".enc") {
			continue
		}
		// Encrypt the file content
		decryptFile, err := decryptFile(filePath)
		if err != nil {
			return err
		}

		defer decryptFile.Close()
		fileIter++

		if a.ctx != nil {
			runtime.EventsEmit(a.ctx, fileProcessed, i+1)
			fmt.Println("\033[31mfileCount ", i+1, "\033[0m")
		}
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.reverseProgress()
		}
	}
	return nil
}

func (a *App) reverseProgress() {
	runtime.EventsEmit(a.ctx, fileCt, 100)
	time.Sleep(1 * time.Second)
	done := make(chan bool) // Cr
	go func() {
		counter := 100
		for counter > 1 {
			counter-- // Decrement the counter
			if a.ctx != nil {
				runtime.EventsEmit(a.ctx, fileProcessed, counter)
			}
			time.Sleep(4 * time.Millisecond) // Wait for 0.2 seconds
		}
		done <- true // Signal that the loop is done
	}()
	<-done // Wait for the goroutine to signal it's done

	if a.ctx != nil {
		time.Sleep(900 * time.Millisecond)
		a.ResizeWindow(_width*2, _height+25, false)
		runtime.EventsEmit(a.ctx, fileProcessed, 0)
		runtime.EventsEmit(a.ctx, fileCt, 0)
		runtime.EventsOff(a.ctx, fileProcessed, fileCt)
	}
}

func encryptFile(filePath string) (*os.File, error) {
	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}

	key := []byte("your-32-byte-long-aes-key-here..")
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}

	nonce := make([]byte, aesGCM.NonceSize())
	if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
		return nil, err
	}

	encrypted := aesGCM.Seal(nonce, nonce, data, nil)
	newFilePath := filePath + ".enc"

	encFile, err := os.Create(newFilePath)
	if err != nil {
		return nil, err
	}
	if _, err := encFile.Write(encrypted); err != nil {
		encFile.Close()           // Attempt to close the file on error
		os.Remove(encFile.Name()) // Cleanup the file if write fails
		return nil, err
	}
	if _, err := encFile.Seek(0, 0); err != nil {
		encFile.Close()           // Attempt to close the file on error
		os.Remove(encFile.Name()) // Cleanup the file if seek fails
		return nil, err
	}

	// Delete the original file after successfully creating the encrypted version
	if err := os.Remove(filePath); err != nil {
		encFile.Close() // Best effort to close the encrypted file before returning error
		return nil, err
	}
	return encFile, nil
}

func decryptFile(filePath string) (*os.File, error) {

	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}

	key := []byte("your-32-byte-long-aes-key-here..")
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}

	nonceSize := aesGCM.NonceSize()
	if len(data) < nonceSize {
		return nil, err // Error handling: the data is shorter than the nonce size
	}

	nonce, ciphertext := data[:nonceSize], data[nonceSize:]
	decrypted, err := aesGCM.Open(nil, nonce, ciphertext, nil)
	if err != nil {
		return nil, err
	}

	// Remove the .enc extension to get the original file path
	newFilePath := filePath[:len(filePath)-len(".enc")]
	// Create the new file for the decrypted data
	decFile, err := os.Create(newFilePath)
	if err != nil {
		return nil, err
	}
	if _, err := decFile.Write(decrypted); err != nil {
		decFile.Close()           // Attempt to close the file on error
		os.Remove(decFile.Name()) // Cleanup the file if write fails
		return nil, err
	}
	if _, err := decFile.Seek(0, 0); err != nil {
		decFile.Close()           // Attempt to close the file on error
		os.Remove(decFile.Name()) // Cleanup the file if seek fails
		return nil, err
	}
	//  delete the original encrypted file
	if err := os.Remove(filePath); err != nil {
		decFile.Close() // Best effort to close the decrypted file before returning error
		return nil, err
	}
	return decFile, nil
}

func encryptString(data []byte) (string, error) {
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
