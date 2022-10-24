/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES_T.listPaths;
import static com.i_comit.windows.Statics.directory;
import static com.i_comit.windows.Statics.path;
import java.io.File;
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
        Statics.fileHideIter = 0;
        List<Path> paths = listPaths(path);
        File[] contents = directory.listFiles();
        int fileCount = GUI.countAllFiles(path);

        Main.jToggleButton1.setEnabled(false);
        Main.jRadioButton0.setEnabled(false);
        Main.jRadioButton1.setEnabled(false);

        if (fileHideBool) {
            if (contents != null) {
                if (contents.length != 0) {
//                    Main.jToggleButton2.setEnabled(false);
                    paths.forEach(x -> {
                        try {
                            Files.setAttribute(x, "dos:hidden", true);
                            Statics.fileHideIter++;
                            if (Statics.fileHideIter == fileCount) {
                                Main.jToggleButton2.setEnabled(true);
                                Main.jToggleButton1.setEnabled(true);
                                Main.jRadioButton0.setEnabled(true);
                                Main.jRadioButton1.setEnabled(true);
                                Main.jToggleButton1.setSelected(false);
                                Main.buttonGroup1.clearSelection();
                                switch (Statics.AESMode) {
                                    case 0 ->Main.jRadioButton0.setSelected(true);
                                    case 1 ->Main.jRadioButton1.setSelected(true);
                                }

                                Thread.sleep(300);
                                GUI.labelCutterThread(Main.jAlertLabel, fileCount + " files hidden", 30, 25, 300);

                            }
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
//                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 1000);
                }
            } else {
//                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 1000);
            }
        } else {
            if (contents != null) {
                if (contents.length != 0) {
                    Main.jToggleButton2.setEnabled(false);

                    paths.forEach(x -> {
                        try {
                            Files.setAttribute(x, "dos:hidden", false);
                            Statics.fileHideIter++;
                            if (Statics.fileHideIter == fileCount) {
                                Main.jToggleButton2.setEnabled(true);
                                Main.jToggleButton1.setEnabled(true);
                                Main.jRadioButton0.setEnabled(true);
                                Main.jRadioButton1.setEnabled(true);
                                Main.jToggleButton1.setSelected(false);
                                switch (Statics.AESMode) {
                                    case 0 ->Main.jRadioButton0.setSelected(true);
                                    case 1 ->Main.jRadioButton1.setSelected(true);
                                }

                                Thread.sleep(300);
                                GUI.labelCutterThread(Main.jAlertLabel, fileCount + " files unhidden", 30, 25, 350);
                            }

                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
//                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 1000);
                }
            } else {
//                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 1000);
            }
        }
    }
}
