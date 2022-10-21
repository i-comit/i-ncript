/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.linux;

import static com.i_comit.linux.AES_T.listPaths;
import static com.i_comit.linux.Statics.directory;
import static com.i_comit.linux.Statics.path;
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

    public static void FileHiderAESThread(boolean fileHideBool, Path outputFile) throws IOException {
        Thread t1 = new Thread(() -> {
            try {
                FileHider_T.FileHiderAES_T(fileHideBool, outputFile);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        t1.start();
    }

//    public static void hideFilerPBarThread() {
//        fileHiderPBar_T fileHiderPBar = new fileHiderPBar_T();
//        Thread t2 = new Thread(fileHiderPBar);
//        t2.start();
//
//    }
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
        int fileCount = GUI.countFiles(path);
        if (!Main.jToggleButton1.isSelected()) {
            if (fileHideBool) {
                if (contents != null) {
                    if (contents.length != 0) {
                        Main.jToggleButton2.setEnabled(false);
                        paths.forEach(x -> {
                            try {
                                Files.setAttribute(x, "dos:hidden", true);
                                Statics.fileHideIter++;
                                if (Statics.fileHideIter == fileCount) {
                                    Main.jToggleButton2.setEnabled(true);
                                    Main.jToggleButton1.setEnabled(true);
                                    Thread.sleep(100);
                                    GUI.labelCutterThread(Main.jAlertLabel, fileCount + " files hidden", 60, 20, 300);

                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (InterruptedException ex) {
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
                                    Thread.sleep(100);
                                    GUI.labelCutterThread(Main.jAlertLabel, fileCount + " files unhidden", 60, 20, 300);
                                }

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (InterruptedException ex) {
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

    public static void FileHiderAES_T(boolean fileHideBool, Path outputFile) throws IOException, InterruptedException {
//        List<Path> paths = listPaths(path);
        File[] contents = directory.listFiles();

        if (fileHideBool) {
            if (contents != null) {
                if (contents.length != 0) {
                    Files.setAttribute(outputFile, "dos:hidden", true);
                    System.out.println("Hiding current file at: " + outputFile.toAbsolutePath().toString() + "\n");
//                    paths.forEach(x -> {
//                        try {
//                            Files.setAttribute(x, "dos:hidden", true);
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    });
                } else {
//                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 1000);
                }
            } else {
//                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 1000);
            }
        } else {
            if (contents != null) {
                if (contents.length != 0) {
                    Files.setAttribute(outputFile, "dos:hidden", false);
//                    System.out.println("Unhiding current file at: " + outputFile.toAbsolutePath().toString() + "\n");

                } else {
//                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 1000);
                }
            } else {
//                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 1000);
            }
        }
    }
}

//class fileHiderPBar_T implements Runnable {
//
//    public void run() {
//        try {
//            fileHiderProgressBar();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public static void fileHiderProgressBar() throws IOException {
//        Main.jProgressBar1.setStringPainted(true);
//        Statics.fileHideIter = 0;
//        System.out.println(Statics.fileHideIter);
//        Main.jProgressBar1.setMaximum(GUI.countFiles(Statics.path));
//        Main.jProgressBar1.setValue(Statics.fileHideIter);
//    }
//}
