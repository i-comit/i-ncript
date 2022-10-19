/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.AES.*;
import static com.i_comit.windows.Main.jAlertLabel;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class HotFiler {

    public static Thread t;

    public static void HotFilerThread() throws IOException {
        HotFiler_T hotFilerThread = new HotFiler_T();
        t = new Thread(hotFilerThread);
        t.start();
    }

}

class HotFiler_T implements Runnable {

    public int threadIterator;

    public void run() {
        try {
            GUI.labelCutterThread(jAlertLabel, "hot filer enabled", 30, 30, 900);
            List<Path> paths = listNewPaths(Statics.path);
            if (paths.isEmpty()) {
                folderWatcher();
                return;
            } else {
                GUI.progressBarThread();
                AES.AESThread();

                boolean b = true;
                //GUI.t1.interrupt();
                while (b = true) {
                    if (!GUI.t1.isAlive()) {
                        folderWatcher();
                        b = false;
                    }
                    if (!b) {
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
//            ex.getMessage();
        }
    }

    public static List<Path> listNewPaths(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static int countEncFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static int countRegularFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static int paths2 = 0;

    public static void folderWatcher() throws IOException {

        Statics.fileIter = 0;

//        WatchService watchService = FileSystems.getDefault().newWatchService();
        if (Main.jToggleButton1.isSelected()) {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            try {
                Path rootPath = Paths.get(Statics.rootFolder);

                rootPath.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE);
//                        StandardWatchEventKinds.ENTRY_DELETE,
//                        StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey key;
                boolean b = true;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        List<Path> paths = listNewPaths(Statics.path);

                        paths2 = countRegularFiles(Statics.path);
                        while (b) {
                            GUI.labelCutterThread(jAlertLabel, "hot filer detected new files", 30, 30, 900);
                            Thread.sleep(2500);
                            b = false;
                        }
                        paths.forEach(x -> System.out.println(x));
                        System.out.println("Paths int " + paths2);
                        System.out.println(
                                "Event kind:" + event.kind()
                                + ". File affected: " + event.context() + ".");
                        File myObj = new File(Statics.rootFolder + "\\i-ncript-temp-folder-refresher.txt.enc");
                        //This is to refresh the file walker method. This is because watchService runs the file counter before detecting any new files, so I have to make a temporary file to trick it into running the file check again
                        if (myObj.createNewFile()) {
                            System.out.println("File created: " + myObj.getName());
                            Files.setAttribute(myObj.toPath(), "dos:hidden", true);

                        } else {
                            myObj.delete();
                        }
                    }

                    int paths3 = countRegularFiles(Statics.path);
                    System.out.println("Paths int 2: " + paths3);
                    key.reset();
                    //only run AES when the 2 paths being listed have the same count, after the thread sleep.
                    if (paths3 == paths2) {
                        Main.jProgressBar1.setValue(0);
                        key.cancel();
                        AES.AESThread();
                        GUI.progressBarThread();
                        System.out.println("Hot Filer Called AES");
                        folderWatcher();
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        } else {
//            Main.buttonGroup1.clearSelection();
//            GUI.t.interrupt();
            GUI.labelCutterThread(jAlertLabel, "hot filer disabled", 30, 30, 900);
            System.out.println("Watch service disabled");
            HotFiler.t.interrupt();
//            watchService.close();
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewPaths(Statics.path);
        File[] contents = Statics.directory.listFiles();
        if (AES.t.isAlive()) {
            AES.t.interrupt();
        }

        if (Main.jToggleButton1.isSelected()) {
//            AES.AESThread();
            if (contents != null) {
                if (contents.length != 0) {
                    paths.forEach(x -> {
                        try {
                            encrypt(Hasher.modHash(Statics.password), x.toFile(), x.toFile());
                        } catch (AES.CryptoException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    GUI.labelCutterThread(jAlertLabel, "i-ncript folder has no files", 40, 40, 1000);
                }
            } else {
                GUI.labelCutterThread(jAlertLabel, "i-ncript folder does not exist", 40, 40, 100);
            }
        } else {
            AES.t.interrupt();

        }

    }

}
