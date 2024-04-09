package main

import (
	"archive/zip"
	"encoding/base64"
	"fmt"
	"io"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"sync/atomic"

	stdRuntime "runtime"

	wailsRuntime "github.com/wailsapp/wails/v2/pkg/runtime" // This is an example path
)

type FileUtils struct {
	app *App
}

var isInFileTask = false
var rebuildFileTree = "rebuildFileTree"

func (f *FileUtils) CheckIfPathIsFile(filePath string) (bool, error) {
	info, err := os.Stat(filePath)
	if err != nil {
		return false, err
	}
	return !info.IsDir(), nil
}

func (f *FileUtils) MoveFilesToPath(filePaths []string, targetPath string) {
	if f.app.ctx != nil {
		for _, files := range filePaths {
			if targetPath == filepath.Dir(files) {
				continue
			}
			newFilePath := filepath.Join(targetPath, filepath.Base(files))
			// Move the file
			err := os.Rename(files, newFilePath)
			if err != nil {
				wailsRuntime.LogError(f.app.ctx, "Error moving file: "+err.Error())
				continue
			}
			wailsRuntime.LogError(f.app.ctx, "Successfully moved file to: "+newFilePath)
		}
		wailsRuntime.EventsEmit(f.app.ctx, rebuildFileTree)
		f.app.SetIsInFileTask(false)
	}
}

func (f *FileUtils) OpenFile(filePath string) error {
	var cmd *exec.Cmd
	switch stdRuntime.GOOS {
	case "windows":
		cmd = exec.Command("cmd", "/c", "start", "", filePath)
	case "darwin":
		cmd = exec.Command("open", filePath)
	case "linux":
		cmd = exec.Command("xdg-open", filePath)
	default:
		return fmt.Errorf("unsupported platform")
	}

	return cmd.Start()
}

func (f *FileUtils) OpenDirectory(_filePath string) error {
	// Determine the directory path from the filePath
	dirPath := _filePath
	fileInfo, err := os.Stat(_filePath)
	if err != nil {
		return err
	}
	if !fileInfo.IsDir() {
		dirPath = filepath.Dir(_filePath) + string(filepath.Separator)
	}
	fmt.Println("DirPath open directory " + dirPath)
	// Open the directory based on the operating system
	var cmd *exec.Cmd
	switch stdRuntime.GOOS {
	case "windows":
		cmd = exec.Command("explorer", dirPath)
	case "darwin":
		cmd = exec.Command("open", dirPath)
	case "linux":
		cmd = exec.Command("xdg-open", dirPath)
	default:
		return fmt.Errorf("unsupported platform")
	}
	return cmd.Start()
}

type FileNode struct {
	RelPath  string      `json:"relPath"`
	Children []*FileNode `json:"children,omitempty"`
}

func (a *App) BuildDirectoryFileTree(dirIndex int) (*FileNode, error) {
	if dirIndex < 0 {
		dirIndex = 0
	} else if dirIndex >= len(a.directories) {
		dirIndex = len(a.directories) - 1
	}
	var rootDir = a.directories[dirIndex]
	var separator = string(filepath.Separator)
	rootDir = filepath.Clean(rootDir)
	rootNode := &FileNode{RelPath: rootDir + separator, Children: []*FileNode{}}
	err := filepath.Walk(rootDir, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		path = filepath.Clean(path)
		if path == rootDir {
			// fmt.Println("ROOT PATH " + path)
			return nil
		}

		relativePath, err := filepath.Rel(rootDir, path)
		if err != nil {
			return err
		}
		parts := strings.Split(relativePath, separator)
		addPath(rootNode, parts, "")
		return nil
	})

	if err != nil {
		return nil, err
	}
	return rootNode, nil
}

