/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES_T.listFiles;
import static com.i_comit.windows.Statics.path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class FileHider {

    public static void FileHiderThread(boolean fileHideBool) throws IOException {
        Thread t = new Thread(() -> {
            try {
                FileHider_T.FileHider_T(fileHideBool);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
            t.start();

//            for(int i=0; i< usbparser.windows.USBParse0.GetDeviceCount(); i++){
//                runnableWindows.threadIterator++;
//                Thread t =new Thread(runnableWindows);    
//                t.start();
//            }
    }

}

class FileHider_T implements Runnable {

    public int threadIterator;

    public void run() {
//
//        try {
//            FileHider.fileHider();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void FileHider_T(boolean fileHideBool) throws IOException {
        List<Path> paths = listFiles(path);
        if (fileHideBool) {
            paths.forEach(x -> {
                try {
                    Files.setAttribute(x, "dos:hidden", true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            paths.forEach(x -> {
                try {
                    Files.setAttribute(x, "dos:hidden", false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
