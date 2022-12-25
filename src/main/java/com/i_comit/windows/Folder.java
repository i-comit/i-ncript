/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.Statics.zipFileCount;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.DefaultListModel;

public class Folder {

    public static String sendFolderStr = "";
    private static final List<String> zipFileList = new ArrayList<>();

    public static void prepareZipFile() throws IOException {
        zipFileList.clear();
        sendFolderStr = Statics.sendFolder + File.separator + firstLastChar(Statics.recipientUsername) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddmmss"));
        zipFile(Statics.sendFolder.toString(), sendFolderStr, Main.jTree1.isSelectionEmpty());
        deleteDirectory(Statics.sendFolder.toFile(), Main.jTree1.isSelectionEmpty());
        new File(Statics.sendFolder + File.separator + Statics.keyName).delete();
        GUI.resetProgressBar(Main.jProgressBar2);
        Main.toolBtnsBool(true);
        Statics.resetSendTools(2);
        Main.dragDrop.setVisible(true);
        Statics.resetStaticInts();
        GUI.getGB();
        Main.refreshTreeView(Statics.sendFolder, TreeView.sendCaretPos);
    }

    public static File appLockFile = new File(Main.root + Main.masterFolder + "app" + File.separator + ".app.🔒");
    public static boolean appLockBool = true;