func (f *FileUtils) PackFilesForENCP(receiverUsername, receiverPassword string, filePaths []string) error {
	f.app.closeDirectoryWatcher()
	interrupt = make(chan struct{})

	lastFilePath := filePaths[len(filePaths)-1]
	filenameWithoutExtension := removeFileExtension(lastFilePath)
	zipFilePath := filenameWithoutExtension + ".zip"
	// Create the zip file
	zipFile, err := os.Create(zipFilePath)
	if err != nil {
		return err
	}
	defer zipFile.Close()
	// Create a new zip writer
	zipWriter := zip.NewWriter(zipFile)
	defer zipWriter.Close()
	for _, filePath := range filePaths {
		select {
		case <-interrupt: // Check if there's an interrupt signal
			fmt.Printf("packing interrupted")
			lastFilePath = filePath
			return nil
		default:
			if err := f.addFileToZip(zipWriter, filePath); err != nil {
				return err
			}
		}
	}
	if err := zipWriter.Close(); err != nil {
		return fmt.Errorf("error closing zip writer: %s", err)
	}
	zipFile.Close()

	hashedReceiverUsername, err := hashString(receiverUsername)
	if err != nil {
		return fmt.Errorf("recipient hash fail: %w", err)
	}
	var shuffledCredentials = shuffleStrings(hashedReceiverUsername, receiverPassword)
	_hashedCredentials, err := hashString(shuffledCredentials)
	if err != nil {
		return fmt.Errorf("error hashing recipient credentials: %s", err)
	}

	fmt.Printf("_hashedCredentials! %s", _hashedCredentials)

	// encodedCredentials := base64.StdEncoding.EncodeToString([]byte(_hashedCredentials))
	encryptedZipFile, err := f.app.encryptZipFile([]byte(_hashedCredentials), zipFile.Name())
	if err != nil {
		fmt.Printf("failed encryption ENCP %s", err)
		return err
	}
	encryptedZipFile.Close()

	// keyFile, err := os.Create(filepath.Base(lastFilePath) + "_stuff.key")
	// if err != nil {
	// 	fmt.Printf("key error %s", err)
	// 	return err
	// }
	// defer keyFile.Close()

	// _, err = keyFile.WriteString(_hashedCredentials)
	// if err != nil {
	// 	log.Fatalf("Failed to write to file: %s", err)
	// }

	// // Assuming previous code exists above this and you've just closed the encryptedZipFile
	// // Create a new parent zip file that will contain the encrypted zip file and the key file
	// parentZipFile, err := os.Create(filepath.Join(filepath.Dir(zipFile.Name()), "final_bundle.zip"))
	// if err != nil {
	// 	return fmt.Errorf("failed to create parent zip file: %s", err)
	// }
	// defer parentZipFile.Close()

	// parentZipWriter := zip.NewWriter(parentZipFile)
	// defer parentZipWriter.Close()

	// // Function to add a file to the parent zip
	// addFileToZip := func(zipWriter *zip.Writer, filePath string) error {
	// 	fileToZip, err := os.Open(filePath)
	// 	if err != nil {
	// 		return err
	// 	}
	// 	defer fileToZip.Close()

	// 	// Get the info about the file
	// 	info, err := fileToZip.Stat()
	// 	if err != nil {
	// 		return err
	// 	}

	// 	header, err := zip.FileInfoHeader(info)
	// 	if err != nil {
	// 		return err
	// 	}

	// 	header.Name = filepath.Base(filePath)
	// 	header.Method = zip.Deflate

	// 	writer, err := zipWriter.CreateHeader(header)
	// 	if err != nil {
	// 		return err
	// 	}

	// 	_, err = io.Copy(writer, fileToZip)
	// 	return err
	// }

	// // Add the encrypted zip file to the parent zip
	// if err := addFileToZip(parentZipWriter, encryptedZipFile.Name()); err != nil {
	// 	return fmt.Errorf("failed to add encrypted zip to parent zip: %s", err)
	// }

	// // Add the key file to the parent zip
	// if err := addFileToZip(parentZipWriter, keyFile.Name()); err != nil {
	// 	return fmt.Errorf("failed to add key file to parent zip: %s", err)
	// }

	// // Make sure to close the parentZipWriter to finalize the zip file
	// if err := parentZipWriter.Close(); err != nil {
	// 	return fmt.Errorf("error closing parent zip writer: %s", err)
	// }

	// fmt.Println("Successfully created parent zip with encrypted zip and key file.")

	return nil
}

