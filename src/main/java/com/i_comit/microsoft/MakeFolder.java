/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.microsoft;

import static com.i_comit.microsoft.Globals.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class MakeFolder {

    public static void CreateFolder() {
        System.out.println("Password hashed");
        File directory = new File(root + folderName);
        if (!directory.exists()) {
            try {
                directory.mkdir();
                Files.setAttribute(path, "dos:hidden", true);
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
                ZipFolder.AESQuery();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
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
                String bitStr = fileContent.toString();
                System.out.println("bitString: " + bitStr);
                //ZipFolder();

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
    
        public static void deleteFolder(File file) 
    { 
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
