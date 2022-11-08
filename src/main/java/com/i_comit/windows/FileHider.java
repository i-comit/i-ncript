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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class FileHider {

    public static void cleanUp() {
        try {
            Set<String> unique = new HashSet<>();

            List<Path> paths = listPaths(path);
            List<String> duplicates = new ArrayList<>();
            List<String> duplStr = new ArrayList<>();
            int deletedFiles = 0;

            paths.forEach(x -> {
                String f = x.toFile().getAbsolutePath().replace(".enc", "");
                duplStr.add(f);
            });

            for (String n : duplStr) {
                if (!unique.add(n)) {
                    duplicates.add(n);
                }
            }

            if (!duplicates.isEmpty()) {
                for (int i = 0; i < duplicates.size(); i++) {
                    System.out.println(duplicates.get(i));
                    String enc = duplicates.get(i) + ".enc";

                    File decF = Paths.get(duplicates.get(i)).toFile();
                    File encF = Paths.get(enc).toFile();

//                    System.out.println(decF);
//                    System.out.println(encF);
                    if (decF.length() > encF.length()) {
                        encF.delete();
                        System.out.println("deleted ENC " + encF);
                    } else {
                        decF.delete();
                        System.out.println("deleted DEC " + decF);
                    }
                    deletedFiles++;
                }
            } else {
                System.out.println("no files to clean up");
            }
            if (deletedFiles > 2) {
                Main.jTextArea1.append("cleaned up " + deletedFiles + " corrupted files\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                } else {
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);

                                    Main.jProgressBar1.setVisible(false);
                                    Main.jProgressBar2.setVisible(true);
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
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                } else {
                                    Main.toolBtnsBool(true);
                                    Main.jTextArea5.setVisible(true);
                                    Main.jProgressBar1.setVisible(false);
                                    Main.jProgressBar2.setVisible(true);
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