func (f *FileUtils) AuthenticateENCPFile(password string, encFilePath string) error {
	fmt.Println("ENCP file path " + encFilePath)
	var shuffledCredentials = shuffleStrings(hashedUsername, password)
	// _bytesCredentials, err := hashBytes([]byte(shuffledCredentials))
	// if err != nil {
	// 	return fmt.Errorf("error hashing user credentials: %s", err)
	// }
	_hashedCredentials, err := hashString(shuffledCredentials)
	if err != nil {
		return fmt.Errorf("error hashing user credentials: %s", err)
	}
	fmt.Printf("hashed Credentials %s \n", hashedCredentials)
	fmt.Printf("_hashedCredentials %s \n", _hashedCredentials)
	decryptedZipFile, err := f.app.decryptENCPFile([]byte(_hashedCredentials), encFilePath)
	if err != nil {
		fmt.Printf("error decrypting zip file: %s", err)
		return fmt.Errorf("error writing decrypted encp to file: %s", err)
	}
	fmt.Println("decrypted zip file " + decryptedZipFile.Name())
	return nil

	// encodedCredentials := base64.StdEncoding.EncodeToString(_hashedCredentials)

	// fmt.Println("hashed your creds " + encodedCredentials)
	// encFileBytes, err := os.ReadFile(encFilePath)
	// if err != nil {
	// 	return fmt.Errorf("error reading file: %s", err)
	// }

	// // encFile, err := os.Open(encFilePath)
	// // if err != nil {
	// // 	return fmt.Errorf("error reading file: %s", err)
	// // }
	// // decryptedZipFile, err := f.app.decryptENCPFile([]byte(encodedCredentials), encFile)
	// // if err != nil {
	// // 	fmt.Printf("error decrypted zip file: %s", err)
	// // 	return fmt.Errorf("error writing decrypted encp to file: %s", err)
	// // }
	// // fmt.Println("decrypted zip file " + decryptedZipFile.Name())

	// matched, originalBytes := matchBytesProcedurally(bytesToRunes(encFileBytes), []byte(encodedCredentials))
	// if matched {
	// 	zipFileName := removeFileExtension(encFilePath)
	// 	openedZipFile, err := os.Create(zipFileName + "_original.zip")
	// 	if err != nil {
	// 		fmt.Printf("create zip file fail %s", err)
	// 		return err
	// 	}
	// 	err = os.WriteFile(openedZipFile.Name(), originalBytes, 0644)
	// 	if err != nil {
	// 		fmt.Printf("error writing encp bytes to file: %s", err)
	// 		return fmt.Errorf("error writing decrypted encp to file: %s", err)
	// 	}

	// 	decryptedZipFile, err := f.app.decryptENCPFile([]byte(encodedCredentials), openedZipFile)
	// 	if err != nil {
	// 		fmt.Printf("error decrypted zip file: %s", err)
	// 		return fmt.Errorf("error writing decrypted encp to file: %s", err)
	// 	}
	// 	fmt.Println("decrypted zip file " + decryptedZipFile.Name())
	// }
	// return nil
}

func (f *FileUtils) OpenENCPmatch(password, encFilePath string) error {
	fmt.Println("Open file path " + encFilePath)

	var shuffledCredentials = shuffleStrings(hashedUsername, password)
	_hashedCredentials, err := hashBytes([]byte(shuffledCredentials))
	if err != nil {
		return fmt.Errorf("error hashing user credentials: %s", err)
	}
	encodedCredentials := base64.StdEncoding.EncodeToString(_hashedCredentials)

	fmt.Println("hashed your creds " + encodedCredentials)
	encFileBytes, err := os.ReadFile(encFilePath)
	if err != nil {
		return fmt.Errorf("error reading file: %s", err)
	}

	matched, originalBytes := matchBytesProcedurally(bytesToRunes(encFileBytes), []byte(encodedCredentials))
	if matched {
		zipFileName := removeFileExtension(encFilePath)
		openedZipFile, err := os.Create(zipFileName + "_originalunmixed.zip")
		if err != nil {
			fmt.Printf("create zip file fail %s", err)
			return err
		}
		err = os.WriteFile(openedZipFile.Name(), originalBytes, 0644)
		if err != nil {
			fmt.Printf("error writing encp bytes to file: %s", err)
			return fmt.Errorf("error writing decrypted encp to file: %s", err)
		}
	}
	return nil
}

func (f *FileUtils) GetAppendedFileBytes(filePath string) error {
	file, err := os.Open(filePath)
	if err != nil {
		return err
	}
	defer file.Close()
	info, err := file.Stat()
	if err != nil {
		return err
	}
	// Check if the file size is more than 10 bytes
	if info.Size() > 10 {
		startPos := info.Size() - 5
		// Seek to the start position
		_, err := file.Seek(startPos, 0)
		if err != nil {
			return err
		}
		// Read the last 5 bytes
		buf := make([]byte, 5)
		_, err = file.Read(buf)
		if err != nil {
			return err
		}
		fmt.Println("Last 5 bytes:", buf)
	}
	return nil
}

