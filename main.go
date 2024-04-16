/*
██╗      ███╗   ██╗ ██████╗██████╗ ██╗██████╗ ████████╗
██║      ████╗  ██║██╔════╝██╔══██╗██║██╔══██╗╚══██╔══╝
██║█████╗██╔██╗ ██║██║     ██████╔╝██║██████╔╝   ██║
██║╚════╝██║╚██╗██║██║     ██╔══██╗██║██╔═══╝    ██║
██║      ██║ ╚████║╚██████╗██║  ██║██║██║        ██║
╚═╝      ╚═╝  ╚═══╝ ╚═════╝╚═╝  ╚═╝╚═╝╚═╝        ╚═╝

A portable, cross-platform file encryption app
Copyright (C) 2022-present Khiem Luong (@khiemgluong)

	This software project is licensed under the GNU General Public
License version 3 (GPLv3). This project is free software: you can
redistribute it and/or modify it under the terms of the GNU General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.

Business Email: khiemluong@i-comit.com
*/

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

// EVENT consts
const fileProcessed = "fileProcessed"
const totalFileCt = "totalFileCount"
const addLogFile = "addLogFile"
const largeFilePercent = "largeFilePercent"
const refreshDirSize = "refreshDirSize"

// DATA consts
// Change to whatever name you want the root folder to be
const rootFolder = "i-ncript"
const keyFileName = ".i-ncript.🔑"
const _width = 220

// Change this in your build for extra security
const _uniqueID = "f56bcbf2-db56-481b-a722-11c21a4b3ae2"

// DYNAMIC data
var interrupt = make(chan struct{})
var lastDirIndex = -1
var hashedUsername string
var hashedCredentials []byte

func (m *App) onSecondInstanceLaunch(data options.SecondInstanceData) {
	// Your code to handle the second instance launch
}

func main() {
	app := NewApp()
	getters := &Getters{
		app:         app,
		directories: app.directories,
	}
	fileUtils := &FileUtils{app: app}

	err := wails.Run(&options.App{
		Title:         "i-ncript",
		Width:         _width,
		Height:        _height + 5,
		DisableResize: true,
		Fullscreen:    false,
		Frameless:     true,

		AssetServer: &assetserver.Options{
			Assets: assets,
		},
		OnStartup: app.startup,
		// OnShutdown: app.shutdown,
		Bind: []interface{}{
			app, getters, fileUtils,
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
				DarkModeBorder:     windows.RGB(200, 200, 200),
				LightModeTitleBar:  windows.RGB(200, 200, 200),
				LightModeTitleText: windows.RGB(20, 20, 20),
				LightModeBorder:    windows.RGB(200, 200, 200),
			},
		},
		SingleInstanceLock: &options.SingleInstanceLock{
			UniqueId:               _uniqueID,
			OnSecondInstanceLaunch: app.onSecondInstanceLaunch,
		},
		BackgroundColour: &options.RGBA{R: 80, G: 80, B: 80, A: 1},
	})

	if err != nil {
		println("Error:", err.Error())
	}
}
