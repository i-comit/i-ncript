package main

import (
	"bufio"
	"context"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"io"
	"log"
	"path/filepath"
	"strings"
	"time"

	"os"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type Encrypt struct {
	ctx         context.Context
	directories []string // Your directories list
}

// EVENT consts
var fileProcessed = "fileProcessed"
var fileCt = "fileCount"
var addLogFile = "addLogFile"
var keyFileName = ".i-ncript.🔑"

// func (b *Encrypt) EncryptString(stringToEncrypt string) string {
// 	encryptedString, _ := hashCredentials(stringToEncrypt)
// 	return encryptedString
// }

func (a *App) EncryptFilesInDir(dirIndex int) (bool, error) {
	filePaths, err := getFilesRecursively(a.directories[0])
	fmt.Println("\033[32mdirectories[0] ", a.directories[0], "\033[0m")
	if err != nil {
		return false, err
	}
	a.closeDirectoryWatcher()
	var fileIter = 0
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
			runtime.EventsEmit(a.ctx, addLogFile, filePath)
		}
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.reverseProgress(true, len(filePaths))
			return false, err
		}
	} else {
		return true, err
	}
	return false, err
}

func (a *App) DecryptFilesInDir(dirIndex int) error {
	filePaths, err := getFilesRecursively(a.directories[0])
	fmt.Println("\033[32mdirectories[0] ", a.directories[0], "\033[0m")
	if err != nil {
		return err
	}
	a.closeDirectoryWatcher()
	var fileIter = 0
	for i, filePath := range filePaths {

		if !strings.HasSuffix(filePath, ".enc") {
			continue
		}
		decryptFile, err := decryptFile(filePath)
		if err != nil {
			return err
		}

		defer decryptFile.Close()
		fileIter++

		if a.ctx != nil {
			runtime.EventsEmit(a.ctx, fileProcessed, i+1)
			runtime.EventsEmit(a.ctx, addLogFile, filePath)
		}
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.reverseProgress(false, len(filePaths))
		}
	}
	return nil
}

func (a *App) reverseProgress(encrypt bool, files int) {
	runtime.EventsEmit(a.ctx, fileCt, 100)
	runtime.EventsEmit(a.ctx, rebuildFileTree)
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
	if encrypt {
		response := fmt.Sprintf("encrypted %d files.", files)
		runtime.EventsEmit(a.ctx, addLogFile, response)
	} else {
		response := fmt.Sprintf("decrypted %d files.", files)
		runtime.EventsEmit(a.ctx, addLogFile, response)
	}

	if a.ctx != nil {
		time.Sleep(time.Second)
		isInFileTask = false
		a.ResizeWindow(_width*2, _height+25, false)
		runtime.EventsEmit(a.ctx, fileProcessed, 0)
		runtime.EventsEmit(a.ctx, fileCt, 0)
		runtime.EventsOff(a.ctx, fileProcessed, fileCt, addLogFile, rebuildFileTree)
		a.SetIsInFileTask(false)
	}
}

func encryptFile(filePath string) (*os.File, error) {
	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, err
	}
	hashedKey := sha256.Sum256(hashedCredentials)
	key := hashedKey[:]
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
	defer encFile.Close() // Ensure the file is closed when the function returns

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

	hashedKey := sha256.Sum256(hashedCredentials)
	key := hashedKey[:]
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
	newFilePath := filePath[:len(filePath)-len(".enc")]
	// Create the new file for the decrypted data
	decFile, err := os.Create(newFilePath)
	if err != nil {
		return nil, err
	}
	defer decFile.Close()

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
	if err := os.Remove(filePath); err != nil {
		decFile.Close() // Best effort to close the decrypted file before returning error
		return nil, err
	}
	return decFile, nil
}

func checkCredentials(stringToCheck string) int {
	cwd, err := os.Getwd()
	if err != nil {
		fmt.Printf("Failed to get current working directory: %s", err)
		return -1
	}
	keyFilePath := filepath.Join(cwd, keyFileName)
	fmt.Println("path to keyFile " + keyFilePath)

	file, err := os.Open(keyFilePath)
	if err != nil {
		fmt.Println("Key file doesn't exist", err)
		return 0
	}
	defer file.Close()

	reader := bufio.NewReader(file)
	line, isPrefix, err := reader.ReadLine()
	if err != nil {
		fmt.Println("Error reading line:", err)
		return 1
	}
	if line == nil {
		fmt.Println("Line is empty", err)
		return 1
	}
	// Handling the case where the line is longer than the buffer
	for isPrefix {
		var more []byte
		more, isPrefix, err = reader.ReadLine()
		if err != nil {
			fmt.Println("Error reading continuation of line:", err)
		}
		line = append(line, more...)
	}

	fmt.Println("First line of the file is:", string(line))
	hashedStringToCheck, err := hashCredentials(stringToCheck)
	if err != nil {
		log.Printf("Failed to hash credentials to check %s", err)
	}
	fmt.Println("hashedStringToCheck is:", hashedStringToCheck)
	if string(line) == hashedStringToCheck {
		log.Printf("String matched with key hash")
		return 2
	}
	return 3 //String does not match with key hash
}

func hashCredentials(stringToHash string) (string, error) {
	byteData := []byte(_uniqueID + stringToHash)
	hashedKey := sha256.Sum256(byteData)
	key := hashedKey[:] // Convert to slice
	// fmt.Printf("Hashed string (32 bytes): %x\n", key)

	block, err := aes.NewCipher(key)
	if err != nil {
		return "", err
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return "", err
	}

	// Nonce is usually critical for security in AES-GCM. Here, we omit it to meet the requirement,
	// Be aware this makes the encryption deterministic and less secure.
	// nonce := make([]byte, aesGCM.NonceSize())
	// if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
	// 	return "", err
	// }
	// encrypted := aesGCM.Seal(nonce, nonce, byteData, nil)

	encrypted := aesGCM.Seal(nil, make([]byte, aesGCM.NonceSize()), byteData, nil) // Using a zero nonce
	return hex.EncodeToString(encrypted), nil
}
