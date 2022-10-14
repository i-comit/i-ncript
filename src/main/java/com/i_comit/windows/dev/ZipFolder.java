/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.dev;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
import static com.i_comit.windows.dev.Globals.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.crypto.*;

public class ZipFolder {

    // zip a directory, including sub files and sub directories
    public static void zipFolder(Path source) throws IOException {

        // get folder name as zip file name
        String zipFileName = root + source.getFileName().toString() + ".zip";
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

                        Path targetFile = source.relativize(file);
                        zos.putNextEntry(new ZipEntry(targetFile.toString()));

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }

                        // if large file, throws out of memory
                        //byte[] bytes = Files.readAllBytes(file);
                        //zos.write(bytes, 0, bytes.length);
                        zos.closeEntry();

                        System.out.printf("Zip file : %s%n", file);

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

    public static void unzip(Path sourceFolder, Path targetFolder) throws IOException {
        try ( ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(sourceFolder.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                // Check for zip slip vulnerability attack
                Path newUnzipPath = zipSlipVulnerabilityProtect(zipEntry, targetFolder);

                boolean isDirectory = false;
                //check for files or directory
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                if (isDirectory) {
                    Files.createDirectories(newUnzipPath);
                } else {

                    if (newUnzipPath.getParent() != null) {
                        if (Files.notExists(newUnzipPath.getParent())) {
                            Files.createDirectories(newUnzipPath.getParent());
                        }
                    }

                    // copy files using nio
                    Files.copy(zipInputStream, newUnzipPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
        }
    }

    public static Path zipSlipVulnerabilityProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        /**
         * resolve(String other) method of java. nio. file.Path used to converts
         * a given path string to a Path and resolves it against this Path in
         * the exact same manner as specified by the resolve method
         */
        Path dirResolved = targetDir.resolve(zipEntry.getName());

        /**
         * Normalizing a path involves modifying the string that identifies a
         * path or file so that it conforms to a valid path on the target
         * operating system.
         */
        //normalize the path on target directory or else throw exception
        Path normalizePath = dirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Invalid zip: " + zipEntry.getName());
        }

        return normalizePath;
    }

    public static void AESQuery() throws IOException {
        Scanner myObj = new Scanner(System.in);
        System.out.println("0 to Encrypt | 1 to Decrypt | 2 to Exit");

        // create output directory if it doesn't exist
        Integer q1 = Integer.parseInt(myObj.nextLine());  // Read user input
        if (q1 == 0) {
            if (!Paths.get(root + "encrypted-folder.enc").toFile().exists()) {
                try {
                    zipFolder(path);
                    AESFolder.encryptFile(encKeyString, root + "encrypted-folder.zip", root + "encrypted-folder.enc");
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("No files to encrypt.");
                AESQuery();
            }
        }
        File dir = new File(root + folderName + ".enc");

        if (q1 == 1) {
            if (Paths.get(root + "encrypted-folder.enc").toFile().exists()) {
                try {
                    AESFolder.decryptFile(Globals.encKeyString, root + "encrypted-folder.enc", root + "encrypted-folder.zip");
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                    ex.printStackTrace();
                }
            } else {
                System.out.println("No files to decrypt.");
                AESQuery();
            }
        }
        if(q1 == 2){
            System.exit(0);
        }
    }
}
