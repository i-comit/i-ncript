package main

import (
	"bufio"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"crypto/sha256"
	"encoding/base64"
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
	"golang.org/x/crypto/argon2"
)

func (a *App) EncryptFiles(filePaths []string) (bool, error) {
	a.closeDirectoryWatcher()
	cipherResult, err := a.encryptOrDecrypt(true, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) DecryptFiles(filePaths []string) (bool, error) {
	a.closeDirectoryWatcher()
	cipherResult, err := a.encryptOrDecrypt(false, filePaths)
	if err != nil {
		return false, err
	}
	return cipherResult, nil
}

func (a *App) resetProgress(encrypt bool, files int) {
	a.lastFilePath = ""
	if files > 0 {
		if encrypt {
			response := fmt.Sprintf("encrypted %d file(s) ENCRYPTED.", files)
			runtime.EventsEmit(a.ctx, addLogFile, response)
		} else {
			response := fmt.Sprintf("decrypted %d file(s) DECRYPTED.", files)
			runtime.EventsEmit(a.ctx, addLogFile, response)
		}
	}

	if a.ctx != nil {
		runtime.EventsEmit(a.ctx, refreshDirSize)
		runtime.EventsEmit(a.ctx, rebuildFileTree)
		runtime.EventsEmit(a.ctx, fileProcessed, 0)
		runtime.EventsEmit(a.ctx, totalFileCt, 0)
		runtime.EventsEmit(a.ctx, "fileTaskSize", "")
		runtime.EventsOff(a.ctx, fileProcessed, totalFileCt, addLogFile)
		a.SetIsInFileTask(false)
	}
}

func (a *App) encryptOrDecrypt(encryptOrDecrypt bool, filePaths []string) (bool, error) {
	interrupt = make(chan struct{})
	var fileIter = 0
	a.fileTaskSize = 0
	for i, filePath := range filePaths {
		select {
		case <-interrupt: // Check if there's an interrupt signal
			runtime.LogError(a.ctx, "CIPHER interrupted")
			a.lastFilePath = filePath
			a.resetProgress(encryptOrDecrypt, i)
			return false, nil
		default:
			if encryptOrDecrypt {
				if strings.HasSuffix(filePath, ".enc") {
					continue
				}
				cipherFile, err := a.encryptFile(filePath)
				if err != nil {
					runtime.LogWarning(a.ctx, fmt.Sprintf("cipher issue: %v", err))
					continue
				}
				cipherFile.Close()
			} else {
				if !strings.HasSuffix(filePath, ".enc") {
					continue
				}
				cipherFile, err := a.decryptFile(filePath)
				if err != nil {
					runtime.LogWarning(a.ctx, fmt.Sprintf("cipher issue: %v", err))
					continue
				}
				cipherFile.Close()
			}

			a.lastFilePath = filePath // Update lastFile if successful
			fileIter++
			if a.ctx != nil {
				runtime.EventsEmit(a.ctx, fileProcessed, i+1)
				if encryptOrDecrypt {
					runtime.EventsEmit(a.ctx, addLogFile, "encrypted "+filepath.Base(filePath))
				} else {
					runtime.EventsEmit(a.ctx, addLogFile, "decrypted "+filepath.Base(filePath))
				}
			}
		}
	}
	if fileIter != 0 {
		if a.ctx != nil {
			a.resetProgress(true, len(filePaths))
			return true, nil
		}
	} else {
		a.SetIsInFileTask(false)
		return false, fmt.Errorf("ciphered files == 0")
	}
	return true, nil
}

func (a *App) encryptFile(filePath string) (*os.File, error) {
	aesGCM, data, err := initFileCipher(hashedCredentials, filePath)
	if err != nil {
		return nil, err
	}
	if len(data) < 1 {
		return nil, fmt.Errorf("file is empty")
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

	cipherFileWrite := a.writeCipherFile(data, encrypted, encFile)

	if cipherFileWrite != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to write encrypt file: %v", err))
		return nil, err
	}
	if err := os.Remove(filePath); err != nil {
		encFile.Close() // Best effort to close the encrypted file before returning error
		return nil, err
	}
	return encFile, nil
}

func (a *App) EncryptENCPFile(_username, _password, filePath string) (bool, error) {
	hashedReceiverUsername, err := hashString(_username)
	if err != nil {
		return false, err
	}
	var shuffledCredentials = shuffleStrings(hashedReceiverUsername, _password)
	_hashedCredentials, err := hashString(shuffledCredentials)
	if err != nil {
		return false, err
	}

	aesGCM, data, err := initFileCipher([]byte(_hashedCredentials), filePath)
	if err != nil {
		return false, err
	}
	nonce := make([]byte, aesGCM.NonceSize())
	if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
		return false, err
	}
	encrypted := aesGCM.Seal(nonce, nonce, data, nil)

	newFilePath := filePath + ".encp"
	encFile, err := os.Create(newFilePath)
	if err != nil {
		return false, err
	}
	defer encFile.Close() // Ensure the file is closed when the function returns
	fileCipher := a.writeCipherFile(data, encrypted, encFile)

	if fileCipher != nil {
		return false, err
	}
	if err := os.Remove(filePath); err != nil {
		encFile.Close()
		fmt.Printf("error closing file %s", err)
		return false, err
	}
	s := "encrypted " + filepath.Base(newFilePath)
	runtime.EventsEmit(a.ctx, addLogFile, s)
	runtime.EventsEmit(a.ctx, refreshDirSize)
	return true, nil
}

