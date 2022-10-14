/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.dev;

import static com.i_comit.windows.dev.Globals.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AESFolder {

    public static void encryptFile(String secretKey, String fileInputPath, String fileOutPath)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
            IllegalBlockSizeException, BadPaddingException {
        var key = new SecretKeySpec(secretKey.getBytes(), "AES");
        var cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        var fileInput = new File(fileInputPath);
        var inputStream = new FileInputStream(fileInput);
        var inputBytes = new byte[(int) fileInput.length()];
        inputStream.read(inputBytes);

        var outputBytes = cipher.doFinal(inputBytes);

        var fileEncryptOut = new File(fileOutPath);
        var outputStream = new FileOutputStream(fileEncryptOut);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        System.out.println("File successfully encrypted!");
        System.out.println("New File: " + fileOutPath);
        MakeFolder.deleteFolder(directory);
        Files.delete(Paths.get(root + folderName + ".zip"));
        directory.delete();
        Files.setAttribute(Paths.get(root + folderName + ".enc"), "dos:hidden", true);
        ZipFolder.AESQuery();
    }

    public static void decryptFile(String secretKey, String fileInputPath, String fileOutPath)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException,
            IllegalBlockSizeException, BadPaddingException {
        var key = new SecretKeySpec(secretKey.getBytes(), "AES");
        var cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        var fileInput = new File(fileInputPath);
        var inputStream = new FileInputStream(fileInput);
        var inputBytes = new byte[(int) fileInput.length()];
        inputStream.read(inputBytes);

        byte[] outputBytes = cipher.doFinal(inputBytes);

        var fileEncryptOut = new File(fileOutPath);
        var outputStream = new FileOutputStream(fileEncryptOut);
        outputStream.write(outputBytes);

        inputStream.close();
        outputStream.close();

        System.out.println("File successfully decrypted!");
        System.out.println("New File: " + fileOutPath);

        ZipFolder.unzip(Globals.zipPath, Globals.path);
        Files.delete(Paths.get(root + folderName + ".enc"));
        Files.delete(Paths.get(root + folderName + ".zip"));

        Files.setAttribute(path, "dos:hidden", false);
        ZipFolder.AESQuery();
    }
}
