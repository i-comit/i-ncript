/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.macos;

import static com.i_comit.macos.GUI.listAESPaths;
import static com.i_comit.macos.Main.jAlertLabel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.*;

/**
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

    @Override
    public void run() {
        synchronized (this) {
            try {
                if (Main.jToggleButton1.isSelected()) {
                    List<Path> path = listAESPaths(Statics.path);
                    if (path.isEmpty()) {
                        System.out.println("HotFilerPathEmpty");
                        folderWatcher();

                    } else {
                        AES.AESThread(path, Statics.directory, true, 0);
                    }

                } else {
                    HotFiler.t.stop();

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static int countRegFiles(Path path) throws IOException {
        int result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = Math.toIntExact(walk.filter(Files::isRegularFile).filter(p -> !p.getFileName().toString().endsWith(".enc")).count());
        }
        return result;
    }

    public static WatchService watchService;

    public static void folderWatcher() throws IOException {
        System.out.println("Folder Watcher Enabled");
        try {
            watchService = FileSystems.getDefault().newWatchService();
            List<Path> directories = GUI.listDirs(Paths.get(Statics.rootFolder));
            for (int i = 0; i < directories.size(); i++) {
                directories.get(i).toFile().toPath().register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE);
            }
            System.out.println("folder watcher attached at " + directories.size() + " folders");
            WatchKey key;
            while ((key = watchService.take()) != null && Main.jToggleButton1.isSelected()) {
                GUI.labelCutterThread(jAlertLabel, "hot filer detected new files", 15, 25, 550, false);
                Statics.fileCount = 0;

                for (WatchEvent<?> event : key.pollEvents()) {
                    Main.jToggleButton1.setEnabled(false);
                    boolean b = true;
                    while (b) {
                        int paths0 = countRegFiles(Statics.path);
                        Thread.sleep(1200);
                        Statics.fileCount = countRegFiles(Statics.path);
                        if (Statics.fileCount == paths0) {
                            if (paths0 != 0 && Statics.fileCount != 0) {
                                GUI.getGB();
                                key.cancel();
                                watchService.close();
                                Statics.fileIter = 0;
                                Statics.fileCount = GUI.countFiles(Statics.path);
                                Main.jProgressBar1.setMaximum(Statics.fileCount);
                                AES.AESThread(listAESPaths(Statics.path), Statics.directory, true, 0);
                                System.out.println("Hot Filer Called AES");
                                b = false;
                            }
                        }
                    }
//                    System.out.println(Statics.fileIter++);
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                }
//                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ClosedWatchServiceException ex) {
            System.out.println("Watch Service Closed");
        }
    }
}