    public static void appLock() {
        if (appLockBool) {
            new Thread(() -> {
                try {
                    while (!appLockFile.exists()) {
                        Files.createFile(appLockFile.toPath());
                        Files.setAttribute(appLockFile.toPath(), "dos:hidden", true);
                        System.out.println("creating app lock file");
                    }
                    Thread.sleep(100);
                    appLock();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private static String firstLastChar(String username) {
        String a = username.substring(0, 1);
        String b = username.substring(username.length() - 1, username.length());
        String c = a + b + "-";
        return c;
    }

    public static void deleteDirectory(File file, boolean jTreeBool) {
        if (jTreeBool) {
            for (File subfile : file.listFiles()) {
                if (!subfile.toString().endsWith(".i-cc")) {
                    if (subfile.isDirectory()) {
                        deleteDirectory(subfile, jTreeBool);
                    }
                    subfile.delete();
                }
            }
        } else {
            for (Path subPath : filteredSendList) {
                if (!subPath.toString().endsWith(".i-cc")) {
                    if (subPath.toFile().isDirectory()) {
                        System.out.println("deleting directory " + subPath.toFile());
                        deleteDirectory(subPath.toFile(), jTreeBool);
                    }
                    System.out.println("deleting file " + subPath.toFile());
                    subPath.toFile().delete();
                }
            }
        }
    }

    private static DefaultListModel zipList = new DefaultListModel();

    public static void listZipFiles() {
        zipList.clear();
        Main.jList1.removeAll();
        File folder = new File(Statics.receiveFolder.toString());
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles.length != 0) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.getName().endsWith(".i-cc")) {
                    if (listOfFile.isFile()) {
                        String finalF = listOfFile.getName().replaceAll(".i-cc", "").trim();
                        if (finalF.length() > 12) {
                            finalF = listOfFile.getName().substring(0, 12).trim() + "..";
                        } else {
                            zipList.addElement(finalF);
                            Main.jList1.setModel(zipList);
                        }
                    }
                }
            }
            Main.jList1.setSelectedIndex(0);
            Main.refreshTreeView(Statics.receiveFolder, TreeView.receiveCaretPos);
            GUI.getGB();
        }
    }

    public static List<Path> filteredSendList = new ArrayList<>();

    private static void zipFile(String dir, String zipFile, boolean jTreeBool) {
        zipFile = sendFolderStr + ".i-cc";
        Main.jProgressBar2.setStringPainted(true);
        Main.jProgressBar2.setMaximum(zipFileCount);
        try ( FileOutputStream fos = new FileOutputStream(zipFile);  ZipOutputStream zos = new ZipOutputStream(fos)) {
            if (jTreeBool) {
                File directory = new File(dir);
                zipFileList(directory);
                for (String filePath : zipFileList) {
                    if (!filePath.endsWith(".i-cc")) {
                        File filePathF = Paths.get(filePath).toFile();
                        String name = filePath.substring(directory.getAbsolutePath().length() + 1);
                        System.out.print("zipping: " + name);
                        ZipEntry zipEntry = new ZipEntry(name);
                        zos.putNextEntry(zipEntry);
                        // Read file content and write to zip output stream.
                        try ( FileInputStream fis = new FileInputStream(filePath)) {
                            byte[] buffer = AES.dynamicBytes(filePathF);
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }

                            zos.closeEntry();
                            Statics.zipIter++;
                            Main.jProgressBar2.setValue(Statics.zipIter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                List<Path> treeViewPaths = TreeView.convertTreePathToPath(Main.jTree1.getSelectionPaths());
                treeViewPaths.forEach(x -> {
//                                System.out.println(x.toFile().getName());
                    if (!x.toFile().getName().endsWith(".enc") && !x.toFile().getName().endsWith(".i-cc")) {
                        filteredSendList.add(Paths.get(x + ".enc"));
                    }
                });
                filteredSendList.add(Paths.get(Statics.sendFolder + File.separator + Statics.keyName));
                for (Path path : filteredSendList) {
                    if (!path.toFile().getName().endsWith(".i-cc")) {
                        File filePathF = path.toFile();
                        String name = filePathF.getName();
                        ZipEntry zipEntry = new ZipEntry(name);
                        System.out.println("zipping: " + name);
                        zos.putNextEntry(zipEntry);

                        // Read file content and write to zip output stream.
                        try ( FileInputStream fis = new FileInputStream(path.toFile())) {
                            byte[] buffer = AES.dynamicBytes(filePathF);
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }

                            zos.closeEntry();
                            Statics.zipIter++;
                            Main.jProgressBar2.setValue(Statics.zipIter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFileList(File directory) {
        File[] files = directory.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    zipFileList.add(file.getAbsolutePath());
                } else {
                    zipFileList(file);
                }
            }
        }
    }

    public static void unzipFile(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileInputStream fis;
        //buffer for read and write data to file
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recursiveFileDropStoreThread(File filesf, Path path, List<Path> paths) {
        Thread t = new Thread(() -> recursiveFileDrop_T.recursiveFileStoreDrop(filesf, path, paths));
        t.start();
    }

    public static void recursiveFileDropThread(File filesf, Path path) {
        Thread t = new Thread(() -> {
            try {
                recursiveFileDrop_T.recursiveFileDrop(filesf, path);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        t.start();
    }

    public static int fileDropCount;

    public static int getFileDropCount(File filesf) {
        fileDropCount = 0;
        try {
            fileDropCount = GUI.countAllFiles(filesf.toPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fileDropCount;
    }

}

class recursiveFileDrop_T implements Runnable {

    public void run() {

    }
    public static int fileDropIter;

    public static void recursiveFileDrop(File filesf, Path path) throws IOException, InterruptedException {
        Paths.get(path + File.separator + filesf.getName()).toFile().mkdir();
        File[] filesArr = filesf.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    try {
                        Files.move(filesArr1.toPath(), Paths.get(path + File.separator + filesf.getName() + File.separator + filesArr1.getName()), StandardCopyOption.REPLACE_EXISTING);
                        AES.getFileAttr(new File(path + File.separator + filesf.getName() + File.separator + filesArr1.getName()), new File(path + File.separator + filesf.getName() + File.separator + filesArr1.getName()));
                        fileDropIter++;
                        Main.jAlertLabel.setText("moved " + fileDropIter + " files");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                String parentStr = filesArr1.getParent();
                String parentFile = Paths.get(parentStr).toFile().getName();
                recursiveFileDrop(filesArr1, Paths.get(path + File.separator + parentFile));
            }
        }
        if (fileDropIter == Folder.fileDropCount) {
            GUI.t.interrupt();
            if (Statics.toolMode == 2) {
                GUI.labelCutterThread(Main.jAlertLabel, recursiveFileDrop_T.fileDropIter + " files moved to o-box", 10, 25, 750, false);
                Main.jTextArea1.append(recursiveFileDrop_T.fileDropIter + " files moved to o-box\n");
                TreeView.populateStoreTree(Statics.sendFolder);
            }
            if (Statics.toolMode == 0 || Statics.toolMode == 3) {
                Main.jAlertLabel.setText("");
                Main.jTextArea1.append(recursiveFileDrop_T.fileDropIter + " files moved to vault\n");
                Statics.dragDropBool = false;
                Main.jButton2.setVisible(true);
                Statics.AESMode = 0;
                Statics.fileCount = GUI.countFiles(Statics.path);
                AES.AESThread(listAESPaths(Statics.path), Statics.directory, false, 0);
            }
            recursiveFileDrop_T.fileDropIter = 0;
        }
        filesf.delete();
    }

    public static void recursiveFileStoreDrop(File filesf, Path path, List<Path> recursiveStorePaths) {
        File[] filesArr = filesf.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    recursiveStorePaths.add(filesArr1.toPath());
                }
            } else {
                recursiveFileStoreDrop(filesArr1, path, recursiveStorePaths);
                if (Statics.toolMode == 2) {
                    GUI.labelCutterThread(Main.jAlertLabel, recursiveFileDrop_T.fileDropIter + " files moved to o-box", 10, 25, 750, false);
                    Main.jTextArea1.append(recursiveFileDrop_T.fileDropIter + " files moved to o-box\n");
                    TreeView.populateStoreTree(Statics.sendFolder);
                }
            }
        }
    }
}
