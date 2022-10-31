/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.decrypt;
import static com.i_comit.windows.AES.encrypt;
import static com.i_comit.windows.DragDrop.decFiles;
import static com.i_comit.windows.DragDrop.encFiles;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.*;
import java.io.*;
import java.nio.file.*;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
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

    public static void AESThread(List<Path> paths, boolean AESBool) {
        t = new Thread(() -> {
            try {
                AES_T.AESQuery(paths, AESBool);
            } catch (InterruptedException ex) {
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

    public static void getFileAttr(File inputFile, File outputFile) throws IOException {
        BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);

        FileTime time = attr.creationTime();
        FileTime time2 = attr.lastModifiedTime();

        Files.setAttribute(outputFile.toPath(), "basic:creationTime", time, NOFOLLOW_LINKS);
        Files.setLastModifiedTime(outputFile.toPath(), time2);

    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {

        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            try ( FileInputStream inputStream = new FileInputStream(inputFile);  FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] inputBytes = new byte[1024 * 64];
                int nread;
                while ((nread = inputStream.read(inputBytes)) > 0) {
                    byte[] enc = cipher.update(inputBytes, 0, nread);
                    Memory.byteMonitor(inputStream, inputFile);
                    outputStream.write(enc);
                }
                byte[] enc = cipher.doFinal();
                outputStream.write(enc);
                inputStream.close();
                outputStream.close();
                System.gc();
                System.runFinalization();
            }
            fileIter++;
            float percentage = ((float) fileIter / AES_T.paths.size() * 100);
            DecimalFormat format = new DecimalFormat("0.#");
            String percentageStr = format.format(percentage);
            Main.jProgressBar1.setString(percentageStr + "% | " + fileIter + "/" + AES_T.paths.size());
            Main.jProgressBar1.setValue(fileIter);
            GUI.loggerThread(outputFile);
            getFileAttr(inputFile, outputFile);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        } catch (IOException | UncheckedIOException ex) {
            System.out.println("Last File Was " + inputFile.getName());
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

    public static List<Path> paths = null;

    public static void AESQuery(List<Path> paths, boolean AESBool) throws InterruptedException {
        AES_T.paths = paths;
        Main.jProgressBar1.setString("0% | " + "0/" + AES_T.paths.size());
        if (AESBool) {
            contents = directory.listFiles();
            //                paths = listAESPaths(path);
            if (contents != null) {
                if (contents.length != 0) {
                    if (!paths.isEmpty()) {
                        Main.toolBtnsBool(false);
                        Main.jButton2.setVisible(true);
                        Main.jTextArea5.setVisible(false);
                        switch (AESMode) {
                            case 0 -> {
                                Main.jProgressBar1.setStringPainted(true);
                                GUI.labelCutterThread(jAlertLabel, "encrypting " + paths.size() + " files", 0, 15, 300);
                                paths.forEach(x -> {
                                    try {
                                        if (x.toFile().length() > maxFileBytes) {
                                            if (GUI.t.isAlive()) {
                                                GUI.t.interrupt();
                                            }
                                            Main.jProgressBar2.setVisible(true);
                                            Main.jAlertLabel.setText("encrypting " + x.toFile().getName());
                                        }
                                        encrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                                    } catch (AES.CryptoException ex) {
                                    }
                                });
                                if (fileIter == 0) {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "incorrect key", 10, 25, 500);
                                } else {
                                    System.out.println("File Encryption Complete");
                                    GUI.resetProgressBar();
                                }

                            }
                            case 1 -> {
                                Main.jProgressBar1.setStringPainted(true);
                                GUI.labelCutterThread(jAlertLabel, "decrypting files...", 0, 15, 300);
                                paths.forEach(x -> {
                                    try {
                                        if (x.toFile().length() > maxFileBytes) {
                                            if (GUI.t.isAlive()) {
                                                GUI.t.interrupt();
                                            }
                                            Main.jProgressBar2.setVisible(true);
                                            Main.jAlertLabel.setText("decrypting " + x.toFile().getName());
                                        }
                                        decrypt(Hasher.modHash(password), x.toFile(), x.toFile());
                                    } catch (AES.CryptoException ex) {
                                    }
                                });
                                if (fileIter == 0) {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "incorrect key", 10, 25, 500);
                                } else {
                                    System.out.println("File Decryption Complete");
                                    GUI.resetProgressBar();
                                }
                            }
                        }

                    } else {
                        if (!Main.jToggleButton1.isSelected()) {
                            switch (AESMode) {
                                case 0 -> {
                                    GUI.labelCutterThread(jAlertLabel, "no files to encrypt", 10, 20, 400);
                                }
                                case 1 -> {
                                    GUI.labelCutterThread(jAlertLabel, "no files to decrypt", 10, 20, 400);
                                    Main.jToggleButton1.setEnabled(true);
                                }
                            }
                        }
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 20, 40, 800);
                    Main.jToggleButton2.setEnabled(true);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 20, 40, 800);
                Main.jToggleButton2.setEnabled(true);
            }
        } else {
            jProgressBar1.setMaximum(paths.size());
            jProgressBar1.setStringPainted(true);
            jProgressBar1.setValue(fileIter);

            paths.forEach(x -> {
                try {
                    String fileStr = x.toString();
                    File file = Paths.get(fileStr).toFile();
                    if (x.toString().endsWith(".enc") || x.toString().startsWith("Thumbs.db")) {
                        encFiles++;
                        if (x.toFile().length() > maxFileBytes) {
                            if (GUI.t.isAlive()) {
                                GUI.t.interrupt();
                            }
                            Main.jProgressBar2.setVisible(true);
                            Main.jAlertLabel.setText("decrypting " + x.toFile().getName());
                        }
                        AES.decrypt(Hasher.modHash(password), file, file);
                    }
                    if (!x.toString().endsWith(".enc")) {
                        decFiles++;
                        if (x.toFile().length() > maxFileBytes) {
                            if (GUI.t.isAlive()) {
                                GUI.t.interrupt();
                            }
                            Main.jProgressBar2.setVisible(true);
                            Main.jAlertLabel.setText("encrypting " + x.toFile().getName());
                        }
                        AES.encrypt(Hasher.modHash(password), file, file);
                    }
                    System.out.println("ENC " + encFiles + " " + decFiles);
                } catch (AES.CryptoException ex) {
                    ex.printStackTrace();
                }
            });

            if (fileIter == 0) {
                GUI.t.interrupt();
                GUI.labelCutterThread(Main.jAlertLabel, "incorrect key", 10, 25, 500);
            } else {
                DragDrop.resetProgressBar(encFiles, decFiles);
                GUI.getGB();
            }
        }
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
            switch (AESMode) {
                case 0 ->
                    result = walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).filter(p -> !p.getFileName().toString().startsWith("Thumbs.db"))
                            .collect(Collectors.toList());
                case 1 ->
                    result = walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc"))
                            .collect(Collectors.toList());
            }
        }
        return result;
    }
}
