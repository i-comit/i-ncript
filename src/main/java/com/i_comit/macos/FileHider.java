/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.macos;

import static com.i_comit.macos.FileHider.fileCt;
import static com.i_comit.macos.GUI.listPaths;
import static com.i_comit.macos.HotFiler_T.folderWatcher;
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

    static int fileCt = 0;

    public static void cleanUp(Path path) {
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
                    String enc = duplicates.get(i) + ".enc";

                    File decF = Paths.get(duplicates.get(i)).toFile();
                    File encF = Paths.get(enc).toFile();

                    if (decF.length() > encF.length()) {
                        encF.delete();
                        System.out.println("deleted " + encF);
                    } else if (decF.length() == encF.length()) {
                        encF.delete();
                        System.out.println("deleted " + encF);
                    } else {
                        decF.delete();
                        System.out.println("deleted " + decF);
                    }
                    deletedFiles++;
                }
            } else {
                System.out.println("no files to clean up in " + path.toFile().getName() + " folder");
            }
            if (deletedFiles > 2) {
                Main.jTextArea1.append("cleaned up " + deletedFiles + " corrupted files\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void FileHiderThread(boolean fileHideBool, Path path) throws IOException {
        Thread t = new Thread(() -> {
            try {
                FileHider_T.FileHider_T(fileHideBool, path);
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

    public static void fileHiderToolReenable() {
        Main.jToggleButton2.setEnabled(true);
        Main.toolBtnsBool(true);
        Main.jTree1.setEnabled(true);
        Main.dragDrop.setVisible(true);
        Main.jTabbedPane1.setSelectedIndex(0);
        Main.jProgressBar1.setVisible(false);
        Main.jProgressBar2.setVisible(true);
        Statics.fileIter = 0;
        Statics.fileCount = 0;
        fileCt = 0;
    }

    public static void FileHider_T(boolean fileHideBool, Path path) throws IOException {
        Statics.fileHideIter = 0;
        List<Path> paths = listPaths(path);
        int fileCount = GUI.countAllFiles(path);
        if (Main.jToggleButton1.isSelected()) {
            System.out.println("hot filer is selected");
            Main.toolBtnsBool(true);
            Main.jProgressBar1.setValue(0);
            Main.jProgressBar2.setMaximum(0);
            folderWatcher();
        }
        if (fileHideBool) {
            paths.forEach(x -> {
                try {
                    getFileAttr(x, fileHideBool);
                    if (Statics.fileHideIter >= fileCount - 1) {
                        if (Statics.fileHideIter != 0) {
                            if (fileCt > 10) {
                                Thread.sleep(300);
                                GUI.labelCutterThread(Main.jAlertLabel, Statics.fileHideIter + " files hidden", 30, 25, 300, false);
                                fileHiderToolReenable();
                            } else {
                                fileHiderToolReenable();
                            }
                        } else {
                            fileHiderToolReenable();
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
            TreeView.populateStoreTree(path);
        } else {
            paths.forEach(x -> {
                try {
                    getFileAttr(x, fileHideBool);
                    if (Statics.fileHideIter >= fileCount - 1) {
                        if (Statics.fileHideIter != 0) {
                            if (fileCt > 10) {
                                Thread.sleep(300);
                                GUI.labelCutterThread(Main.jAlertLabel, Statics.fileHideIter + " files unhidden", 30, 25, 300, false);
                                fileHiderToolReenable();
                            } else {
                                fileHiderToolReenable();
                            }
                        } else {
                            fileHiderToolReenable();
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
            TreeView.populateStoreTree(path);
        }
    }

    public static void getFileAttr(Path x, boolean fileHideBool) throws IOException {
        if (!Main.jToggleButton2.isSelected()) {
            if (x.toFile().getName().startsWith(".")) {
                String filePath = x.toFile().toString().replaceAll(x.toFile().getName(), "");
                File outputFile = new File(filePath + x.toFile().getName().substring(1));
                x.toFile().renameTo(outputFile);
                ++FileHider.fileCt;
            }
        }

        if (Main.jToggleButton2.isSelected()) {
            if (!x.toFile().getName().startsWith(".")) {
                String filePath = x.toFile().toString().replaceAll(x.toFile().getName(), "");
                File outputFile = new File(filePath + "." + x.toFile().getName());
                x.toFile().renameTo(outputFile);
                ++FileHider.fileCt;
            }
        }
        ++Statics.fileHideIter;
    }
}