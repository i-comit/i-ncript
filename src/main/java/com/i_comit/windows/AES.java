/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.decrypt;
import static com.i_comit.windows.AES.encrypt;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Statics.*;
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
        Thread t = new Thread(() -> {
            try {
                AES_T.AESQuery();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        t.start();
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

            }
        }
    }

    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        if (inputFile.exists()) {
            if (inputFile.toString().endsWith(".enc")) {
                outputFile = new File(inputFile.toString().replaceAll(".enc", ""));
                doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
                inputFile.delete();
            }
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
//        try {
//            AESQuery();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void AESQuery() throws IOException {
        List<Path> paths = listFiles(path);
        File[] contents = directory.listFiles();

        switch (AESMode) {
            case 0 -> {
                if (contents != null) {
                    if (contents.length != 0) {
                        paths.forEach(x -> {
                            try {
                                encrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                            } catch (AES.CryptoException ex) {
                                ex.printStackTrace();
                            }
                        });
                    } else {
                        GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files",40, 1000);
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist",40,1000);
                }
            }
            case 1 -> {
                if (contents != null) {
                    if (contents.length != 0) {
                        paths.forEach(x -> {
                            try {
                                decrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                            } catch (AES.CryptoException ex) {
                                ex.printStackTrace();
                            }
                        });
                    } else {
                        GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files",40,1000);

                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist",40,1000);

                }
            }
            default -> {
            }
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
            Path rootPath = Paths.get(rootFolder);
            rootPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    List<Path> paths = listFiles(path);
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
        List<Path> paths = listNewFiles(path);
        paths.forEach(x -> System.out.println(x));
    }
}
