/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jLabel5;
import static com.i_comit.windows.Main.jLabel6;
import static com.i_comit.windows.Main.jPasswordField1;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Main.jRadioButton2;
import static com.i_comit.windows.Main.jTextField1;
import static com.i_comit.windows.Statics.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static boolean loginCheck() {
        boolean b = false;
        char[] password = jPasswordField1.getPassword();
        username = jTextField1.getText();
        Statics.password = new String(password);
        if (!"".equals(username)) {
            if (!"".equals(Statics.password)) {
                if (username.length() >= 5) {
                    if (username.length() <= 10) {
                        if (Statics.password.length() >= 5) {
                            if (Statics.password.length() <= 10) {
                                if (!username.equals(Statics.password)) {
                                    Login.Authenticator();
                                    b = true;
                                } else {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "password can't be username", 20, 20, 1200, false);
                                }
                            } else {
                                GUI.t.interrupt();
                                GUI.labelCutterThread(jAlertLabel, "password is too long", 20, 20, 1200, false);
                            }
                        } else {
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "password is too short", 20, 20, 1200, false);
                        }
                    } else {
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "username is too long", 20, 20, 1200, false);
                    }
                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "username is too short", 20, 20, 1200, false);
                }
            } else {
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "enter a password", 20, 20, 1200, false);
            }
        } else {
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "enter a username", 20, 20, 1200, false);
        }
        jAlertLabel.setHorizontalAlignment(CENTER);
        jTextField1.setText("");
        jPasswordField1.setText("");
        return b;
    }

    private static boolean Authenticator() {
        boolean b = false;
        if (keyFile.exists()) {
            verifyLogin();
        } else {
            Main.jProgressBar1.setVisible(false);
            Main.jProgressBar2.setVisible(true);
            Hasher.hashedUsername = Hasher.getHash(username, true);
            Hasher.hashedPassword = Hasher.getHash(password, false);
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 30, 30, 600, false);
            makeKey();
            b = true;
        }
        return b;
    }

    private static void makeKey() {
        Path path = keyFile.toPath();//creates Path instance  
        try {
            List<String> lines = Arrays.asList(Hasher.finalizeHash(username, true), Hasher.finalizeHash(password, false));
            Path p = Files.createFile(path);//creates file at specified location  
            Files.write(path, lines);
            Files.setAttribute(p, "dos:hidden", true);
            Main.jLoginPanel.setVisible(false);
            Main.jToolPanel.setVisible(true);
            Main.dragDropper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendPanelTools() {
        jLabel6.setVisible(true);
        jLabel5.setVisible(true);
        jRadioButton2.setVisible(false);
    }

    public static boolean sendKeyCheck() throws IOException {
        boolean b = false;
        char[] password = Main.jPasswordField2.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        recipientUsername = Main.jTextField2.getText();
        recipientPassword = new String(password);
        if (!"".equals(recipientUsername)) {
            if (!"".equals(recipientPassword)) {
                if (recipientUsername.length() >= 5 && recipientUsername.length() <= 10) {
                    if (!username.equals(Statics.password)) {
                        Hasher.hashedUsername = Hasher.getHash(recipientUsername, true);
                        Hasher.hashedPassword = Hasher.getHash(recipientPassword, false);
                        Main.jRadioButton2.setEnabled(false);
                        Main.jTextField2.setText("");
                        Main.jPasswordField2.setText("");
                        AESMode = 0;
                        fileCount = GUI.countFiles(sendFolder);
                        zipFileCount = fileCount;
                        jProgressBar1.setMaximum(zipFileCount);
                        AES.AESThread(listAESPaths(sendFolder), sendFolder.toFile(), true, 2);
                        b = true;
                    } else {
                        GUI.t.interrupt();
                        GUI.labelCutterThread(jAlertLabel, "password can't be username", 20, 20, 1200, false);
                        sendPanelTools();
                    }
                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "invalid username", 20, 20, 1200, false);
                    sendPanelTools();
                }
            } else {
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200, false);
                sendPanelTools();
            }
        } else {
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "please make a username", 20, 20, 1200, false);
            sendPanelTools();
        }
        return b;
    }

    public static void sendKey() {
        Path sendKeyPath = Paths.get(Statics.sendFolder + "\\send.key");
        try {
            List<String> lines = Arrays.asList(Hasher.finalizeHash(recipientUsername, true), Hasher.finalizeHash(recipientPassword, false));
//            List<String> lines = Arrays.asList(Hasher.modHash(recipientPassword));
            Path p = Files.createFile(sendKeyPath);//creates file at specified location  
            Files.write(sendKeyPath, lines);
            Files.setAttribute(p, "dos:hidden", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receivePanelTools() {
        Main.jLabel8.setVisible(true);
        Main.jLabel7.setVisible(true);
        Main.jRadioButton3.setVisible(false);
        Main.jRadioButton3.setSelected(false);
    }

    public static void receiveKeyCheck(String zipFileN) throws IOException {
        char[] password = Main.jPasswordField3.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }

        zipFileName = zipFileN;
        System.out.println(Main.jList1.getSelectedValue());

        recipientPassword = new String(password);
        if (Main.jList1.getSelectedValue() != null) {
            if (!"".equals(recipientPassword)) {
                Hasher.hashedUsername = Hasher.getHash(username, true);
                Hasher.hashedPassword = Hasher.getHash(recipientPassword, false);
                Main.jPasswordField3.setText("");
                Folder.list1Dir(1);
            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200, false);
                receivePanelTools();
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please select a .i-cc file", 20, 20, 1200, false);
            receivePanelTools();
            Main.jPasswordField3.setText("");
        }

    }

    public static void verifySendKey() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(zipFileName + "\\send.key"));
            String usernameRead = Hasher.readKey(brTest.readLine(), username);
            String passwordRead = Hasher.readKey(brTest.readLine(), recipientPassword);

            BufferedReader brTest1 = new BufferedReader(new FileReader(keyFile));
            String usernameRead1 = Hasher.readKey(brTest1.readLine(), username);
//
            if (usernameRead.equals(usernameRead1)) {
                System.out.println("USERNAME MATCH");
                if (passwordRead.equals(Hasher.getHash(recipientPassword, false))) {
                    System.out.println("PASSWORD MATCH");
                    Statics.fileIter = 0;
                    Statics.fileCount = 0;
                    jProgressBar1.setValue(Statics.fileIter);
                    jProgressBar1.setValue(Statics.fileCount);
                    AESMode = 1;
                    fileCount = GUI.countFiles(receiveFolder);
                    zipFileCount = fileCount;
                    jProgressBar1.setMaximum(fileCount);
                    AES.AESThread(listAESPaths(Paths.get(Statics.zipFileName)), Paths.get(Statics.zipFileName).toFile(), true, 1);
                } else {
                    brTest.close();
                    Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile());
                    Paths.get(Statics.zipFileName).toFile().delete();
                    GUI.labelCutterThread(jAlertLabel, "mismatched credentials", 20, 20, 1000, false);
                    Main.jList1.clearSelection();
                    receivePanelTools();
                    Main.jPasswordField3.setText("");
                    Main.jRadioButton3.setEnabled(true);
                    Folder.listZipFiles();
                    Main.toolBtnsBool(true);
                }
            } else {
                brTest.close();
                Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile());
                Paths.get(Statics.zipFileName).toFile().delete();
                GUI.labelCutterThread(jAlertLabel, "mismatched credentials", 20, 20, 1000, false);
                receivePanelTools();
                Main.jList1.clearSelection();
                Main.jPasswordField3.setText("");
                Main.jRadioButton3.setEnabled(true);
                Folder.listZipFiles();
                Main.toolBtnsBool(true);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean verifyLogin() {
        boolean b = false;
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(keyFile));
            String usernameRead = Hasher.readKey(brTest.readLine(), username);
            String passwordRead = Hasher.readKey(brTest.readLine(), password);
            if (usernameRead.equals(Hasher.getHash(username, true))) {
                if (passwordRead.equals(Hasher.getHash(password, false))) {
                    Hasher.hashedUsername = Hasher.getHash(username, true);
                    Hasher.hashedPassword = Hasher.getHash(password, false);
                    Main.jLoginPanel.setVisible(false);
                    Main.jToolPanel.setVisible(true);
                    Main.jProgressBar2.setVisible(true);
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript, " + username + ".", 20, 40, 1200, false);
                    Main.dragDropper();
                    Main.jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
                    Main.jLabel10.setToolTipText("drop box will encrypt & decrypt any files dropped here");
                    Main.jToolPanel.requestFocus();
                    b = true;
                }
            } else {

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
