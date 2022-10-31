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
import java.nio.file.LinkOption;
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
        Main.toolBtnsBool(false);
        fileCt = 0;
        Statics.fileIter = 0;
        Statics.fileCount = 0;
        if (fileHideBool) {
            if (contents != null) {
                if (contents.length != 0) {
                    paths.forEach(x -> {
                        try {
                            getFileAttr(x, fileHideBool);
                            if (Statics.fileHideIter == fileCount) {
                                if (fileCt > 10) {
                                    Thread.sleep(300);
                                    GUI.labelCutterThread(Main.jAlertLabel, fileCt + " files hidden", 30, 25, 300);
                                    Main.jTextArea1.append("\nhide filer enabled (hide all files)\n");
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);

                                } else {
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                }
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
                    paths.forEach(x -> {
                        try {
                            getFileAttr(x, fileHideBool);
                            if (Statics.fileHideIter == fileCount) {
                                if (fileCt > 10) {
                                    Thread.sleep(300);
                                    GUI.labelCutterThread(Main.jAlertLabel, fileCt + " files unhidden", 30, 25, 350);
                                    Main.jTextArea1.append("\nhide filer disabled (reveal all files)\n");

                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                } else {
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                }
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

    static int fileCt = 0;

    public static void getFileAttr(Path x, boolean fileHideBool) throws IOException {
        String fileAttr = Files.getAttribute(x, "dos:hidden", LinkOption.NOFOLLOW_LINKS).toString();
        boolean fileAttrBool = Boolean.parseBoolean(fileAttr);

        if (fileAttrBool == true) {
            if (!Main.jToggleButton2.isSelected()) {
                Files.setAttribute(x, "dos:hidden", false);
                fileCt++;
            }
        }

        if (fileAttrBool == false) {
            if (Main.jToggleButton2.isSelected()) {
                Files.setAttribute(x, "dos:hidden", true);
                fileCt++;
            }
        }
        Statics.fileHideIter++;

    }
}
