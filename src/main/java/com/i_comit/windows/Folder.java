/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
import static com.i_comit.windows.Statics.zipFileCount;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.DefaultListModel;

public class Folder {

    public static String sendFolderStr = "";

    public static void prepareZipFile() throws IOException {
        //SEND
        sendFolderStr = Statics.sendFolder + File.separator + firstLastChar(Statics.recipientUsername) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddmmss"));
        zipFile(Statics.sendFolder, ".i-cc", sendFolderStr);
        GUI.resetProgressBar(Main.jProgressBar2);
        Main.toolBtnsBool(true);
        deleteDirectory(Statics.sendFolder.toFile());
        Main.jRadioButton2.setEnabled(true);
        Main.jRadioButton2.setSelected(false);
        Main.dragDrop.setVisible(false);
        GUI.getGB();
    }

    public static String firstLastChar(String username) {
        String a = username.substring(0, 1);
        String b = username.substring(username.length() - 1, username.length());
        String c = a + b + "-";
        return c;
    }

    public static String first2Char(String fileName) {
        String a = fileName.substring(0, 2);
        String b = a + "-";
        return b;
    }

    public static void deleteDirectory(File file) {
        for (File subfile : file.listFiles()) {
            if (!subfile.toString().endsWith(".i-cc")) {
                if (subfile.isDirectory()) {
                    deleteDirectory(subfile);
                }
                subfile.delete();
            }
        }
    }

    public static DefaultListModel zipList = new DefaultListModel();

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
            GUI.getGB();
        }
    }

    public static void zipFile(Path source, String fileExt, String sendFolderPath) throws IOException {
        String zipFileName = sendFolderStr + fileExt;
        Main.jProgressBar2.setStringPainted(true);
        Main.jProgressBar2.setMaximum(zipFileCount);
        try (
                 ZipOutputStream zos = new ZipOutputStream(
                        new FileOutputStream(zipFileName))) {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attributes) {
                    // only copy files, no symbolic links
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }
                    try ( FileInputStream fis = new FileInputStream(file.toFile())) {
                        if (!file.toString().endsWith(fileExt)) {
                            Path targetFile = source.relativize(file);
                            if (!file.toString().equals(zipFileName)) {
                                zos.putNextEntry(new ZipEntry(targetFile.toString()));
                            }

                            byte[] buffer = AES.dynamicBytes(file.toFile());
//                        byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }

                            zos.closeEntry();
                            System.out.printf("Send file : %s%n", file);
                            Statics.zipIter++;
                            Main.jProgressBar2.setValue(Statics.zipIter);
//                            if (Main.jProgressBar2.getValue() >= zipFileCount - 1 && zipFileCount > 10) {
//                                Main.jProgressBar2.setValue(zipFileCount);
//                                Main.jProgressBar2.setString("packing key file..");
//                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.printf("Unable to zip : %s%n%s%n", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });
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
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
//                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
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

    public static void recursiveFileDropThread(File filesf, Path path) {
        Thread t = new Thread(() -> recursiveFileDrop_T.recursiveFileDrop(filesf, path));
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

    public static void recursiveFileDrop(File filesf, Path path) {
        Paths.get(path + File.separator + filesf.getName()).toFile().mkdir();
        File[] filesArr = filesf.listFiles();
        for (int x = 0; x < filesArr.length; x++) {
            if (!filesArr[x].isDirectory()) {
                if (!filesArr[x].getName().endsWith("Thumbs.db")) {
                    try {
                        Files.move(filesArr[x].toPath(), Paths.get(path + File.separator + filesf.getName() + File.separator + filesArr[x].getName()), StandardCopyOption.REPLACE_EXISTING);
                        fileDropIter++;
                        Main.jAlertLabel.setText("moved " + fileDropIter + " files");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } else {
                System.out.println(filesArr[x]);
                System.out.println(Paths.get(path + File.separator + filesArr[x].getName()));
                String parentStr = filesArr[x].getParent();
                String parentFile = Paths.get(parentStr).toFile().getName();
//                System.out.println("W PARENT " + Paths.get(path + File.separator + parentFile + File.separator + filesArr[x].getName()));
//                System.out.println("W PARENT 2 " + parentFile);
                recursiveFileDrop(filesArr[x], Paths.get(path + File.separator + parentFile));
            }
        }
        if (fileDropIter == Folder.fileDropCount) {
            GUI.labelCutterThread(Main.jAlertLabel, recursiveFileDrop_T.fileDropIter + " files moved to o-box", 10, 25, 750, false);
            Main.jTextArea1.append(recursiveFileDrop_T.fileDropIter + " files moved to o-box\n");
            TreeView.populateStoreTree(Statics.sendFolder);
        }
        filesf.delete();
    }
}
