/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.decrypt;
import static com.i_comit.windows.AES.encrypt;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.*;
import java.io.*;
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

    public static Thread t;

    public static void AESThread() {
        t = new Thread(() -> AES_T.AESQuery());
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
                Statics.fileIter++;
                jProgressBar1.setValue(Statics.fileIter);
//                System.out.println("File Iterator  " + Statics.fileIter);
            }
        }
    }

    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException {
        if (inputFile.exists()) {
            if (inputFile.toString().endsWith(".enc")) {
                outputFile = new File(inputFile.toString().replaceAll(".enc", ""));
                doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
                Statics.fileIter++;
                jProgressBar1.setValue(Statics.fileIter);
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
            AES.t.interrupt();
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (IOException | UncheckedIOException ex) {
            //System.out.println("Last File Was " + inputFile.getName());

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

    public static void AESQuery() {
        contents = directory.listFiles();

        if (AES.t.isAlive()) {
            AES.t.interrupt();
        }

        try {
            List<Path> paths = listAESPaths(path);
            if (contents != null) {
                if (contents.length != 0) {
                    if (!paths.isEmpty()) {
                        Main.jRadioButton0.setEnabled(false);
                        Main.jRadioButton1.setEnabled(false);
                        switch (Statics.AESMode) {
                            case 0:
                                GUI.progressBarThread();
                                GUI.labelCutterThread(jAlertLabel, "encrypting files...", 0, 15, 300);
                                Main.jRadioButton1.setVisible(false);
                                paths.forEach(x -> {
                                    try {
                                        encrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                                    } catch (AES.CryptoException ex) {
                                    }
                                });
                                break;
                            case 1:
                                GUI.progressBarThread();
                                GUI.labelCutterThread(jAlertLabel, "decrypting files...", 0, 15, 300);
                                Main.jRadioButton0.setVisible(false);
                                paths.forEach(x -> {
                                    try {
                                        decrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                                    } catch (AES.CryptoException ex) {
                                    }
                                });
                                break;
                        }

                    } else {
                        if (!Main.jToggleButton1.isSelected()) {
                            switch (Statics.AESMode) {
                                case 0 -> {
                                    GUI.labelCutterThread(jAlertLabel, "no files to encrypt", 10, 20, 400);

                                }
                                case 1 -> {
                                    GUI.labelCutterThread(jAlertLabel, "no files to decrypt", 10, 20, 400);

                                }
                            }
                        }
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 20, 40, 800);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 20, 40, 800);
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
        }

//        try {
//            List<Path> paths = listAESPaths(path);
//            if (paths.isEmpty() && jProgressBar1.getValue() == 0) {
//
//            }
//        } catch (IOException | UncheckedIOException ex) {
//            System.out.println("USB disconnected");
//        }
    }

    public static List<Path> listPaths(Path path) throws IOException {
        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            return result;
        }

    }

    public static List<Path> listAESPaths(Path path) throws IOException {

        List<Path> result = null;
        try ( Stream<Path> walk = Files.walk(path)) {
            switch (Statics.AESMode) {
                case 0:
                    result = walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc"))
                            .collect(Collectors.toList());
                    break;
                case 1:
                    result = walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc"))
                            .collect(Collectors.toList());
                    break;
            }
        }
        return result;
    }
}
