package com.i_comit.windows.gui;

import com.i_comit.windows.gui.AES.CryptoException;
import static com.i_comit.windows.gui.AES.decrypt;
import static com.i_comit.windows.gui.AES.encrypt;

import static com.i_comit.windows.gui.Globals.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class AES_T implements Runnable {

    public int threadIterator;

    public void run() {

        try {
            AESQuery();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void AESQuery() throws IOException {
        Globals.encKeyString = "Mary has one cat";
        List<Path> paths = listFiles(Globals.path);
        paths.forEach(x -> System.out.println(x));

        switch (Globals.queryMode) {
            case 0:
                paths.forEach(x -> {
                    try {
                        encrypt(Globals.encKeyString, x.toFile(), x.toFile());
                    } catch (CryptoException ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            case 1:
                paths.forEach(x -> {
                    try {
                        decrypt(Globals.encKeyString, x.toFile(), x.toFile());
                    } catch (CryptoException ex) {
                        ex.printStackTrace();
                    }
                });
                break;
            default:
                break;
        }

        //folderWatcher();
    }

    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static List<Path> listNewFiles(Path path) throws IOException {

        List<Path> result;
        try ( Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().endsWith(".enc"))
                    .collect(Collectors.toList());
        }
        return result;
    }

    public static void folderWatcher() throws IOException {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path rootPath = Paths.get(Globals.rootFolder);

            rootPath.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    List<Path> paths = listFiles(Globals.path);
                    paths.forEach(y -> System.out.println(y));
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    getLastModified();

                }
                key.reset();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void getLastModified() throws IOException {
        List<Path> paths = listNewFiles(Globals.path);
        paths.forEach(x -> System.out.println(x));
    }
}
