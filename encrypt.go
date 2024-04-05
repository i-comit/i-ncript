package main

import (
	"bufio"
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
	"sync"
	"sync/atomic"
	"time"

	"os"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

func (a *App) EncryptFilesInDir(dirIndex int) (bool, error) {
	filePaths, err := getFilesRecursively(a.directories[dirIndex])
	fmt.Println("\033[32mdirectories[0] ", a.directories[dirIndex], "\033[0m")
	if err != nil {
		return false, err
	}
	a.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	cipherResult, err := a.encryptOrDecrypt(true, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) EncryptFilesInArr(filePaths []string) (bool, error) {
	a.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	cipherResult, err := a.encryptOrDecrypt(true, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) encryptOrDecrypt(encryptOrDecrypt bool, filePaths []string) (bool, error) {
	var fileIter = 0
	for i, filePath := range filePaths {
		select {
		case <-interrupt: // Check if there's an interrupt signal
			fmt.Printf("encryption interrupted")
			lastFilePath = filePath
			return false, nil
		default:
			if encryptOrDecrypt {
				if strings.HasSuffix(filePath, ".enc") {
					continue
				}
				cipherFile, err := a.encryptFile(filePath)
				if err != nil {
					fmt.Println("\033[31mcipher issue ", err, "\033[0m")
					continue
				}
				cipherFile.Close() // Close right after done to avoid deferred pileup
			} else {
				if !strings.HasSuffix(filePath, ".enc") {
					continue
				}
				cipherFile, err := a.decryptFile(filePath)
				if err != nil {
					fmt.Println("\033[31mcipher issue ", err, "\033[0m")
					continue
				}
				cipherFile.Close()
			}

			lastFilePath = filePath // Update lastFile after successful
			fileIter++
			if a.ctx != nil {
				runtime.EventsEmit(a.ctx, fileProcessed, i+1)
				runtime.EventsEmit(a.ctx, addLogFile, filePath)
			}
		}
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.reverseProgress(true, len(filePaths))
			return true, nil
		}
	} else {
		return false, fmt.Errorf("ciphered files == 0")
	}
	return true, nil
}

func (a *App) DecryptFilesInDir(dirIndex int) (bool, error) {
	filePaths, err := getFilesRecursively(a.directories[dirIndex])
	if err != nil {
		return false, err
	}
	a.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	cipherResult, err := a.encryptOrDecrypt(false, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) DecryptFilesInArr(filePaths []string) (bool, error) {
	a.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	cipherResult, err := a.encryptOrDecrypt(false, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) InterruptEncryption() {
	file, err := os.OpenFile(lastFilePath, os.O_RDWR|os.O_APPEND|os.O_CREATE, 0666)
	if err != nil {
		log.Printf("failed to open last file: %v", err)
	}
	defer file.Close()
	fmt.Println("\033[31minterrupted encryption: ", filepath.Base(lastFilePath), "\033[0m")
	if err := os.Remove(lastFilePath); err != nil {
		fmt.Printf("last file remove failed %s", err)
	}
	close(interrupt) // Closing the channel sends a signal to all receivers
	//Make sure to not run this method when interrupt is already closed; this will cause a panic
}

func (a *App) reverseProgress(encrypt bool, files int) {
	lastFilePath = ""
	// runtime.EventsEmit(a.ctx, totalFileCt, 100)
	time.Sleep(time.Millisecond * 400)
	// done := make(chan bool)
	// go func() {
	// 	counter := 100
	// 	for counter > 0 {
	// 		counter-- // Decrement the counter
	// 		if a.ctx != nil {
	// 			runtime.EventsEmit(a.ctx, fileProcessed, counter)
	// 		}
	// 		time.Sleep(2 * time.Millisecond) // Wait for 0.2 seconds
	// 	}
	// 	done <- true // Signal that the loop is done
	// }()
	// <-done
	if encrypt {
		response := fmt.Sprintf("encrypted %d files.", files)
		runtime.EventsEmit(a.ctx, addLogFile, response)
	} else {
		response := fmt.Sprintf("decrypted %d files.", files)
		runtime.EventsEmit(a.ctx, addLogFile, response)
	}

	if a.ctx != nil {
		runtime.EventsEmit(a.ctx, rebuildFileTree)
		a.ResizeWindow(_width*2, _height)
		runtime.EventsEmit(a.ctx, fileProcessed, 0)
		runtime.EventsEmit(a.ctx, totalFileCt, 0)
		runtime.EventsOff(a.ctx, fileProcessed, totalFileCt, addLogFile)
		a.SetIsInFileTask(false)
	}
}

func (a *App) encryptFile(filePath string) (*os.File, error) {
	aesGCM, data, err := initFileCipher(filePath)
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
	largeFileErr := a.checkLargeFileTicker(data, encrypted, encFile)

	if largeFileErr != nil {
		return nil, fmt.Errorf("failed to encrypt large File: %w", largeFileErr)
	}
	if err := os.Remove(filePath); err != nil {
		encFile.Close() // Best effort to close the encrypted file before returning error
		return nil, err
	}
	return encFile, nil
}

func (a *App) decryptFile(filePath string) (*os.File, error) {
	aesGCM, data, err := initFileCipher(filePath)
	if err != nil {
		return nil, err
	}
	nonceSize := aesGCM.NonceSize()

	if len(data) < nonceSize {
		return nil, fmt.Errorf("data too short")
	}
	nonce, ciphertext := data[:nonceSize], data[nonceSize:]
	decrypted, err := aesGCM.Open(nil, nonce, ciphertext, nil)

	if err != nil {
		return nil, err
	}
	newFilePath := filePath[:len(filePath)-len(".enc")]
	decFile, err := os.Create(newFilePath)
	if err != nil {
		return nil, err
	}
	defer decFile.Close()

	largeFileErr := a.checkLargeFileTicker(data, decrypted, decFile)
	if largeFileErr != nil {
		return nil, fmt.Errorf("failed to decrypt large File: %w", largeFileErr)
	}
	if err := os.Remove(filePath); err != nil {
		decFile.Close()
		return nil, err
	}
	return decFile, nil
}

func (a *App) checkLargeFileTicker(data []byte, cipherData []byte, cipherFile *os.File) error {
	done := make(chan struct{})
	var once sync.Once         // Ensure the done channel is closed only once
	var thresholdFileSize = 40 //  file size in MBs to trigger ticker
	interrupted := false       // Flag to track if an interrupt has occurred

	var writtenBytes int64 //Use atomic.Int64 w/ writtenBytes.Load() for 32bit systems
	if len(data) > thresholdFileSize*1024*1024 {
		go func() {
			ticker := time.NewTicker(10 * time.Millisecond)
			defer ticker.Stop()
			for {
				select {
				case <-interrupt: // Check if there's an interrupt signal
					fmt.Println("\033[31m", "large file interrupted "+cipherFile.Name(), "\033[0m")
					lastFilePath = cipherFile.Name()
					once.Do(func() { close(done) }) // Close done channel safely
					interrupted = true              // Set the interrupted flag
					return
				case <-ticker.C:
					percent := (float64(atomic.LoadInt64(&writtenBytes)) / float64(len(cipherData))) * 100
					percentInt := int(percent + 0.5) // Adds 0.5 before casting to round to nearest whole number
					// fmt.Printf("Percentage: %d%%\n", percentInt)
					runtime.EventsEmit(a.ctx, "largeFilePercent", percentInt)
				case <-done:
					return
				}
			}
		}()
	}
	chunkSize := 2 * 1024 // 2KB Chunk
	for i := 0; i < len(cipherData); i += chunkSize {
		if interrupted { // Check if an interrupt has occurred
			cipherFile.Close()
			os.Remove(cipherFile.Name())
			return fmt.Errorf("large file write interrupted: %s", cipherFile.Name())
		}
		end := i + chunkSize
		if end > len(cipherData) {
			end = len(cipherData)
		}
		n, err := cipherFile.Write(cipherData[i:end])
		if err != nil {
			return fmt.Errorf("failed to write file: %w", err)
		}
		atomic.AddInt64(&writtenBytes, int64(n))
		// writtenBytes.Add(int64(n)) // if writtenBytes atomic.Int64
	}
	once.Do(func() { close(done) }) // Ensure the done channel is closed
	if _, err := cipherFile.Seek(0, 0); err != nil {
		cipherFile.Close()
		os.Remove(cipherFile.Name())
		return err
	}
	runtime.EventsEmit(a.ctx, "largeFilePercent", 0)
	return nil
}

func initFileCipher(filePath string) (cipher.AEAD, []byte, error) {
	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, nil, err
	}
	hashedKey := sha256.Sum256(hashedCredentials)
	key := hashedKey[:]
	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, nil, err
	}
	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		fmt.Println("\033[31mGCM err ", err, "\033[0m")
		return nil, nil, err
	}
	return aesGCM, data, nil
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
	if err != nil || line == nil {
		fmt.Println("Error reading line:", err)
		return 1
	}
	// Handling the case where the line is longer than the buffer
	for isPrefix {
		var more []byte
		more, isPrefix, err = reader.ReadLine()
		if err != nil {
			fmt.Println("Error reading continuation of line:", err)
			return 1
		}
		line = append(line, more...)
	}

	hashedStringToCheck, err := hashCredentials(stringToCheck)
	if err != nil {
		log.Printf("Failed to hash credentials to check %s", err)
		return -1
	}
	if string(line) == hashedStringToCheck {
		log.Printf("String matched with key hash")
		return 2
	} else {
		log.Printf("String not matched with key hash")
		return 3
	}
}

func hashCredentials(stringToHash string) (string, error) {
	byteData := []byte(_uniqueID + stringToHash)
	hashedKey := sha256.Sum256(byteData)
	key := hashedKey[:] // Convert to slice

	block, err := aes.NewCipher(key)
	if err != nil {
		return "", fmt.Errorf("hashCredentials fail: %w", err)
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return "", fmt.Errorf("hashCredentials fail: %w", err)
	}
	encrypted := aesGCM.Seal(nil, make([]byte, aesGCM.NonceSize()), byteData, nil) // Using a zero nonce
	return hex.EncodeToString(encrypted), nil
}
