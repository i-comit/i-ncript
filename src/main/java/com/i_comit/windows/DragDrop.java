/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.decrypt;
import static com.i_comit.windows.AES_T.listAESPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import static com.i_comit.windows.Statics.password;
import static com.i_comit.windows.Statics.path;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
class DragDrop implements DropTargetListener {

    public static Thread t;

    public static void progressBarThread() {
        t = new Thread(() -> progressBar_T2.resetProgressBar());
        t.start();

    }

    @Override
    public void drop(DropTargetDropEvent event) {
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
                    // Loop them through
                    for (int i = 0; i < files.size(); i++) {
                        String sf = files.get(i).toString();
                        File filesf = new File(sf);
                        paths.add(filesf.toPath());
                        System.out.println("File path is " + filesf);
                        if (i == files.size() - 1) {
                            listDragDropFiles(paths);
                        }
                    }
                    for (Object file : files) {
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

    public static void listDragDropFiles(List paths) throws IOException {
        Statics.fileIter = 0;
        jProgressBar1.setMaximum(paths.size());
        System.out.println(paths.size());
        jProgressBar1.setStringPainted(true);
        jProgressBar1.setValue(Statics.fileIter);
        paths.forEach(x -> {
            try {
                String fileStr = x.toString();
                File file = Paths.get(fileStr).toFile();
                if (x.toString().endsWith(".enc") || x.toString().startsWith("Thumbs.db")) {
//                    System.out.println("Encrypted");
                    AES.decrypt(Hasher.modHash(password), file, file);
                }
                if (!x.toString().endsWith(".enc")) {
                    System.out.println("Not encrypted");
                    try {
                        AES.encrypt(Hasher.modHash(password), file, file);
                    } catch (AES.CryptoException ex) {
                    }
                }
            } catch (AES.CryptoException ex) {
                ex.printStackTrace();
            }
        });
        if (Statics.fileIter == 0) {
            GUI.t.interrupt();
            GUI.labelCutterThread(Main.jAlertLabel, "incorrect key", 10, 25, 500);
        } else {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            progressBarThread();
            System.out.println("amogus");
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
        Main.toolBtnsBool(false);

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

class progressBar_T2 implements Runnable {

    public int threadIterator;

    public void run() {

    }

    public static void resetProgressBar() {
        jProgressBar1.setMaximum(100);
        jProgressBar1.setValue(jProgressBar1.getMaximum());
        Main.jButton2.setVisible(false);
        try {
            Thread.sleep(500);

            for (int x = jProgressBar1.getMaximum(); x >= 0; x--) {
                Thread.sleep(5);
                jProgressBar1.setValue(x);
            }
            if (jProgressBar1.getValue() == 0) {
                Statics.fileIter = 0;
                Statics.fileCount = 0;
                jProgressBar1.setValue(Statics.fileIter);
                jProgressBar1.setStringPainted(false);
                FileHider.FileHiderThread(Main.jToggleButton2.isSelected());
            }
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
