/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.GUI.listAESPaths;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jProgressBar1;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
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
                    List<Path> path = listNewPaths(Statics.path);

                    if (path.isEmpty()) {
                        System.out.println("HotFilerPathEmpty");

                    } else {
                        Statics.fileIter = 0;
                        Statics.fileCount = GUI.countFiles(Statics.path);
                        jProgressBar1.setMaximum(Statics.fileCount);
                        AES.AESThread(listAESPaths(Statics.path), Statics.directory, true, 0);
                    }
                    Main.jToggleButton1.setBackground(new Color(28, 68, 94));
                    jAlertLabel.setText("hot filer enabled");
                    Thread.sleep(500);
                    jAlertLabel.setText("");
                    folderWatcher();
//

                } else {
                    HotFiler.t.stop();

                }
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
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
                System.out.println("folder watcher attached at " + directories.get(i).toFile().getName());
            }
            WatchKey key;
            while ((key = watchService.take()) != null && Main.jToggleButton1.isSelected()) {
                GUI.labelCutterThread(jAlertLabel, "hot filer detected new files", 15, 25, 550);
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
                                key.cancel();
                                Statics.fileIter = 0;
                                Statics.fileCount = GUI.countFiles(Statics.path);
                                Main.jProgressBar1.setMaximum(Statics.fileCount);
                                AES.AESThread(listAESPaths(Statics.path), Statics.directory, true, 0);
                                System.out.println("Hot Filer Called AES");
//                                watchService.close();
                                GUI.getGB();
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
