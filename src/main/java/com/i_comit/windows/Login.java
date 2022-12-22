/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import com.i_comit.shared.Client;
import com.i_comit.shared.Hasher;
import static com.i_comit.windows.Folder.unzipFile;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Login {

    public static boolean loginCheck(Main main) throws UnsupportedEncodingException {
        boolean b = false;
        username = jTextField1.getText().trim();
        char[] password = jPasswordField1.getPassword();
        Statics.password = new String(password).trim();
        if (!Main.jUsernameLabel.getText().equals("enter username")) {
            if (!"".equals(username)) {
                if (!"".equals(Statics.password)) {
                    if (username.length() >= 5) {
                        if (username.length() <= 11) {
                            if (Statics.password.length() >= 8) {
                                if (Statics.password.length() <= 16) {
                                    if (!username.equals(Statics.password)) {
                                        String regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!<>~:;])";
                                        Pattern p = Pattern.compile(regex);
                                        Matcher m = p.matcher(Statics.password);
                                        if (m.lookingAt()) {
                                            Login.Authenticator();
                                            b = true;
                                        } else {
                                            GUI.t.interrupt();
                                            GUI.labelCutterThread(jAlertLabel, "password failed regex check", 20, 20, 1200, false);
                                        }
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
                    GUI.labelCutterThread(jAlertLabel, "please enter a password", 20, 20, 1200, false);
                }
            } else {
                GUI.t.interrupt();
                GUI.labelCutterThread(jAlertLabel, "please enter a username", 20, 20, 1200, false);
            }
            jTextField1.setText("");
            jPasswordField1.setText("");
        } else {
            verifyLogin();
            b = true;
        }
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
            jAlertLabel.setHorizontalAlignment(CENTER);
            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript, " + username + ".", 20, 40, 1200, false);
            Main.dragDropper();
            Main.jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
            Main.jToolPanel.requestFocus();
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
        Main.jTextField2.setText("");
        Main.jPasswordField2.setText("");
        jLabel6.setVisible(true);
        jLabel5.setVisible(true);
        jRadioButton2.setVisible(false);
    }

    public static boolean sendKeyCheck(boolean jTreeBool) throws IOException {
        boolean b = false;
        char[] password = Main.jPasswordField2.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        recipientUsername = Main.jTextField2.getText().trim();
        recipientPassword = new String(password).trim();
        if (!"".equals(recipientUsername)) {
            if (!"".equals(recipientPassword)) {
                if (recipientUsername.length() >= 5 && recipientUsername.length() <= 11) {
                    if (!recipientUsername.equals(Statics.recipientPassword)) {
                        resetStaticInts();
                        Hasher.hashedUsername = Hasher.getHash(recipientUsername, true);
                        Hasher.hashedPassword = Hasher.getHash(recipientPassword, false);
                        Main.jSendSQL.setEnabled(false);
                        Main.jRadioButton2.setEnabled(false);
                        Main.jTextField2.setText("");
                        Main.jPasswordField2.setText("");
                        Main.jTextField2.setEnabled(false);
                        Main.jPasswordField2.setEnabled(false);
                        if (jTreeBool) {
                            AES.AESThread(listAESPaths(sendFolder), sendFolder.toFile(), true, 2);
                            b = true;
                        } else {
                            List<Path> treeViewPaths = TreeView.convertTreePathToPath(Main.jTree1.getSelectionPaths());
                            List<Path> filteredSendPath = new ArrayList<>();
                            System.out.println("ORIG PATH " + treeViewPaths);
                            treeViewPaths.forEach(x -> {
//                                System.out.println(x.toFile().getName());
                                if (!x.toFile().getName().endsWith(".enc") && !x.toFile().getName().endsWith(".i-cc")) {
                                    filteredSendPath.add(x);
                                }
                            });
                            if (filteredSendPath.isEmpty()) {
                                GUI.t.interrupt();
                                GUI.labelCutterThread(jAlertLabel, "folder can't contain .enc files", 20, 30, 1500, false);
                                b = false;
                            } else {
                                System.out.println("FILTERED PATH " + filteredSendPath);
                                System.out.println(recipientUsername);
                                AES.AESThread(filteredSendPath, sendFolder.toFile(), true, 2);
                                b = true;
                            }
                            filteredSendPath.clear();
                        }
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
        Path sendKeyPath = Paths.get(Statics.sendFolder + File.separator + keyName);
        try {
            List<String> lines = Arrays.asList(Hasher.finalizeHash(recipientUsername, true), Hasher.finalizeHash(recipientPassword, false));
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

    private static void resetInvalidSendKey(BufferedReader brTest) throws IOException {
        try (brTest) {
            resetStaticInts();
        }
        Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile(), Main.jTree1.isSelectionEmpty());
        Paths.get(Statics.zipFileName).toFile().delete();
        GUI.labelCutterThread(jAlertLabel, "invalid credentials", 20, 20, 1000, false);
        Main.jList1.clearSelection();
        receivePanelTools();
        Main.jPasswordField3.setText("");
        Main.jRadioButton3.setEnabled(true);
        Folder.listZipFiles();
        Main.toolBtnsBool(true);
    }

    public static boolean verifySendKey(String zipFileN) {
        boolean b = false;
        char[] password = Main.jPasswordField3.getPassword();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        zipFileName = zipFileN;
        String recipientPasswordStr = new String(password);
        if (Main.jList1.getSelectedValue() != null) {
            if (!"".equals(recipientPasswordStr)) {
                Hasher.hashedUsername = Hasher.getHash(username, true);
                Main.jPasswordField3.setText("");
                Main.jRadioButton3.setEnabled(false);
                unzipFile(Statics.zipFileName + ".i-cc", Statics.zipFileName.replaceAll(".i-cc", ""));
                Main.toolBtnsBool(true);
                try {
                    BufferedReader brTest = new BufferedReader(new FileReader(zipFileName + File.separator + keyName));
                    String usernameRead = Hasher.readKey(brTest.readLine(), username);
                    String passwordRead = Hasher.readKey(brTest.readLine(), recipientPasswordStr);

                    BufferedReader brTest1 = new BufferedReader(new FileReader(keyFile));
                    String usernameRead1 = Hasher.readKey(brTest1.readLine(), username);
//
                    if (usernameRead.equals(usernameRead1)) {
                        if (passwordRead.equals(Hasher.getHash(recipientPasswordStr, false))) {
                            Hasher.hashedPassword = Hasher.getHash(recipientPasswordStr, false);
                            resetStaticInts();
                            jProgressBar1.setValue(Statics.fileIter);
                            jProgressBar1.setValue(Statics.fileCount);
                            AESMode = 1;
                            fileCount = GUI.countFiles(receiveFolder);
                            zipFileCount = fileCount;
                            jProgressBar1.setMaximum(fileCount);
                            Main.jPasswordField3.setEnabled(false);
                            Main.jList1.setEnabled(false);
                            AES.AESThread(listAESPaths(Paths.get(Statics.zipFileName)), Paths.get(Statics.zipFileName).toFile(), true, 1);
                            Main.progressbarBool = false;
                            b = true;
                        } else {
                            resetInvalidSendKey(brTest);
                        }
                    } else {
                        resetInvalidSendKey(brTest);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } else {
                GUI.labelCutterThread(jAlertLabel, "please make a password", 20, 20, 1200, false);
                receivePanelTools();
            }
        } else {
            GUI.labelCutterThread(jAlertLabel, "please select an .i-cc file", 20, 20, 1200, false);
            receivePanelTools();
            Main.jPasswordField3.setText("");
        }
        return b;
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
                    jAlertLabel.setHorizontalAlignment(CENTER);
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript, " + username + ".", 20, 40, 1200, false);
                    Main.dragDropper();
                    Main.jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
                    Main.jToolPanel.requestFocus();
                    Main.refreshTreeView(path, TreeView.nodeCaretPos);
                    if (Client.internetBool) {
                        Client.postTable(username);
                    }
                    b = true;
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return b;
    }
}
