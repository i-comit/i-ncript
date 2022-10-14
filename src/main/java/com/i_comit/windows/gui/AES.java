/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import static com.i_comit.windows.gui.AES.decrypt;
import static com.i_comit.windows.gui.AES.encrypt;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AES {
    public static void AESThread() throws IOException {

        AES_T aesThread = new AES_T();
        aesThread.threadIterator = 0;
        Thread t1 = new Thread(aesThread);
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
                //System.exit(0);
                return;
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
                System.out.println("No files to decrypt.");
                //System.exit(0);
                return;
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

class AES_T implements Runnable {
    public int threadIterator;
    public void run() {
        try {
            AESQuery();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void AESQuery() throws IOException {
        Globals.encKeyString = "Mary has one cat";
        List<Path> paths = listFiles(Globals.path);
        paths.forEach(x -> System.out.println(x));

        switch (Globals.AESMode) {
            case 0:
                paths.forEach(x -> {
                    try {
                        encrypt(Globals.encKeyString, x.toFile(), x.toFile());
                    } catch (AES.CryptoException ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            case 1:
                paths.forEach(x -> {
                    try {
                        decrypt(Globals.encKeyString, x.toFile(), x.toFile());
                    } catch (AES.CryptoException ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            default:
                break;
        }
        //folderWatcher();
    }

    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<Path> listNewFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static void folderWatcher() throws IOException {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path rootPath = Paths.get(Globals.rootFolder);
            rootPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    List<Path> paths = listFiles(Globals.path);
                    paths.forEach(y -> System.out.println(y));
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    getLastModified();
                }
                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewFiles(Globals.path);
        paths.forEach(x -> System.out.println(x));
    }
}
