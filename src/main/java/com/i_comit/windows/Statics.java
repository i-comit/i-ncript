/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.HotFiler_T.watchService;
import static com.i_comit.windows.Main.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Statics {

    public static int toolMode = 0;

    public static String folderName = "i-ncript";
    public static String rootFolder = root.substring(0, 3) + Main.masterFolder + folderName;
    public static String keyName = "\\i-ncript.key";

    public static int maxFileBytes = 1048576 * 4;

    public static int AESMode;
    public static String GB;

    public static boolean hotFilerBool = false;
    public static boolean fileHiderBool = false;
    public static boolean treeViewBool = false;

    public static File directory = new File(root.substring(0, 3) + Main.masterFolder + folderName);
    public static File keyFile = Paths.get(root.substring(0, 3) + Main.masterFolder + keyName).toFile();

    public static File[] contents = null;

    public static Path path = Paths.get(root.substring(0, 3) + Main.masterFolder + folderName);
    public static Path sendFolder = Paths.get(root.substring(0, 3) + Main.masterFolder + "o-box");
    public static Path receiveFolder = Paths.get(root.substring(0, 3) + Main.masterFolder + "n-box");

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

    public static void encryptFunction() {
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        AESMode = 0;
        jProgressBar1.setValue(0);
        jProgressBar1.setMaximum(0);
        try {
            fileCount = GUI.countFiles(path);
            jProgressBar1.setMaximum(fileCount);
            AES.AESThread(listAESPaths(path), directory, true, 0);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void decryptFunction() {
        jToggleButton1.setSelected(false);
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        Statics.AESMode = 1;
        jProgressBar1.setValue(0);
        jProgressBar1.setMaximum(0);
        try {
            fileCount = GUI.countFiles(path);
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

        switch (toolMode) {
            case 1 -> {
                jSwitchMode.setText("N-BOX");
                jSwitchMode.setToolTipText("current panel can decrypt .i-cc files inside n-box (inbox) folder");
                jStorePanel.setVisible(false);
                jSendPanel.setVisible(false);
                jReceivePanel.setVisible(true);
                dragDrop.setVisible(true);

                jLabel5.setVisible(false);
                jLabel6.setVisible(false);
                jRadioButton2.setVisible(false);
                Folder.listZipFiles();

                jLabel10.setText("N-BOX MODE");
                jLabel11.setText("MOVE .I-CC TO N-BOX");
                jLabel10.setToolTipText("drop box will move dropped .i-cc file to n-box folder");
                TreeView.setRootName("n-box");
                TreeView.populateStoreTree(receiveFolder);
            }
            case 2 -> {
                jSwitchMode.setText("O-BOX");
                jSwitchMode.setToolTipText("current panel can encrypt files for sending from o-box (outbox) folder");
                jStorePanel.setVisible(false);
                jSendPanel.setVisible(true);
                jLabel6.setVisible(true);
                jLabel5.setVisible(true);
                jReceivePanel.setVisible(false);
                dragDrop.setVisible(false);

                jLabel7.setVisible(false);
                jLabel8.setVisible(false);
                jRadioButton3.setVisible(false);
                TreeView.setRootName("o-box");
                TreeView.populateStoreTree(sendFolder);
            }
            case 3 -> {
                jSwitchMode.setText("STORE");
                jSwitchMode.setToolTipText("current panel can encrypt & decrypt personal files");
                jStorePanel.setVisible(true);
                jSendPanel.setVisible(false);
                jLabel7.setVisible(true);
                jLabel8.setVisible(true);
                jReceivePanel.setVisible(false);
                dragDrop.setVisible(true);
                toolMode = 0;
                jLabel10.setText("STORE MODE");
                jLabel11.setText("ENCRYPT & DECRYPT");
                jLabel10.setToolTipText("drop box will encrypt & decrypt any files dropped here");
                TreeView.setRootName("i-ncript");
                TreeView.populateStoreTree(path);
            }
        }
    }

    public static void stopFunction() {
        fileIter = 0;
        fileHideIter = 0;
        FileHider.fileCt = 0;

        AES.t.interrupt();
        AES.t.stop();
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
        jButton2.setVisible(false);
        switch (AESMode) {
            case 0 ->
                jTextArea1.append("encryption of " + fileCount + " files stopped\n");
            case 1 ->
                jTextArea1.append("decryption of " + fileCount + " files stopped\n");
        }
        fileCount = 0;

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
        FileHider.cleanUp();
        jAlertLabel.setText("");
        dragDrop.setVisible(true);
        toolBtnsBool(true);
    }

    public static void hotFilerFunction() {
        AESMode = 0;
        if (jToggleButton1.isSelected()) {
            dragDrop.setVisible(false);
            try {
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        jRadioButton1.setVisible(!jToggleButton1.isSelected());
        jRadioButton0.setSelected(jToggleButton1.isSelected());
        jRadioButton0.setEnabled(!jToggleButton1.isSelected());
        if (GUI.t.isAlive()) {
            GUI.t.interrupt();
        }
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
}
