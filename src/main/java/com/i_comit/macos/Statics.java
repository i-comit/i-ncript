/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.macos;

import static com.i_comit.macos.GUI.listAESPaths;
import static com.i_comit.macos.HotFiler_T.watchService;
import static com.i_comit.macos.Main.*;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static int toolMode = 0;

    public static final String folderName = "i-ncript";
    public static String rootFolder = root + Main.masterFolder + folderName;
    public static final String keyName = ".i-ncript.key";

    public static int maxFileBytes = 1048576 * 4;

    public static int AESMode;
    public static String GB;

    public static boolean hotFilerBool = false;
    public static boolean fileHiderBool = false;

    public static File directory = null;
    public static File keyFile = null;

    public static File[] contents = null;

    public static Path path = null;
    public static Path sendFolder = null;
    public static Path receiveFolder = null;

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
            //create the font to use. Specify the size!
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile.openStream()).deriveFont(fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //register the font
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

        try {
            jProgressBar1.setString("0% | 0/" + fileCount);
            jProgressBar1.setMaximum(fileCount);
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

        try {
            jProgressBar1.setString("0% | 0/" + fileCount);
            jProgressBar1.setMaximum(fileCount);
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
            case 1:
                jSwitchMode.setText("N-BOX");
                jSwitchMode.setToolTipText("current panel can decrypt .i-cc files inside n-box (inbox) folder");
                jStorePanel.setVisible(false);
                jSendPanel.setVisible(false);
                jReceivePanel.setVisible(true);
                jTree1.setDragEnabled(true);

                jLabel5.setVisible(false);
                jLabel6.setVisible(false);
                jRadioButton2.setVisible(false);
                Folder.listZipFiles();

                jLabel10.setText("N-BOX MODE");
                jLabel11.setText("MOVE .I-CC TO N-BOX");
                dragDrop.setToolTipText("drop box will move dropped .i-cc file to n-box folder");
                FileHider.cleanUp(receiveFolder);
                TreeView.setRootName("n-box");
                TreeView.populateStoreTree(receiveFolder);
                TreeView.expandTreeNode(receiveFolder);
                break;
            case 2:
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
                jLabel10.setText("O-BOX MODE");
                jLabel11.setText("MOVE FILES TO O-BOX");
                dragDrop.setToolTipText("drop box will move 10 files or 1 folder into o-box folder");
                FileHider.cleanUp(sendFolder);
                TreeView.setRootName("o-box");
                TreeView.populateStoreTree(sendFolder);
                TreeView.expandTreeNode(sendFolder);
                break;
            case 3:
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
                TreeView.setRootName("i-ncript");
                TreeView.populateStoreTree(path);
                TreeView.expandTreeNode(path);
                toolMode = 0;
                break;
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
        switch (AESMode) {
            case 0:
                jTextArea1.append("encryption of " + fileCount + " files stopped at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                break;
            case 1:
                jTextArea1.append("decryption of " + fileCount + " files stopped at " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n");
                break;
        }
        resetStaticInts();

        jButton2.setVisible(false);
        jTabbedPane1.setSelectedIndex(0);
        jProgressBar1.setValue(fileIter);
        jProgressBar1.setMaximum(fileCount);
        jProgressBar1.setStringPainted(false);
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
                FileHider.cleanUp(receiveFolder);
                refreshTreeView(receiveFolder, TreeView.receiveCaretPos);
                break;
            case 2:
                FileHider.cleanUp(sendFolder);
                refreshTreeView(sendFolder, TreeView.sendCaretPos);
                break;
            case 3:
                FileHider.cleanUp(path);
                refreshTreeView(path, TreeView.nodeCaretPos);
                break;
        }
        main.setSize(756, 224);
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
                HotFiler.HotFilerThread();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            dragDrop.setVisible(true);
            try {
                watchService.close();
                HotFiler.t.interrupt();
                Main.buttonGroup1.clearSelection();
                Main.jProgressBar1.setStringPainted(false);
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

    public static void collapseLogin(Main main) {
        main.setSize(756, 224);
        jLabel1.setLocation(259, 8);
        jLabel3.setLocation(361, 4);
        jAlertLabel.setLocation(259, 174);
        main.setLocationRelativeTo(null);
        jScrollPane5.setVisible(true);
    }

}
