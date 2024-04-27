package main

import (
	"fmt"
	"os"
	"time"

	"github.com/fatih/color"

	"github.com/wailsapp/wails/v2/pkg/logger"
)

// CustomLogger implements the logger.Logger interface
type CustomLogger struct {
	// logFile *os.File
}

// NewCustomLogger creates a new instance of CustomLogger
func NewCustomLogger() logger.Logger {
	// // Optionally open a log file
	// file, err := os.OpenFile("app.log", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	// if err != nil {
	// 	fmt.Println("Error opening log file:", err)
	// 	return nil
	// }
	return &CustomLogger{}

}

// Print logs a message
func (l *CustomLogger) Print(message string) {
	println("PRN | " + message)
}

// Trace logs a trace level message
func (l *CustomLogger) Trace(message string) {
	println("TRA | " + message)
}

// Debug logs a debug level message
func (l *CustomLogger) Debug(message string) {
	c := color.New(color.FgHiGreen).SprintFunc()
	println(c("DEB |"), message)
}

// Info logs an info level message
func (l *CustomLogger) Info(message string) {
	c := color.New(color.FgBlue).Add(color.Underline).SprintFunc()
	println(c("INF |"), message)
}

// Warning logs a warning level message
func (l *CustomLogger) Warning(message string) {
	c := color.New(color.FgHiYellow).Add(color.Bold).SprintFunc()
	println(c("WAR |"), message)
}

// Error logs an error level message
func (l *CustomLogger) Error(message string) {
	c := color.New(color.FgRed).Add(color.Bold).SprintFunc()
	println(c("ERR |"), message)
}

// Fatal logs a fatal level message and exits the application
func (l *CustomLogger) Fatal(message string) {
	c := color.New(color.BgRed).Add(color.Bold).SprintFunc()
	println(c("FAT |"), message)
	os.Exit(1)
}

// log is a helper function to handle logging
func (l *CustomLogger) log(message string) {
	timestamp := time.Now().Format(time.RFC3339)
	logMessage := fmt.Sprintf("%s %s\n", timestamp, message)

	fmt.Print(logMessage)
	// // Output to file
	// if l.logFile != nil {
	// 	if _, err := l.logFile.WriteString(logMessage); err != nil {
	// 		fmt.Println("Error writing to log file:", err)
	// 	}
	// }
}