func (a *App) decryptENCPFile(hashedReceiverCredentials []byte, filePath string) (bool, error) {
	aesGCM, data, err := initFileCipher(hashedReceiverCredentials, filePath)
	if err != nil {
		return false, err
	}
	nonceSize := aesGCM.NonceSize()

	if len(data) < nonceSize {
		return false, fmt.Errorf("data too short")
	}
	nonce, ciphertext := data[:nonceSize], data[nonceSize:]
	decrypted, err := aesGCM.Open(nil, nonce, ciphertext, nil)
	if err != nil {
		return false, err
	}
	newFilePath := removeFileExtension(filePath)
	decFile, err := os.Create(newFilePath + ".zip")
	if err != nil {
		return false, err
	}
	defer decFile.Close()

	largeFileErr := a.writeCipherFile(data, decrypted, decFile)
	if largeFileErr != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to decrypt file: %v", err))
		return false, err
	}
	if err := os.Remove(filePath); err != nil {
		decFile.Close()
		return false, err
	}
	s := "opened " + filepath.Base(filePath)
	runtime.EventsEmit(a.ctx, addLogFile, s)
	runtime.EventsEmit(a.ctx, refreshDirSize)
	return true, nil
}

func (a *App) decryptFile(filePath string) (*os.File, error) {
	aesGCM, data, err := initFileCipher(hashedCredentials, filePath)
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

	fileWrite := a.writeCipherFile(data, decrypted, decFile)
	if fileWrite != nil {
		runtime.LogError(a.ctx, fmt.Sprintf("Failed to write decrypt file: %v", err))
		return nil, err
	}
	if err := os.Remove(filePath); err != nil {
		decFile.Close()
		return nil, err
	}
	return decFile, nil
}