func insertBytesProcedurally(b1, b2 []byte) []rune {
	if len(b1) < len(b2) {
		b1, b2 = b2, b1
	}
	r1, r2 := bytesToRunes(b1), bytesToRunes(b2)
	// Determine total length and ratio
	totalLength := len(r1) + len(r2)
	ratio := float64(len(r1)) / float64(len(r2))
	// Prepare result slice
	var result []rune
	i, j := 0, 0.0 // i tracks position in r1, j tracks float position in r2
	for len(result) < totalLength {
		// Decide whether to append from r1 or r2 based on ratio and progress
		if float64(i) <= j*ratio && i < len(r1) {
			result = append(result, r1[i])
			i++
		} else if int(j) < len(r2) {
			result = append(result, r2[int(j)])
			j++
		}
	}
	return result
}

func matchBytesProcedurally(combinedByte []rune, byteToMatch []byte) (bool, []byte) {
	rByteToMatch := bytesToRunes(byteToMatch)
	originalS1Length := len(combinedByte) - len(rByteToMatch)
	var ratio float64

	if originalS1Length != 0 {
		ratio = float64(len(combinedByte)) / float64(originalS1Length)
	} else {
		fmt.Println("Matched string: (none, combinedByte is the same as byteToMatch)")
		return false, runesToBytes(combinedByte)
	}

	var matchedRunes []rune
	var remainingRunes []rune
	j := 0.0 // j tracks the float position in rByteToMatch based on the ratio.
	for i := 0; i < len(combinedByte); i++ {
		if int(j) < len(rByteToMatch) && float64(i) >= j*ratio && combinedByte[i] == rByteToMatch[int(j)] {
			matchedRunes = append(matchedRunes, combinedByte[i])
			j++
		} else {
			remainingRunes = append(remainingRunes, combinedByte[i])
		}
	}

	fmt.Println("Matched string:", string(matchedRunes))
	fmt.Println("Matched?:", len(matchedRunes) == len(rByteToMatch))
	// Success is determined by whether we've matched all runes from byteToMatch.
	// Returns the remaining bytes as a byte array if the full sequence was found.
	return len(matchedRunes) == len(rByteToMatch), runesToBytes(remainingRunes)
}

func (f *FileUtils) addFileToZip(zipWriter *zip.Writer, zipFilePath string) error {
	fileToZip, err := os.Open(zipFilePath)
	if err != nil {
		return err
	}
	defer fileToZip.Close()
	// Get the info about the file
	info, err := fileToZip.Stat()
	if err != nil {
		return err
	}
	// Create a header based on the file info
	header, err := zip.FileInfoHeader(info)
	if err != nil {
		return err
	}
	// Use the basename of the filePath for the file name in the zip
	header.Name = filepath.Base(zipFilePath)
	// Create the writer for the file
	writer, err := zipWriter.CreateHeader(header)
	if err != nil {
		return err
	}

	const thresholdFileSize = 50 //if file is more than 50MB
	if info.Size() > int64(thresholdFileSize*1024*1024) {
		err = f.checkLargeZipFileTicker(writer, fileToZip)
	} else {
		_, err = io.Copy(writer, fileToZip)
	}
	fmt.Printf("Copied %s to zip file \n", zipFilePath)
	return err
}

func (f *FileUtils) checkLargeZipFileTicker(writer io.Writer, fileToZip *os.File) error {
	fileSize, err := fileToZip.Seek(0, io.SeekEnd) // Get the file size
	if err != nil {
		return err
	}
	_, err = fileToZip.Seek(0, io.SeekStart) // Reset file pointer
	if err != nil {
		return err
	}

	var writtenBytes int64 = 0
	// Initialize your progress tracking here, if needed
	buf := make([]byte, 4096) // 4KB buffer
	for {
		n, err := fileToZip.Read(buf)
		if err != nil && err != io.EOF {
			return err
		}
		if n == 0 {
			break
		}
		wn, err := writer.Write(buf[:n])
		if err != nil {
			return err
		}
		atomic.AddInt64(&writtenBytes, int64(wn))
		// Update progress here using writtenBytes and fileSize
		percent := float64(writtenBytes) / float64(fileSize) * 100
		fmt.Printf("Progress: %.2f%%\n", percent) // Replace with your event emission

		if err == io.EOF {
			break
		}
	}
	return nil
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

func getFilesRecursively(dirs ...string) ([]string, error) {
	var files []string
	for _, dir := range dirs {
		err := filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
			if err != nil {
				return err
			}
			if !info.IsDir() { // Ensure we're only appending files, not directories
				if filepath.Base(path) != keyFileName {
					files = append(files, path)
				}
			}
			return nil
		})
		if err != nil {
			return nil, err
		}
	}
	return files, nil
}
