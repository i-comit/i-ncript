/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import com.i_comit.shared.Hasher;
import static com.i_comit.windows.AES.decrypt;
import static com.i_comit.windows.AES.encrypt;
import static com.i_comit.windows.DragDrop.decFiles;
import static com.i_comit.windows.DragDrop.encFiles;
import static com.i_comit.windows.Login.sendKey;
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
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AES {

    public static Thread t;

    public static void AESThread(List<Path> paths, File dirFile, boolean AESBool, int toolMode) {
        t = new Thread(() -> {
            try {
                AES_T.AESQuery(paths, dirFile, AESBool, toolMode);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        t.start();
    }

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static void encrypt(String key, File inputFile, File outputFile) {
        if (inputFile.exists()) {
            if (!inputFile.toString().endsWith(".enc")) {
                try {
                    outputFile = new File(outputFile + ".enc");
                    doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
                    inputFile.delete();
                } catch (CryptoException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void decrypt(String key, File inputFile, File outputFile) {
        if (inputFile.exists()) {
            if (inputFile.toString().endsWith(".enc")) {
                try {
                    outputFile = new File(inputFile.toString().replaceAll(".enc", ""));
                    doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
                    inputFile.delete();
                } catch (CryptoException ex) {
                    ex.printStackTrace();
                }
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
    
    public static byte[] dynamicBytes(File inputFile) {
        byte[] inputBytes;
        if (inputFile.length() > Statics.maxFileBytes) {
            inputBytes = new byte[1024 * 64];
        } else {
            inputBytes = new byte[(int) inputFile.length()];
        }
        return inputBytes;
    }

    private static void doCrypto(int cipherMode, String key, File inputFile,
            File outputFile) throws CryptoException {
        try {
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);

            try ( FileInputStream inputStream = new FileInputStream(inputFile);  FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] inputBytes = dynamicBytes(inputFile);
                int nread;
                while ((nread = inputStream.read(inputBytes)) > 0) {
                    byte[] enc = cipher.update(inputBytes, 0, nread);
                    if (inputFile.length() > Statics.maxFileBytes) {
                        Memory.byteMonitor(inputStream, inputFile);
                    }
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
            GUI.loggerThread(outputFile, Statics.toolMode);
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

    public static void AESQuery(List<Path> paths, File dirFile, boolean AESBool, int toolMode) throws InterruptedException {
        AES_T.paths = paths;
        if (AESBool) {
            contents = dirFile.listFiles();
            if (contents != null) {
                if (contents.length != 0) {
                    if (!paths.isEmpty()) {
                        buttonRestart();
                        switch (AESMode) {
                            case 0:
                                Main.jProgressBar1.setStringPainted(true);
                                Main.jProgressBar1.setString("0% | " + "0/" + AES_T.paths.size());
                                if (paths.size() >= 30) {
                                    GUI.labelCutterThread(jAlertLabel, "encrypting " + paths.size() + " files", 0, 15, 1500, false);
                                }
                                paths.forEach(x -> {
                                    if (x.toFile().length() > maxFileBytes) {
                                        if (GUI.t.isAlive()) {
                                            GUI.t.interrupt();
                                        }
                                        Main.jProgressBar2.setVisible(true);
                                        Main.jAlertLabel.setText("encrypting " + x.toFile().getName());
                                    }
                                    encrypt(Hasher.hashedPassword, x.toFile(), x.toFile());
                                });
                                if (fileIter == 0) {
                                    Main.toolBtnsBool(true);
                                    Main.jProgressBar1.setVisible(false);
                                    Main.jProgressBar2.setVisible(true);
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "incorrect key", 10, 25, 500, true);
                                    FileHider.cleanUp(path);
                                    GUI.resetIncorrectKeyProgressBar(jProgressBar1);
                                } else {
                                    System.out.println("File Encryption Complete");
                                    if (toolMode == 2) {
                                        try {
                                            sendKey();
                                            Folder.prepareZipFile();
                                            Main.jLabel6.setVisible(true);
                                            Main.jLabel5.setVisible(true);
                                            Main.jRadioButton2.setVisible(false);
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    } else {
                                        GUI.resetProgressBar(jProgressBar1);
                                    }
                                }
                                break;
                            case 1:
                                jProgressBar1.setStringPainted(true);
                                if (paths.size() >= 30) {
                                    GUI.labelCutterThread(jAlertLabel, "decrypting " + paths.size() + " files", 0, 15, 1500, false);
                                }
                                paths.forEach(x -> {
                                    if (x.toFile().length() > maxFileBytes) {
                                        if (GUI.t.isAlive()) {
                                            GUI.t.interrupt();
                                        }
                                        Main.jProgressBar2.setVisible(true);
                                        Main.jAlertLabel.setText("decrypting " + x.toFile().getName());
                                    }
                                    decrypt(Hasher.hashedPassword, x.toFile(), x.toFile());
                                });
                                if (fileIter == 0) {
                                    Main.toolBtnsBool(true);
                                    Main.jProgressBar1.setVisible(false);
                                    Main.jProgressBar2.setVisible(true);
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "incorrect key", 10, 25, 500, true);
                                    FileHider.cleanUp(path);
                                    GUI.resetIncorrectKeyProgressBar(jProgressBar1);
                                } else {
                                    System.out.println("File Decryption Complete");
                                    if (toolMode == 1) {
                                        new File(Statics.zipFileName + ".i-cc").delete();
                                        new File(Statics.zipFileName + File.separator + keyName).delete();
                                        Main.jList1.clearSelection();
                                        Statics.resetSendTools(toolMode);
                                        Main.jList1.setEnabled(true);
                                        if (!Main.mouseOverLog) {
                                            Main.jTabbedPane1.setSelectedIndex(0);
                                        }
                                        Main.toolBtnsBool(true);
                                    }
                                    GUI.resetProgressBar(jProgressBar1);
                                    Statics.resetStaticInts();
                                }
                                break;
                        }
                    }
                } else {
                    switch (toolMode) {
                        case 0:
                            GUI.labelCutterThread(jAlertLabel, "vault folder has no files", 20, 40, 800, false);
                            break;
                        case 1:
                            GUI.labelCutterThread(jAlertLabel, "n-box folder has no files", 20, 40, 800, false);
                            Main.jRadioButton3.setEnabled(true);
                            break;
                    }
                    Main.jToggleButton2.setEnabled(true);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "vault folder does not exist", 20, 40, 800, false);
                Main.jToggleButton2.setEnabled(true);
            }
        } else {
            buttonRestart();
            jProgressBar1.setMaximum(paths.size());
            jProgressBar1.setStringPainted(true);
            jProgressBar1.setValue(fileIter);
            paths.forEach(x -> {
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
                    AES.decrypt(Hasher.hashedPassword, file, file);
                }
                if (!x.toString().endsWith(".enc") || x.toString().startsWith("Thumbs.db")) {
                    decFiles++;
                    if (x.toFile().length() > maxFileBytes) {
                        if (GUI.t.isAlive()) {
                            GUI.t.interrupt();
                        }
                        Main.jProgressBar2.setVisible(true);
                        Main.jAlertLabel.setText("encrypting " + x.toFile().getName());
                    }
                    AES.encrypt(Hasher.hashedPassword, file, file);
                }
            });
            if (fileIter == 0) {
                Main.toolBtnsBool(true);
                Main.dragDrop.setVisible(true);
                Main.jProgressBar1.setVisible(false);
                Main.jProgressBar2.setVisible(true);
                GUI.t.interrupt();
                GUI.labelCutterThread(Main.jAlertLabel, "incorrect key", 10, 25, 500, true);
                FileHider.cleanUp(path);
                GUI.resetIncorrectKeyProgressBar(jProgressBar1);
            } else {
                DragDrop_T.resetProgressBar(encFiles, decFiles);
                GUI.getGB();
            }
        }
    }

    private static void buttonRestart() {
        Main.jButton2.setVisible(true);
        Main.jTabbedPane1.setSelectedIndex(1);
        Main.toolBtnsBool(false);
        Main.dragDrop.setVisible(false);
        Main.jSwitchMode.setVisible(false);
        Main.jProgressBar1.setVisible(true);
        Main.jProgressBar2.setVisible(false);
    }
}
