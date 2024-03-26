package main

import (
	"context"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"io"
	"strings"

	// "io"
	// "log"
	"os"
	"path/filepath"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type Encryptor struct {
	ctx context.Context
}

func (a *App) EncryptFilesInDir(dirIndex int) error {
	filePaths, err := getFilesRecursively(directories[0])
	fmt.Println("\033[32mdirectories[0] ", directories[0], "\033[0m")
	if err != nil {
		return err
	}
	// dirFileCt, err := a.getters.GetDirectoryFileCt(dirIndex)
	if err != nil {
		return err
	}
	for i, filePath := range filePaths {
		// Read the file content into a byte slice
		// fmt.Println("\033[31mencrypt filePath ", filePath, "\033[0m")
		if err != nil {
			return err
		}
		if strings.HasSuffix(filePath, ".enc") {
			continue
		}

		// s := fmt.Sprintf("%f", i+1)
		// runtime.LogError(e.ctx, "current fileCount "+s)
		// fmt.Println("\033[32mfileCount ", i+1, "\033[0m")
		// }
		encryptedFile, err := encryptFile(filePath)
		if err != nil {
			return err
		}
		defer encryptedFile.Close()
		if a.ctx != nil {
			runtime.EventsEmit(a.ctx, "fileProcessed", i+1, 161)
			fmt.Println("\033[31mfileCount ", 161, "\033[0m")
		}
		// fmt.Println("Encrypted file created:", encryptedFile.Name())
	}
	if a.ctx != nil {
		// runtime.EventsEmit(a.ctx, "fileProcessed", 0, 0)
		runtime.EventsOff(a.ctx, "fileProcessed")
	}
	return nil
}

func (a *App) DecryptFilesInDir() error {
	filePaths, err := getFilesRecursively(directories[0])
	fmt.Println("\033[32mdirectories[0] ", directories[0], "\033[0m")
	if err != nil {
		return err
	}
	// dirFileCt, err := a.getters.GetDirectoryFileCt(0)
	for i, filePath := range filePaths {
		// Read the file content into a byte slice
		// fmt.Println("\033[31mdecrypt filePath ", filePath, "\033[0m")
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
		if a.ctx != nil {
			runtime.EventsEmit(a.ctx, "fileProcessed", i+1, 161)
			fmt.Println("\033[31mfileCount ", 161, "\033[0m")
		}
	}
	if a.ctx != nil {
		// runtime.EventsEmit(a.ctx, "fileProcessed", 0, 0)
		runtime.EventsOff(a.ctx, "fileProcessed")
	}
	return nil
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
