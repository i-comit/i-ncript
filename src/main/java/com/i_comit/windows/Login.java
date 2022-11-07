/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES_T.listAESPaths;
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

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static void loginCheck() {
        char[] password = jPasswordField1.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        Statics.username = jTextField1.getText();
        Statics.password = new String(password);
        if (!"".equals(Statics.username)) {
            if (!"".equals(Statics.password)) {
                if (Statics.username.length() >= 4) {
                    if (Statics.password.length() >= 4) {
                        Login.Authenticator();
                    } else {
                        GUI.labelCutterThread(jAlertLabel, "please have a longer password", 20, 20, 1200);
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "please have a longer username", 20, 20, 1200);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200);
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please make a username", 20, 20, 1200);
        }
        jTextField1.setText("");
        jPasswordField1.setText("");
    }

    public static void Authenticator() {
        if (keyFile.exists()) {
            verifyPassword();
        } else {
            Main.jProgressBar1.setVisible(true);
            GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 30, 30, 600);
            makeKey();
        }
    }

    public static void makeKey() {
        Path path = keyFile.toPath();//creates Path instance  
        try {
            List<String> lines = Arrays.asList(Hasher.modHash(username), Hasher.modHash(password));
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

    public static void sendKeyCheck() throws IOException {
        char[] password = Main.jPasswordField2.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        Statics.recipientUsername = Main.jTextField2.getText();
        Statics.recipientPassword = new String(password);
        if (!"".equals(Statics.recipientUsername)) {
            if (!"".equals(Statics.recipientPassword)) {
                if (Statics.recipientUsername.length() >= 4) {
                    if (Statics.recipientPassword.length() >= 4) {
                        Main.jTextField2.setText("");
                        Main.jPasswordField2.setText("");
                        AESMode = 0;
                        fileCount = GUI.countFiles(sendFolder);
                        jProgressBar1.setMaximum(fileCount);
                        AES.AESThread(listAESPaths(sendFolder), sendFolder.toFile(), true, 2);
                    } else {
                        GUI.labelCutterThread(jAlertLabel, "please have a longer password", 20, 20, 1200);
                        jLabel6.setVisible(true);
                        jLabel5.setVisible(true);
                        jRadioButton2.setVisible(false);
                    }
                } else {
                    GUI.labelCutterThread(jAlertLabel, "please have a longer username", 20, 20, 1200);
                    jLabel6.setVisible(true);
                    jLabel5.setVisible(true);
                    jRadioButton2.setVisible(false);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200);
                jLabel6.setVisible(true);
                jLabel5.setVisible(true);
                jRadioButton2.setVisible(false);
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please make a username", 20, 20, 1200);
            jLabel6.setVisible(true);
            jLabel5.setVisible(true);
            jRadioButton2.setVisible(false);
        }

    }

    public static void sendKey() {
        Path sendKeyPath = Paths.get(Statics.sendFolder + "\\send.key");
        System.out.println("sendkey " + sendKeyPath);
        try {
            List<String> lines = Arrays.asList(Hasher.modHash(recipientUsername), Hasher.modHash(recipientPassword));
//            List<String> lines = Arrays.asList(Hasher.modHash(recipientPassword));
            Path p = Files.createFile(sendKeyPath);//creates file at specified location  
            Files.write(sendKeyPath, lines);
            Files.setAttribute(p, "dos:hidden", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void receiveKeyCheck() throws IOException {
        char[] password = Main.jPasswordField3.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        Statics.zipFileName = Statics.receiveFolder + "\\" + Main.jList1.getSelectedValue();
        Statics.zipFileIndex = Main.jList1.getSelectedIndex();
        System.out.println("zipFile Index " + Statics.zipFileIndex);
        System.out.println(Main.jList1.getSelectedValue());

        Statics.recipientPassword = new String(password);
        if (Main.jList1.getSelectedValue() != null) {
            if (!"".equals(Statics.recipientPassword)) {
                if (Statics.recipientPassword.length() >= 4) {
                    Main.jPasswordField3.setText("");
                    System.out.println("recipient pw " + Statics.recipientPassword);
                    Folder.list1Dir(1);
                } else {
                    GUI.labelCutterThread(jAlertLabel, "please have a longer password", 20, 20, 1200);
                    Main.jLabel8.setVisible(true);
                    Main.jLabel7.setVisible(true);
                    Main.jRadioButton3.setVisible(false);
                    Main.jRadioButton3.setSelected(false);
                    Main.jPasswordField3.setText("");
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200);
                Main.jLabel8.setVisible(true);
                Main.jLabel7.setVisible(true);
                Main.jRadioButton3.setVisible(false);
                Main.jRadioButton3.setSelected(false);
                Main.jPasswordField3.setText("");
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please select a .i-cc file", 20, 20, 1200);
            Main.jLabel8.setVisible(true);
            Main.jLabel7.setVisible(true);
            Main.jRadioButton3.setVisible(false);
            Main.jRadioButton3.setSelected(false);
            Main.jPasswordField3.setText("");
        }
    }

    public static void verifySendKey() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(Statics.zipFileName + "\\send.key"));
            String usernameRead = brTest.readLine();
            String passwordRead = brTest.readLine();
//            System.out.println("recipient username hash " + usernameRead);
            System.out.println("recipient password hash " + passwordRead);
            System.out.println("input password hash " + Hasher.modHash(Statics.recipientPassword));

            BufferedReader brTest1 = new BufferedReader(new FileReader(keyFile));
            String usernameRead1 = brTest1.readLine();
            String passwordRead1 = brTest1.readLine();
//
//            System.out.println("my username hash " + usernameRead1);
//            System.out.println("my password hash " + passwordRead1);

            if (usernameRead.equals(usernameRead1)) {
                System.out.println("USERNAME MATCH");
                if (passwordRead.equals(Hasher.modHash(Statics.recipientPassword))) {
                    System.out.println("PASSWORD MATCH");
                    AESMode = 1;
                    fileCount = GUI.countFiles(receiveFolder);
                    jProgressBar1.setMaximum(fileCount);
                    AES.AESThread(listAESPaths(Paths.get(Statics.zipFileName)), Paths.get(Statics.zipFileName).toFile(), true, 1);
                } else {
                    brTest.close();
                    Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile());
                    Paths.get(Statics.zipFileName).toFile().delete();
                    GUI.labelCutterThread(jAlertLabel, "mismatched credentials", 20, 20, 1000);
                    Main.jList1.clearSelection();
                    Main.jLabel8.setVisible(true);
                    Main.jLabel7.setVisible(true);
                    Main.jRadioButton3.setVisible(false);
                    Main.jRadioButton3.setSelected(false);
                    Main.jPasswordField3.setText("");

                }
            } else {
                brTest.close();
                Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile());
                Paths.get(Statics.zipFileName).toFile().delete();
                GUI.labelCutterThread(jAlertLabel, "mismatched credentials", 20, 20, 1000);
                Main.jLabel8.setVisible(true);
                Main.jLabel7.setVisible(true);
                Main.jRadioButton3.setVisible(false);
                Main.jRadioButton3.setSelected(false);
                Main.jList1.clearSelection();
                Main.jPasswordField3.setText("");

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void verifyPassword() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(keyFile));
            String usernameRead = brTest.readLine();
            String passwordRead = brTest.readLine();

            if (usernameRead.equals(Hasher.modHash(username))) {
                if (passwordRead.equals(Hasher.modHash(password))) {
                    Main.jLoginPanel.setVisible(false);
                    Main.jToolPanel.setVisible(true);
                    Main.jProgressBar1.setVisible(true);
                    GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript", 45, 30, 900);
                    Main.dragDropper();
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "incorrect login info", 45, 30, 900);

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
