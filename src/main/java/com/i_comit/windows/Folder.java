/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
import static com.i_comit.windows.Login.sendKey;
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
    public static String receiveFolderStr = "";

    public static void list1Dir(int toolMode) throws IOException {
        switch (toolMode) {
            case 1 -> {
                receiveFolderStr = Statics.receiveFolder + "\\" + firstLastChar(Main.jList1.getSelectedValue()) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddmmss"));
                System.out.println(receiveFolderStr);
                System.out.println(Statics.zipFileName + ".i-cc" + "    " + Statics.zipFileName.replaceAll(".i-cc", ""));

                Folder.unzipFolder(Statics.zipFileName + ".i-cc", Statics.zipFileName.replaceAll(".i-cc", ""));
                System.out.println("Unzip Complete");
                Main.toolBtnsBool(true);
                Login.verifySendKey();
            }

            case 2 -> {
                //SEND
                sendFolderStr = Statics.sendFolder + "\\" + firstLastChar(Statics.recipientUsername) + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddmmss"));
                sendKey();
                zipFolder(Statics.sendFolder, ".i-cc", sendFolderStr);
                System.out.println("Zip Complete");
                Main.toolBtnsBool(true);
                deleteDirectory(Statics.sendFolder.toFile());
            }
        }
    }

    public static String firstLastChar(String username) {
        String a = username.substring(0, 1);
        String b = username.substring(username.length() - 1, username.length());
        String c = a + b + "-";
        return c;
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

    public static void listZipFolders() {
        zipList.clear();
        Main.jList1.removeAll();
        File folder = new File(Statics.receiveFolder.toString());
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.getName().endsWith(".i-cc")) {
                if (listOfFile.isFile()) {
                    String finalF = listOfFile.getName().replaceAll(".i-cc", "").trim();
                    if (finalF.length() > 12) {
                        finalF = listOfFile.getName().substring(0, 12).trim() + "..";
                    } else {
//                    zipList.addElement(listOfFile.getName().replaceAll(".i-cc", ""));
                        zipList.addElement(finalF);
                        Main.jList1.setModel(zipList);
                    }

//                    }
                }
            }
        }
    }
    // zip a directory, including sub files and sub directories

    public static void zipFolder(Path source, String fileExt, String sendFolderPath) throws IOException {
        String zipFileName = sendFolderStr + fileExt;
        System.out.println(zipFileName);
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

    public static void unzipFolder(String zipFilePath, String destDir) {
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
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                //create directories for sub directories in zip
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
}