func (a *App) writeCipherFile(data, cipherData []byte, cipherFile *os.File) error {
	done := make(chan struct{})
	var once sync.Once         // Ensure the done channel is closed only once
	var thresholdFileSize = 50 // File size in MBs to trigger ticker
	interrupted := false       // Flag to track if an interrupt has occurred

	var writtenBytes int64 //Use atomic.Int64 w/ writtenBytes.Load() for 32bit systems
	if len(data) > thresholdFileSize*1024*1024 {
		var lastpercentInt = 0
		runtime.EventsEmit(a.ctx, "largeFileName", filepath.Base(cipherFile.Name()))

		go func() {
			ticker := time.NewTicker(10 * time.Millisecond)
			defer ticker.Stop()
			for {
				select {
				case <-interrupt: // Check if there's an interrupt signal
					fmt.Println("\033[31m", "large file interrupted "+cipherFile.Name(), "\033[0m")
					a.lastFilePath = cipherFile.Name()
					once.Do(func() { close(done) }) // Close done channel safely
					interrupted = true              // Set the interrupted flag
					return
				case <-ticker.C:
					percent := (float64(atomic.LoadInt64(&writtenBytes)) / float64(len(cipherData))) * 100
					percentInt := int(percent + 0.5) // Adds 0.5 before casting to round to nearest whole number
					if lastpercentInt != percentInt {
						runtime.EventsEmit(a.ctx, largeFilePercent, percentInt)
					}
					lastpercentInt = percentInt
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
			return fmt.Errorf("file write interrupted: %s", cipherFile.Name())
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
	if len(data) > thresholdFileSize*1024*1024 {
		runtime.EventsEmit(a.ctx, "largeFileName", "")
		runtime.EventsEmit(a.ctx, largeFilePercent, 0)
	}
	a.fileTaskSize += int64(len(data))
	runtime.EventsEmit(a.ctx, "fileTaskSize", formatDirSize(a.fileTaskSize))
	return nil
}

func initFileCipher(_hashedCredentials []byte, filePath string) (cipher.AEAD, []byte, error) {
	data, err := os.ReadFile(filePath)
	if err != nil {
		return nil, nil, err
	}
	hashedKey := sha256.Sum256(_hashedCredentials)
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

func checkCredentials(_hashedCredentials string) int {
	keyFilePath, err := getEndPath(keyFileName)
	if err != nil {
		return 0
	}
	keyfile, err := os.Open(keyFilePath)
	if err != nil {
		fmt.Println("Key file doesn't exist", err)
		return 0
	}
	defer keyfile.Close()

	reader := bufio.NewReader(keyfile)
	readHash, err := reader.ReadString('\n') // Read until the newline, which should be the end of your hash
	if err != nil {
		if err != io.EOF {
			fmt.Println("Error reading line:", err)
			return 1
		}
	}
	fmt.Printf("First line read: %s\n", readHash)
	hashedStringToCheck, err := verifySavedCredentials(_hashedCredentials, []byte(readHash))
	if err != nil {
		log.Printf("Failed to hash credentials to check %s", err)
		return -1
	}
	if hashedStringToCheck {
		log.Printf("String matched with key hash")
		return 2
	} else {
		log.Printf("String not matched with key hash")
		return 3
	}
}

func saveHashedCredentials(credentials string) error {
	// Generate a random salt
	salt := make([]byte, 16)
	if _, err := rand.Read(salt); err != nil {
		return err
	}
	hash := argon2.Key([]byte(credentials), salt, 3, 32*1024, 4, 500)
	// Encode salt and hash to base64 to store them as strings
	saltStr := base64.StdEncoding.EncodeToString(salt)
	hashStr := base64.StdEncoding.EncodeToString(hash)
	keyFilePath, err := getEndPath(keyFileName)
	if err != nil {
		return err
	}
	os.WriteFile(keyFilePath, []byte(saltStr+hashStr), 0666)
	hideFile(keyFilePath)
	return nil
}

func verifySavedCredentials(credentials string, readHash []byte) (bool, error) {
	saltStr := readHash[:24] // Salt is the first 24 characters (base64)
	hashStr := readHash[24:] // Hash is the rest

	salt, err := base64.StdEncoding.DecodeString(string(saltStr))
	if err != nil {
		return false, err
	}
	storedHash, err := base64.StdEncoding.DecodeString(string(hashStr))
	if err != nil {
		return false, err
	}
	enteredHash := argon2.Key([]byte(credentials), salt, 3, 32*1024, 4, 500)
	return base64.StdEncoding.EncodeToString(enteredHash) == base64.StdEncoding.EncodeToString(storedHash), nil
}

func hashString(stringToHash string) (string, error) {
	byteData := []byte(_uniqueID + stringToHash)
	hashedString, err := hashBytes(byteData)
	if err != nil {
		return "", fmt.Errorf("hashString fail: %w", err)
	}
	return hex.EncodeToString(hashedString), nil
}

func hashBytes(byteData []byte) ([]byte, error) {
	hashedKey := sha256.Sum256(byteData)
	key := hashedKey[:] // Convert to slice

	block, err := aes.NewCipher(key)
	if err != nil {
		return nil, err
	}

	aesGCM, err := cipher.NewGCM(block)
	if err != nil {
		return nil, err
	}
	//The nonce is omitted as we need to ensure that the hash is the same for repeated logins
	encrypted := aesGCM.Seal(nil, make([]byte, aesGCM.NonceSize()), byteData, nil) // Using a zero
	return encrypted, nil
}
