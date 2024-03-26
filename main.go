package main

import (
	"embed"

	"github.com/wailsapp/wails/v2"
	"github.com/wailsapp/wails/v2/pkg/options"
	"github.com/wailsapp/wails/v2/pkg/options/assetserver"

	// "github.com/wailsapp/wails/v2/pkg/options/linux"
	// "github.com/wailsapp/wails/v2/pkg/options/mac"
	"github.com/wailsapp/wails/v2/pkg/options/windows"
)

//go:embed all:frontend/dist
var assets embed.FS

func (m *App) onSecondInstanceLaunch(data options.SecondInstanceData) {
	// Your code to handle the second instance launch
}

func main() {

	app := NewApp()
	logger := &Logger{}

	// Create application with options
	err := wails.Run(&options.App{

		Title:         "i-ncript",
		Width:         220,
		Height:        155,
		DisableResize: true,
		Fullscreen:    false,
		Frameless:     true,

		AssetServer: &assetserver.Options{
			Assets: assets,
		},
		OnStartup: app.startup,
		// OnShutdown: app.shutdown,
		Bind: []interface{}{
			app, logger,
		},
		Windows: &windows.Options{
			WebviewIsTransparent:              false,
			WindowIsTranslucent:               false,
			BackdropType:                      windows.Auto,
			DisablePinchZoom:                  false,
			DisableWindowIcon:                 false,
			DisableFramelessWindowDecorations: false,
			WebviewUserDataPath:               "",
			WebviewBrowserPath:                "",
			Theme:                             windows.SystemDefault,
			CustomTheme: &windows.ThemeSettings{
				DarkModeTitleBar:   windows.RGB(20, 20, 20),
				DarkModeTitleText:  windows.RGB(200, 200, 200),
				DarkModeBorder:     windows.RGB(30, 230, 20),
				LightModeTitleBar:  windows.RGB(200, 200, 200),
				LightModeTitleText: windows.RGB(20, 20, 20),
				LightModeBorder:    windows.RGB(200, 200, 200),
			},
			// ZoomFactor is the zoom factor for the WebView2. This is the option matching the Edge user activated zoom in or out.
			// ZoomFactor:           float64,
			// // IsZoomControlEnabled enables the zoom factor to be changed by the user.
			// IsZoomControlEnabled: bool,
			// User messages that can be customised
			// Messages: *windows.Messages
			// // OnSuspend is called when Windows enters low power mode
			// OnSuspend: func()
			// // OnResume is called when Windows resumes from low power mode
			// OnResume: func(),
			// // Disable GPU hardware acceleration for the webview
			// WebviewGpuDisabled: false,
		},
		SingleInstanceLock: &options.SingleInstanceLock{
			UniqueId:               "f56bcbf2-db56-481b-a722-11c21a4b3ae2",
			OnSecondInstanceLaunch: app.onSecondInstanceLaunch,
		},
		BackgroundColour: &options.RGBA{R: 80, G: 80, B: 80, A: 1},
	})

	if err != nil {
		println("Error:", err.Error())
	}
}
