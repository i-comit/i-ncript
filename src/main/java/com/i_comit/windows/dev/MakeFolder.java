/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.dev;

import static com.i_comit.windows.dev.Globals.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class MakeFolder {

    public boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try ( Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }

        return false;
    }

    public static void CreateFolder() {
        File directory = new File(root + folderName);
        if (!directory.exists()) {
            directory.mkdir();
            //Files.setAttribute(path, "dos:hidden", true);
            // If you require it to make the entire directory path including parents, use directory.mkdirs(); here instead.
            {
                System.out.println("You can now fill encrypted-folder with data");
                System.exit(0);
            }
        } else {
            try {
                boolean isEmptyDirectory = Files.list(directory.toPath()).findAny().isEmpty();
                emptyDirectory = isEmptyDirectory;
                if (emptyDirectory) {
                    System.out.println("Please fill encrypted-folder first");
                    System.exit(0);
                }
                else{
                    Authenticator.verifyPassword();
                }

                ZipFolder.AESQuery();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void EncryptFolder() {
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            try {
                System.out.println(files[i].getName());
                byte[] fileContent = Files.readAllBytes(files[i].toPath());

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (!Files.isDirectory(path)) {
            System.out.println("Please provide a folder.");
            return;
        }

        try {
            ZipFolder.AESQuery();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }

    public static void deleteFolder(File file) {
        // store all the paths of files and folders present 
        // inside directory 
        for (File subfile : file.listFiles()) {

            // if it is a subfolder,e.g Rohan and Ritik, 
            // recursively call function to empty subfolder 
            if (subfile.isDirectory()) {
                deleteFolder(subfile);
            }

            // delete files and empty subfolders 
            subfile.delete();
        }
    }

}
