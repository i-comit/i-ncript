/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
class DragDrop implements DropTargetListener {

    public static Thread t;
    public static Thread t2;
    static int encFiles = 0;
    static int decFiles = 0;

    public static File filesf;

    public static void progressBarThread(int encFiles, int decFiles) {
        t = new Thread(() -> DragDrop_T.resetProgressBar(encFiles, decFiles));
        t.start();
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        Main.toolBtnsBool(false);
        Main.dragDrop.setVisible(false);
        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);
        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();
        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        for (DataFlavor flavor : flavors) {
            try {
                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {
                    // Get all of the dropped files
                    List files = (List) transferable.getTransferData(flavor);
                    List<Path> paths = new ArrayList<>();

                    if (files.size() <= 10) {
                        // Loop them through
                        for (int i = 0; i < files.size(); i++) {
                            String sf = files.get(i).toString();
                            filesf = new File(sf);
                            paths.add(filesf.toPath());
                            if (Statics.toolMode == 0) {
                                if (i >= files.size() - 1) {
                                    Main.jProgressBar1.setVisible(true);
                                    AES.AESThread(paths, Statics.directory, false, 0);
                                }
                            }
                            if (Statics.toolMode == 1) {
                                if (files.size() <= 1) {
                                    if (filesf.toString().endsWith(".i-cc")) {
                                        Files.move(filesf.toPath(), Paths.get(Statics.receiveFolder + "\\" + filesf.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Main.jTextArea1.append(filesf.getName() + " has been moved to the n-box folder\n");
                                        Folder.listZipFiles();
                                        Main.toolBtnsBool(true);
                                        Main.dragDrop.setVisible(true);
                                    } else {
                                        System.out.println("only .i-cc files allowed");
                                        Main.toolBtnsBool(true);
                                        Main.dragDrop.setVisible(true);
                                    }
                                } else {
                                    GUI.labelCutterThread(Main.jAlertLabel, "only 1 file is allowed at once", 10, 25, 500);
                                    Main.toolBtnsBool(true);
                                    Main.dragDrop.setVisible(true);
                                }
                            }
                        }
                    } else {
                        if (GUI.t.isAlive()) {
                            GUI.t.interrupt();
                        }
                        GUI.labelCutterThread(Main.jAlertLabel, "only 10 files are allowed at once", 10, 25, 500);
                        Main.jTextArea1.append("For security reasons, you can only drop up to 10 files at once\n");
                        Main.toolBtnsBool(true);
                        Main.dragDrop.setVisible(true);
                    }
                }

            } catch (Exception e) {
                // Print out the error stack
                e.printStackTrace();
            }
        }
        event.dropComplete(true);
    }

    public static void resetProgressBar(int encFiles, int decFiles) {
        Main.jProgressBar1.setString("100% | " + AES_T.paths.size() + "/" + AES_T.paths.size());
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        Main.jButton2.setVisible(false);

        try {
            Thread.sleep(400);
            GUI.labelCutterThread(jAlertLabel, decFiles + " encrypted | " + encFiles + " decrypted", 15, 30, 300);

            Thread.sleep(300);
            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(5);
                jProgressBar1.setValue(x);
            }
            if (jProgressBar1.getValue() == 0) {
                Statics.fileIter = 0;
                Statics.fileCount = 0;
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setStringPainted(false);
                DragDrop.encFiles = 0;
                DragDrop.decFiles = 0;
                Main.dragDrop.setVisible(true);
                Main.jProgressBar1.setVisible(false);
                Main.jTabbedPane1.setSelectedIndex(0);
                Main.toolBtnsBool(true);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {

    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }

}

class DragDrop_T implements Runnable {

    public int threadIterator;

    public void run() {

    }

    public static void resetProgressBar(int encFiles, int decFiles) {
        Main.jProgressBar1.setString("100% | " + AES_T.paths.size() + "/" + AES_T.paths.size());
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        Main.jButton2.setVisible(false);

        try {
            Thread.sleep(400);
            GUI.labelCutterThread(jAlertLabel, decFiles + " encrypted | " + encFiles + " decrypted", 15, 30, 300);

            Thread.sleep(300);
            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(5);
                jProgressBar1.setValue(x);
            }
            if (jProgressBar1.getValue() == 0) {
                Statics.fileIter = 0;
                Statics.fileCount = 0;
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setStringPainted(false);
                DragDrop.encFiles = 0;
                DragDrop.decFiles = 0;
                Main.dragDrop.setVisible(true);
                Main.toolBtnsBool(true);
                AES_T.paths = null;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
