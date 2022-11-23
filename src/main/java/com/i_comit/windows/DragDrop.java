/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
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

    static int encFiles = 0;
    static int decFiles = 0;

    public static File filesf;

    @Override
    public void drop(DropTargetDropEvent event) {
        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);
        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();
        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        // Loop through the flavors
        if (!jTree1.isSelectionEmpty()) {
//            jTree1.clearSelection();
            if (Statics.toolMode == 0 || Statics.toolMode == 3) {
                List<Path> treepaths = new ArrayList<>();
                String path = root + masterFolder + Main.jTree1.getSelectionPaths()[0].toString().substring(1, Main.jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\");
                String fileName = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[0].toString().substring(1, Main.jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\")).getName();

                for (int i = 0; i < Main.jTree1.getSelectionPaths().length; i++) {
                    File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[i].toString().substring(1, Main.jTree1.getSelectionPaths()[i].toString().length() - 1).replaceAll(", ", "\\\\"));
                    if (!fileFormat.isDirectory()) {
                        System.out.println("files from Drag Drop are " + fileFormat);
                        treepaths.add(fileFormat.toPath());
                    }
                }
                if (TreeView.checkFilesAreFromSameFolder(treepaths)) {
                    Main.jButton2.setVisible(true);
                    Main.jProgressBar1.setMaximum(treepaths.size());
                    System.out.println("Path from Drag Drop is " + path.replaceAll(fileName, ""));
                    AES.AESThread(treepaths, new File(path.replaceAll(fileName, "")), false, 0);

                } else {
                    GUI.t.interrupt();
                    GUI.labelCutterThread(jAlertLabel, "all files must be in same folder", 10, 20, 800, false);
                }
                jTree1.clearSelection();
            }
        }

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
                                if (i >= paths.size() - 1) {
                                    if (!filesf.isDirectory()) {
                                        Main.jButton2.setVisible(true);
                                        jProgressBar1.setString("0% | 0/" + files.size());
                                        Main.jProgressBar1.setMaximum(0);
                                        AES.AESThread(paths, Statics.directory, false, 0);
                                    } else {
                                        Folder.getFileDropCount(filesf);
                                        recursiveFileDrop_T.recursiveFileStoreDrop(filesf, Statics.path, paths);
                                        paths.remove(0);
                                        Main.jButton2.setVisible(true);
                                        jProgressBar1.setString("0% | 0/" + files.size());
                                        Main.jProgressBar1.setMaximum(0);
                                        AES.AESThread(paths, Statics.directory, false, 0);
                                    }
                                }
                            }
                            if (Statics.toolMode == 1) {
                                if (files.size() <= 1) {
                                    if (filesf.toString().endsWith(".i-cc")) {
                                        Files.move(filesf.toPath(), Paths.get(Statics.receiveFolder + File.separator + filesf.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Main.jTextArea1.append(filesf.getName() + " has been moved to the n-box folder\n");
                                        Folder.listZipFiles();
                                    } else {
                                        GUI.labelCutterThread(Main.jAlertLabel, "only .i-cc files are allowed", 10, 25, 750, false);
                                    }
                                } else {
                                    GUI.labelCutterThread(Main.jAlertLabel, "only 1 file is allowed at once", 10, 25, 750, false);
                                }
                            }
                            if (Statics.toolMode == 2) {
                                if (filesf.isDirectory()) {
                                    Folder.getFileDropCount(filesf);
                                    Folder.recursiveFileDropSendThread(filesf, Statics.sendFolder);
                                } else {
                                    Files.move(filesf.toPath(), Paths.get(Statics.sendFolder + File.separator + filesf.getName()), StandardCopyOption.REPLACE_EXISTING);
                                }
                            }
                            GUI.t.interrupt();
                        }
                    } else {
                        if (GUI.t.isAlive()) {
                            GUI.t.interrupt();
                        }
                        GUI.labelCutterThread(Main.jAlertLabel, "only 10 files are allowed at once", 10, 25, 750, false);
                        Main.jTextArea1.append("For security reasons, you can only drop up to 10 files at once\n");
                    }
                }

            } catch (UnsupportedFlavorException | IOException e) {
                e.printStackTrace();
            }
        }
        event.dropComplete(true);
    }

    @Override
    public void dragEnter(DropTargetDragEvent event
    ) {

    }

    @Override
    public void dragExit(DropTargetEvent event
    ) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event
    ) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event
    ) {
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
            AudioPlayer.audioPlayerThread("aes-sfx.wav");
            GUI.labelCutterThread(jAlertLabel, decFiles + " encrypted | " + encFiles + " decrypted", 15, 30, 300, false);

            Thread.sleep(300);
            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(5);
                jProgressBar1.setValue(x);
                if (x <= 1) {
                    Main.progressbarBool = true;
                }
            }
            if (jProgressBar1.getValue() == 0) {
                Statics.fileIter = 0;
                Statics.fileCount = 0;
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setStringPainted(false);
                jProgressBar1.setVisible(false);

                Main.jProgressBar2.setStringPainted(false);
                Main.jProgressBar2.setVisible(true);

                DragDrop.encFiles = 0;
                DragDrop.decFiles = 0;
                Main.dragDrop.setVisible(true);
                Main.toolBtnsBool(true);
                Main.jTabbedPane1.setSelectedIndex(0);
                TreeView.populateStoreTree(Statics.path);
                AES_T.paths = null;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
