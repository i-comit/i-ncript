/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class TreeView {

    public static DefaultTreeModel populateStoreTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("i-ncript"); // root node
        root.removeAllChildren();
        try {
            List<Path> directories = GUI.listDirs(Paths.get(Statics.rootFolder));
            for (int i = 1; i < directories.size(); i++) {
                DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(directories.get(i).toFile().getName()); // level 1 node

//                recursiveDir(directories.get(i).toFile());
                if (recursiveDir(directories.get(i).toFile()).length != 0) {
                    System.out.println("PARENT NODE OF RECURSIVE IS " + directories.get(i).toFile().getName());
                    String[] arr = recursiveDir(directories.get(i).toFile());
                    System.out.println("SIZE OF RECURSIVE IS " + arr.length);
                    for (int a = 0; a < arr.length; a++) {
                        String subDirPath = Main.root + Main.masterFolder + Statics.folderName + File.separator + directories.get(i).toFile().getName() + File.separator + arr[a];
                        String subDirPath2 = arr[a];
                        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(subDirPath2); // level 1 node

                        System.out.println("SUBDIR NAME IS " + subDirPath2);
                        File[] files = new File(subDirPath).listFiles();
                        System.out.println(Arrays.toString(files));
                        if (files != null) {
                            dirNodes.add(subDirNodes);

                            for (int b = 0; b < files.length; b++) {
                                if (!files[b].isDirectory()) {
                                    System.out.println("FILES " + files[b]);
                                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files[b].getName()); // level 2 (leaf) node
                                    subDirNodes.add(fileNodes);
                                } else {
                                    DefaultMutableTreeNode subDirNodes1 = new DefaultMutableTreeNode(files[b].getName()); // level 1 node
                                    subDirNodes.add(subDirNodes1);
                                    File[] files2 = files[b].listFiles();
                                    System.out.println("???" + Arrays.toString(files2));
                                    for (int c = 0; c < files2.length; c++) {
                                        if (!files2[c].isDirectory()) {
                                            System.out.println("FILES2 " + files2[c]);

                                            DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files2[c].getName()); // level 2 (leaf) node
                                            subDirNodes1.add(fileNodes);
                                        }
                                    }
                                }

                            }
                        }
                    }
                } else {

                }
                if (i == 1) {
                    root.add(dirNodes);
                    System.out.println(directories.get(i));
                    File[] files = directories.get(i).toFile().listFiles();
                    for (int x = 0; x < files.length; x++) {
                        DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(files[x].getName()); // level 2 (leaf) node
                        dirNodes.add(fileNodes);
                    }
                }

                System.out.println("folder watcher attached at " + directories.get(i).toFile().getName());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }

    public static String[] recursiveDir(File file) {
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        return directories;
    }

    public static File treeFileFormatter(TreePath[] treePaths) {
        File fileFormat = null;
        String path = root + masterFolder + treePaths[0].toString().substring(1, treePaths[0].toString().length() - 1).replaceAll(", ", "\\\\");
        String fileName = new File(root + masterFolder + treePaths[0].toString().substring(1, treePaths[0].toString().length() - 1).replaceAll(", ", "\\\\")).getName();
//                .replaceAll(new File(treePaths[0].toString()).getName(), "");
        System.out.println("fileName is " + fileName);
        System.out.println("path is " + path);
        if (!new File(path).isDirectory()) {
            System.out.println("Final path without filename " + path.replaceAll(fileName, ""));

        }
        for (int i = 0; i < treePaths.length; i++) {
            fileFormat = new File(root + masterFolder + treePaths[i].toString().substring(1, treePaths[i].toString().length() - 1).replaceAll(", ", "\\\\"));
            if (!fileFormat.isDirectory()) {
                System.out.println("files are " + fileFormat);
            }

        }
        return fileFormat;
    }
}
