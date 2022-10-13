/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AES {

    public static void AESThread() throws IOException {

        com.i_comit.windows.gui.AES_T runnableWindows = new com.i_comit.windows.gui.AES_T();
        runnableWindows.threadIterator = 0;
        Thread t1 = new Thread(runnableWindows);
        t1.start();

//            for(int i=0; i< usbparser.windows.USBParse0.GetDeviceCount(); i++){
//                runnableWindows.threadIterator++;
//                Thread t =new Thread(runnableWindows);    
//                t.start();
//            }
    }

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        if (inputFile.exists()) {
            if (!inputFile.toString().endsWith(".enc")) {
                outputFile = new File(outputFile + ".enc");
                doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
                inputFile.delete();

            } else {
                System.out.println("No files to encrypt.");
                System.exit(0);
            }
        }
        else {
            System.out.println("Encryption complete!");
        }
    }

    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        if (inputFile.exists()) {
            if (inputFile.toString().endsWith(".enc")) {
                outputFile = new File(inputFile.toString().replaceAll(".enc", ""));
                doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
                inputFile.delete();
                //System.exit(0);
            } else {
                System.out.println("No encrypted files to decrypt.");
                System.exit(0);
            }
        } else {
            System.out.println("Decryption complete!");
        }

    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }

    public static class CryptoException extends Exception {

        public CryptoException() {
        }

        public CryptoException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
