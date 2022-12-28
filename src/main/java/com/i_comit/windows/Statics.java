/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import com.i_comit.server.Client;
import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.HotFiler_T.watchService;
import static com.i_comit.windows.Main.*;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import static javax.swing.SwingConstants.LEFT;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static int toolMode = 0;

    public static final String folderName = "ᴠᴀᴜʟᴛ";
    public static String rootFolder = root.substring(0, 3) + Main.masterFolder + folderName;
    public static final String keyName = ".➫.🔑";

    public static final String nBoxName = "ɴ-ʙᴏx";
    public static final String oBoxName = "ᴏ-ʙᴏx";

    public static int maxFileBytes = 1048576 * 4;
    public static int portNumber = 8665;

    public static int AESMode;
    public static String GB;

    public static boolean hotFilerBool = false;
    public static boolean fileHiderBool = false;
    public static boolean dragDropBool = false;
    public static boolean fileTreeBool = false;

    public static File directory = new File(root + Main.masterFolder + folderName);
    public static File keyFile = Paths.get(root + Main.masterFolder + keyName).toFile();

    public static File[] contents = null;

    public static Path path = Paths.get(root + Main.masterFolder + folderName);
    public static Path sendFolder = Paths.get(root + Main.masterFolder + oBoxName);
    public static Path receiveFolder = Paths.get(root + Main.masterFolder + nBoxName);

    public static String username = "";
    public static String password = "";

    public static String recipientUsername = "";
    public static String recipientPassword = "";

    public static String zipFileName = "";
    public static int zipFileCount;
    public static int zipIter;

    public static int fileCount;
    public static int fileIter;

    public static int fileHideIter;

    public static Font customFont;

    public static Font registerCustomFont(float fontSize, URL fontFile) {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile.openStream()).deriveFont(fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return customFont;
    }

    public static void resetStaticInts() {
        fileIter = 0;
        fileCount = 0;
        FileHider.fileCt = 0;
        fileHideIter = 0;
    }

    public static void encryptFunction(Main main) {
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        jAlertLabel.setText("");
        jProgressBar1.setValue(0);
        jProgressBar1.setMaximum(0);
        jProgressBar1.setVisible(true);
        Statics.dragDropBool = false;

        try {
            jProgressBar1.setMaximum(fileCount);
            jProgressBar1.setString("0% | 0/" + fileCount);
            AES.AESThread(listAESPaths(path), directory, true, 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void decryptFunction(Main main) {
        jToggleButton1.setSelected(false);
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        jAlertLabel.setText("");
        jProgressBar1.setValue(0);
        jProgressBar1.setMaximum(0);
        jProgressBar1.setVisible(true);
        Statics.dragDropBool = false;

        try {
            jProgressBar1.setMaximum(fileCount);
            jProgressBar1.setString("0% | 0/" + fileCount);
            AES.AESThread(listAESPaths(path), directory, true, 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void switchToolPanels() {
        jRadioButton2.setVisible(false);
        toolMode++;
        jTextField2.setText("");
        jPasswordField2.setText("");
        jPasswordField3.setText("");
        GUI.getGB();

        switch (toolMode) {
            case 1 -> {
                jSwitchMode.setText("N-BOX");
                jSwitchMode.setToolTipText("current panel can decrypt .i-cc files inside n-box (inbox) folder");
                jStorePanel.setVisible(false);
                jSendPanel.setVisible(false);
                jReceivePanel.setVisible(true);
                jTree1.setDragEnabled(false);
                jLabel5.setVisible(false);
                jLabel6.setVisible(false);
                jRadioButton2.setVisible(false);
                jRadioButton2.setSelected(false);
                new Thread(() -> {
                    Client.userRequest(username);
                }).start();
                Folder.listZipFiles();
                jLabel10.setText("N-BOX MODE");
                jLabel11.setText("MOVE .I-CC TO N-BOX");
                dragDrop.setToolTipText("drop box will move dropped .i-cc file to n-box folder");
                FileHider.cleanUp(receiveFolder);
                TreeView.setRootName("ɴ-ʙᴏx");
                Main.refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
            }
            case 2 -> {
                resetSendTools(2);
                jSwitchMode.setText("O-BOX");
                jSwitchMode.setToolTipText("current panel can encrypt files for sending from o-box (outbox) folder");
                jStorePanel.setVisible(false);
                jSendPanel.setVisible(true);
                jLabel6.setVisible(true);
                jLabel5.setVisible(true);
                jReceivePanel.setVisible(false);
                jTree1.setDragEnabled(false);

                jLabel7.setVisible(false);
                jLabel8.setVisible(false);
                jRadioButton3.setVisible(false);
                jRadioButton3.setSelected(false);
                jSendSQL.setVisible(false);

                jLabel10.setText("O-BOX MODE");
                jLabel11.setText("MOVE FILES TO O-BOX");
                dragDrop.setToolTipText("drop box will move files or folder into o-box folder");
                FileHider.cleanUp(sendFolder);
                TreeView.setRootName("ᴏ-ʙᴏx");
                Main.refreshTreeView(sendFolder, TreeView.sendCaretPos);
            }
            case 3 -> {
                jSwitchMode.setText("STORE");
                jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
                jStorePanel.setVisible(true);
                jSendPanel.setVisible(false);
                jLabel7.setVisible(true);
                jLabel8.setVisible(true);
                jReceivePanel.setVisible(false);
                jTree1.setDragEnabled(true);

                jLabel10.setText("STORE MODE");
                jLabel11.setText("ENCRYPT & DECRYPT");
                dragDrop.setToolTipText("drop box will encrypt & decrypt any files dropped here");
                FileHider.cleanUp(path);
                TreeView.setRootName("ᴠᴀᴜʟᴛ");
                Main.refreshTreeView(path, TreeView.nodeCaretPos);
                toolMode = 0;
            }
        }
    }

    public static void stopFunction(Main main) {
        if (jToggleButton1.isSelected()) {
            jToggleButton1.setSelected(false);
            jRadioButton1.setVisible(true);
            HotFiler.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "hot filer disabled", 0, 25, 1000, false);
        }
        AES.t.interrupt();
        AES.t.stop();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        jButton2.setVisible(false);
        jTabbedPane1.setSelectedIndex(0);
        jProgressBar1.setValue(fileIter);
        jProgressBar1.setMaximum(fileCount);
        jProgressBar1.setVisible(false);

        jProgressBar2.setMaximum(0);
        jProgressBar2.setValue(jProgressBar2.getMaximum());
        jProgressBar2.setStringPainted(false);
        jProgressBar2.setVisible(true);
        buttonGroup1.clearSelection();
        jAlertLabel.setText("");
        dragDrop.setVisible(true);

        toolBtnsBool(true);

        switch (toolMode) {
            case 0:
                FileHider.cleanUp(path);
                refreshTreeView(path, TreeView.nodeCaretPos);
                break;
            case 1:
                Folder.deleteDirectory(Paths.get(Statics.zipFileName).toFile(), Main.jTree1.isSelectionEmpty());
                new File(Statics.zipFileName).delete();
                FileHider.cleanUp(receiveFolder);
                refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                resetSendTools(toolMode);
                break;
            case 2:
                FileHider.cleanUp(sendFolder);
                refreshTreeView(sendFolder, TreeView.sendCaretPos);
                resetSendTools(toolMode);
                break;
            case 3:
                FileHider.cleanUp(path);
                refreshTreeView(path, TreeView.nodeCaretPos);
                break;
        }
        main.setSize(779, 240);

        if (!AES.t.isAlive()) {
            Main.jTextArea1.append("--------------------------------------------\n");
            switch (AESMode) {
                case 0:
                    jTextArea1.append("encryption of " + fileIter + " files stopped at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n\n");
                    break;
                case 1:
                    jTextArea1.append("decryption of " + fileIter + " files stopped at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n\n");
                    break;
            }
            Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());
        }
        resetStaticInts();
    }

    public static void resetSendTools(int toolMode) {
        switch (toolMode) {
            case 1:
                jPasswordField3.setEnabled(true);
                jList1.setEnabled(true);
                jTextField2.setText("");
                jPasswordField3.setText("");
                jLabel7.setVisible(true);
                jLabel8.setVisible(true);
                jRadioButton3.setVisible(false);
                jRadioButton3.setSelected(false);
                jRadioButton3.setEnabled(true);
                jPasswordField3.requestFocus();
                Folder.listZipFiles();
                break;
            case 2:
                jPasswordField2.setEnabled(true);
                jTextField2.setEnabled(true);
                jTextField2.setText("");
                jPasswordField2.setText("");
                jLabel5.setVisible(true);
                jLabel6.setVisible(true);
                jRadioButton2.setVisible(false);
                jRadioButton2.setSelected(false);
                jSendSQL.setVisible(false);
                jSendSQL.setSelected(false);
                jSendSQL.setEnabled(true);
                jRadioButton2.setEnabled(true);
                jTextField2.requestFocus();
                Folder.filteredSendList.clear();
                break;
        }

    }

    public static void hotFilerFunction(Main main) {
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
            jAlertLabel.setText("");
        }
        AESMode = 0;
        fileIter = 0;
        fileCount = 0;
        if (jToggleButton1.isSelected()) {
            dragDrop.setVisible(false);
            try {
                fileCount = GUI.countFiles(path);
                jProgressBar1.setMaximum(fileCount);
                HotFiler.HotFilerThread(main);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            dragDrop.setVisible(true);
            try {
                watchService.close();
                HotFiler.t.interrupt();
                Main.buttonGroup1.clearSelection();
            } catch (NullPointerException | IOException ex) {
                GUI.labelCutterThread(jAlertLabel, "press hot filer one more time", 0, 25, 500, false);
            }

        }
        jRadioButton1.setVisible(!jToggleButton1.isSelected());
        jRadioButton0.setSelected(jToggleButton1.isSelected());
        jRadioButton0.setEnabled(!jToggleButton1.isSelected());
    }

    public static void fileHiderFunction() {
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        try {
            if (!jToggleButton1.isSelected()) {
                buttonGroup1.clearSelection();
                Main.jToggleButton2.setEnabled(false);
                jTree1.setEnabled(false);
                if (jToggleButton2.isSelected()) {
                    FileHider.FileHiderThread(true, path);
                } else {
                    FileHider.FileHiderThread(false, path);
                }
            } else {
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void initAppGUI(Main main) {
        main.setSize(779, 240);
        jLabel1.setLocation(265, 10);
        jLabel3.setLocation(367, 4);
        jAlertLabel.setLocation(265, 174);
        Main.jLoginPanel.setVisible(false);
        Main.jToolPanel.setVisible(true);
        Main.jProgressBar2.setVisible(true);
        jScrollPane5.setVisible(true);
        main.setLocationRelativeTo(null);
        GUI.t.interrupt();
        GUI.labelCutterThread(jAlertLabel, "welcome to i-ncript, " + username + ".", 20, 40, 1200, false);
    }

    public static void initLogin(Main main) {
        if (!Main.jClientIPInput.getText().equals("000.000.0.000")) {
            if (Client.startSession(username)) {
                try {
                    Client.postTable(username);
                    initAppGUI(main);
                    Folder.appLock();
                } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else {
                Memory.getHeapSize(main);
                Main.jLoginPanel.setVisible(true);
                Main.jToolPanel.setVisible(false);
                Main.jProgressBar2.setVisible(false);
                jAlertLabel.setHorizontalAlignment(LEFT);
                username = "";
            }
        } else {
            if (Client.getClientIP()) {
                if (Client.startSession(username)) {
                    try {
                        Client.postTable(username);
                        initAppGUI(main);
                        Folder.appLock();
                    } catch (IOException | ClassNotFoundException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Memory.getHeapSize(main);
                    Main.jLoginPanel.setVisible(true);
                    Main.jToolPanel.setVisible(false);
                    Main.jProgressBar2.setVisible(false);
                    jAlertLabel.setHorizontalAlignment(LEFT);
                    username = "";
                }
            }
            initAppGUI(main);
            Folder.appLock();
        }
        jTextField1.setText("");
        jPasswordField1.setText("");
        jTextField1.requestFocus();
        main.setTitle("");
        Main.jMenuBar1.setVisible(true);
        Memory.saveIPAddress();
    }
}
