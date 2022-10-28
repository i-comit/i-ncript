/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.password;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
class DragDrop implements DropTargetListener {

    public static Thread t;

    public static void progressBarThread(int encFiles, int decFiles) {
        t = new Thread(() -> DragDrop_T.resetProgressBar(encFiles, decFiles));
        t.start();
    }

    @Override
    public void drop(DropTargetDropEvent event) {
        Main.toolBtnsBool(false);
        Main.jTextArea5.setVisible(false);
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
                            File filesf = new File(sf);
                            paths.add(filesf.toPath());
                            if (i >= files.size() - 1) {
                                listDragDropFiles(paths);
                            }
                        }
                    } else {
                        GUI.labelCutterThread(Main.jAlertLabel, "you can only drop up to 10 files at once", 10, 25, 500);
                        Main.jTextArea1.append("For security reasons, you can only drop up to 10 files at once\n");
                    }
                }

            } catch (Exception e) {
                // Print out the error stack
                e.printStackTrace();
            }
        }
        // Inform that the drop is complete
        event.dropComplete(true);
    }
    static int encFiles = 0;
    static int decFiles = 0;

    public static void listDragDropFiles(List paths) throws IOException {
        AES_T.paths = paths;
        Statics.fileIter = 0;
        jProgressBar1.setMaximum(paths.size());
        jProgressBar1.setStringPainted(true);
        jProgressBar1.setValue(Statics.fileIter);

        paths.forEach(x -> {
            try {
                String fileStr = x.toString();
                File file = Paths.get(fileStr).toFile();
                if (x.toString().endsWith(".enc") || x.toString().startsWith("Thumbs.db")) {
                    encFiles++;
                    AES.decrypt(Hasher.modHash(password), file, file);
                }
                if (!x.toString().endsWith(".enc")) {
                    decFiles++;
                    AES.encrypt(Hasher.modHash(password), file, file);
                }
            } catch (AES.CryptoException ex) {
                ex.printStackTrace();
            }
        });

        if (Statics.fileIter == 0) {
            GUI.t.interrupt();
            GUI.labelCutterThread(Main.jAlertLabel, "incorrect key", 10, 25, 500);
        } else {
            progressBarThread(encFiles, decFiles);
            GUI.getGB();
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
                Main.jTextArea5.setVisible(true);
                Main.toolBtnsBool(true);
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
