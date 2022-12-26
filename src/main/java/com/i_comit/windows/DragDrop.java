/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import com.i_comit.shared.Audio;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import static com.i_comit.windows.Statics.toolMode;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.TreePath;

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
        boolean b = false;
        Statics.resetStaticInts();
        event.acceptDrop(DnDConstants.ACTION_COPY);
        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();
        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        // Loop through the flavors
        if (jTree1.getSelectionPaths() != null) {
            if (Statics.toolMode == 0 || Statics.toolMode == 3) {
                List<Path> treepaths = new ArrayList<>();
                String path = root + masterFolder + jTree1.getSelectionPaths()[0].toString().substring(1, jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\");
                String fileName = new File(root + masterFolder + jTree1.getSelectionPaths()[0].toString().substring(1, jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\")).getName();
                for (TreePath selectionPath : jTree1.getSelectionPaths()) {
                    File fileFormat = new File(root + masterFolder + selectionPath.toString().substring(1, selectionPath.toString().length() - 1).replaceAll(", ", "\\\\"));
                    if (!fileFormat.isDirectory()) {
                        treepaths.add(fileFormat.toPath());
                        if (TreeView.checkFilesAreFromSameFolder(treepaths)) {
                            if (!b) {
                                Main.jButton2.setVisible(true);
                                Main.jProgressBar1.setMaximum(treepaths.size());
                                jProgressBar1.setString("0% | 0/" + treepaths.size());
                                AES.AESThread(treepaths, new File(path.replaceAll(fileName, "")), false, 0);
                                b = true;
                            }
                        } else {
                            GUI.t.interrupt();
                            GUI.labelCutterThread(jAlertLabel, "all files must be in same folder", 10, 20, 800, false);
                        }
                    } else {
                        try {
                            List<Path> dropDir = GUI.listPaths(fileFormat.toPath());
                            dropDir.forEach(x -> {
                                treepaths.add(x);
                            });
                            AES.AESThread(treepaths, new File(path.replaceAll(fileName, "")), false, 0);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            jTree1.clearSelection();
        } else {
            for (DataFlavor flavor : flavors) {
                try {
                    // If the drop items are files
                    if (flavor.isFlavorJavaFileListType()) {
                        // Get all of the dropped files
                        List files = (List) transferable.getTransferData(flavor);
                        List<Path> paths = new ArrayList<>();
                        // Loop them through
                        for (int i = 0; i < files.size(); i++) {
                            String sf = files.get(i).toString();
                            filesf = new File(sf);
                            paths.add(filesf.toPath());
                            if (Statics.toolMode == 0 || Statics.toolMode == 3) {
                                if (i >= paths.size() - 1) {
                                    if (!b) {
                                        Statics.dragDropBool = true;
                                        if (!filesf.isDirectory()) {
                                            if (!filesf.getName().endsWith(".i-cc")) {
                                                if (!filesf.getAbsolutePath().equals(root + masterFolder + "i-ncript.exe")
                                                        && !filesf.getAbsolutePath().equals(root + masterFolder + "server.exe")
                                                        && !filesf.getAbsolutePath().equals(root + masterFolder + Statics.keyName)) {
                                                    Main.jButton2.setVisible(true);
                                                    Main.jProgressBar1.setMaximum(0);
                                                    jProgressBar1.setString("0% | 0/" + files.size());
                                                    AES.AESThread(paths, Statics.directory, false, 0);
                                                } else {
                                                    GUI.t.interrupt();
                                                    GUI.labelCutterThread(Main.jAlertLabel, "can't encrypt app files", 10, 25, 1000, false);
                                                }
                                            } else {
                                                GUI.t.interrupt();
                                                GUI.labelCutterThread(Main.jAlertLabel, ".i-cc files are not allowed", 10, 25, 1000, false);
                                            }
                                        } else {
                                            if (!filesf.getAbsolutePath().equals(root + masterFolder + "ᴠᴀᴜʟᴛ")
                                                    && !filesf.getAbsolutePath().equals(root + masterFolder + Statics.nBoxName)
                                                    && !filesf.getAbsolutePath().equals(root + masterFolder + Statics.oBoxName)
                                                    && !filesf.getAbsolutePath().equals(root + masterFolder + "app")
                                                    && !filesf.getAbsolutePath().equals(root + masterFolder + "runtime")) {
                                                if (Main.toggleDragDropBool) {
                                                    Folder.getFileDropCount(filesf);
                                                    Folder.recursiveFileDropThread(filesf, Statics.path);
                                                    filesf.delete();
                                                } else {
                                                    recursiveFileDrop_T.recursiveFileStoreDrop(filesf, Statics.path, paths);
                                                    paths.remove(0);
                                                    Main.jButton2.setVisible(true);
                                                    Main.jProgressBar1.setMaximum(0);
                                                    jProgressBar1.setString("0% | 0/" + files.size());
                                                    AES.AESThread(paths, Statics.directory, false, 0);
                                                }
                                            } else {
                                                GUI.t.interrupt();
                                                GUI.labelCutterThread(Main.jAlertLabel, "can't encrypt app folders", 10, 25, 1000, false);
                                            }
                                        }
                                        b = true;
                                    }
                                }
                            }
                            if (Statics.toolMode == 1) {
                                if (files.size() <= 10) {
                                    if (filesf.toString().endsWith(".i-cc")) {
                                        Files.move(filesf.toPath(), Paths.get(Statics.receiveFolder + File.separator + filesf.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Main.jTextArea1.append(filesf.getName() + " has been moved to the n-box folder\n");
                                        Folder.listZipFiles();
                                    } else {
                                        GUI.t.interrupt();
                                        GUI.labelCutterThread(Main.jAlertLabel, "only .i-cc files are allowed", 10, 25, 1000, false);
                                    }
                                } else {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(Main.jAlertLabel, "only 10 file is allowed at once", 10, 25, 1000, false);
                                }
                            }
                            if (Statics.toolMode == 2) {
                                if (!filesf.getParent().equals(Statics.sendFolder.toString())) {
                                    if (filesf.isDirectory()) {
                                        Folder.getFileDropCount(filesf);
                                        Folder.recursiveFileDropThread(filesf, Statics.sendFolder);
                                        filesf.delete();
                                    } else {
                                        Files.move(filesf.toPath(), Paths.get(Statics.sendFolder + File.separator + filesf.getName()), StandardCopyOption.REPLACE_EXISTING);
                                        Main.refreshTreeView(Statics.sendFolder, TreeView.sendCaretPos);
                                    }
                                } else {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(Main.jAlertLabel, "files/folders already in o-box", 10, 25, 750, false);
                                }
                            }
                        }

                    }

                } catch (UnsupportedFlavorException | IOException e) {
                    e.printStackTrace();
                }
            }
            event.dropComplete(true);
        }
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

    public static synchronized void resetProgressBar(int encFiles, int decFiles) {
        Main.jProgressBar1.setString("100% | " + AES_T.paths.size() + "/" + AES_T.paths.size());
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        try {
            Thread.sleep(400);
            Audio.audioPlayerThread("ding-sfx.wav");
            GUI.labelCutterThread(jAlertLabel, decFiles + " encrypted | " + encFiles + " decrypted", 15, 30, 600, false);
            Main.jTextArea1.append("--------------------------------------------\n");
            Main.jTextArea1.append(decFiles + " encrypted | " + encFiles + " decrypted | " + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("hh:ss a")) + "\n\n");
            Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());

            Thread.sleep(250);

            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(4);
                jProgressBar1.setValue(x);
                if (x <= 1) {
                    if (toolMode == 0 || toolMode == 3) {
                        Statics.dragDropBool = false;
                    }
                    Main.progressbarBool = true;
                }
            }
            if (jProgressBar1.getValue() == 0) {
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setVisible(false);

                Main.jProgressBar2.setStringPainted(false);
                Main.jProgressBar2.setVisible(true);

                DragDrop.encFiles = 0;
                DragDrop.decFiles = 0;
                Main.dragDrop.setVisible(true);
                Main.toolBtnsBool(true);
                Main.jButton2.setVisible(false);
                if (!Main.mouseOverLog) {
                    Main.jTabbedPane1.setSelectedIndex(0);
                }
                AES_T.paths.clear();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
