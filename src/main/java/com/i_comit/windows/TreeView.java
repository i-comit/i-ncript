/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows;

import static com.i_comit.windows.Main.jTree1;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class TreeView {

    public static DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("i-ncript"); // root node
    public static DefaultTreeModel model = new DefaultTreeModel(treeRoot);

    public static DefaultTreeModel populateStoreTree(Path path) {
        treeRoot.removeAllChildren();
        try {
            List<Path> directories = GUI.listDirs(path);
            listRoot(directories);
        } catch (IOException ex) {
        }

        model.reload(treeRoot);
        jTree1.revalidate();
        testTree2();
        asdsa();
        System.out.println("DIR NODE LIST " + dirNodeList);
        return model;
    }

    private static List<DefaultMutableTreeNode> dirNodeList = new ArrayList<>();
    private static List<DefaultMutableTreeNode> dirNodeList2 = new ArrayList<>();

    private static Map<Integer, DefaultMutableTreeNode> map = new HashMap<Integer, DefaultMutableTreeNode>();

    private static int keyIter = 0;

    public static void setRootName(String rootName) {
        treeRoot.setUserObject(rootName);
    }

    private static void listRoot(List<Path> directories) {
        dirNodeList.clear();
        File[] filesArr = directories.get(0).toFile().listFiles();
        for (File filesArr1 : filesArr) {
            DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
                    treeRoot.add(fileNodes);
                }
            } else {
//                arrayList.add(new AbstractMap.SimpleEntry(keyIter, dirNodes));

                dirNodeList.add(dirNodes);
                map.put(keyIter, dirNodes);
                ++keyIter;
                treeRoot.add(dirNodes);
                listFiles(filesArr1, dirNodes);
            }
        }
    }

    public static void testTree2() {

//                System.out.println("PATH GET PATH " + treePath.getParentPath());
        System.out.println(jTree1.getSelectionPath());
        System.out.println(jTree1.getRowForPath(jTree1.getSelectionPath()));

        for (DefaultMutableTreeNode singleNode : dirNodeList) {
            TreePath path = new TreePath(singleNode.getPath());
            if (jTree1.isExpanded(path)) {
                if (!dirNodeList2.contains(singleNode)) {
                    System.out.println("is node expanded? " + jTree1.isExpanded(jTree1.getSelectionPath()));
                    dirNodeList2.add(singleNode);
                }
//                System.out.println("DIR NODE LSIT EXPANDED " + dirNodeList2);
            }
            if (jTree1.isCollapsed(path)) {
                if (dirNodeList2.contains(singleNode)) {
                    System.out.println("is node expanded? " + jTree1.isExpanded(jTree1.getSelectionPath()));
                    dirNodeList2.remove(singleNode);
                }
            }

        }
            System.out.println(" DirNodeList FINAL" + dirNodeList2);

    }

    public static void asdsa() {
//        System.out.println(" DirNodeList " + dirNodeList);
        for (DefaultMutableTreeNode pathx : dirNodeList) {
            System.out.println(pathx);
            TreePath path2 = new TreePath(pathx.getPath());
            jTree1.expandPath(path2);
        }
//        dirNodeList.remove(keyIter);

        System.out.println("ArrayList " + map);
//        DefaultMutableTreeNode expandableNode = map.get(2);
//        dirNodeList.remove(expandableNode);
        System.out.println("NEW DIR NODE " + dirNodeList);

    }

    private static void listFiles(File file, DefaultMutableTreeNode dirNodes) {
        File[] filesArr = file.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
                    dirNodes.add(fileNodes);
                }
            } else {
                listFilesRecursively(filesArr1, dirNodes);
            }
        }
    }

    private static void listFilesRecursively(File file, DefaultMutableTreeNode dirNodes) {
        DefaultMutableTreeNode subDirNodes = new DefaultMutableTreeNode(file.getName()); // level 2 (leaf) node
        dirNodes.add(subDirNodes);
        File[] filesArr = file.listFiles();
        for (File filesArr1 : filesArr) {
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
                    subDirNodes.add(fileNodes);
                }
            } else {
                DefaultMutableTreeNode subDirNodes0 = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
                subDirNodes.add(subDirNodes0);
                dirNodeList.add(subDirNodes0);
                map.put(keyIter, subDirNodes0);
                ++keyIter;
                listFiles(filesArr1, subDirNodes0);
            }
        }
        dirNodeList.add(subDirNodes);
        map.put(keyIter, subDirNodes);
        ++keyIter;
        dirNodes.add(subDirNodes);
    }

    public static void openFile(TreePath treePath) {
        File file = null;
        file = new File(root + masterFolder + treePath.toString().substring(1, treePath.toString().length() - 1).replaceAll(", ", "\\\\"));
        if (!file.isDirectory()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                }
            }
        }
    }

    public static void getFileCreationNSize() throws IOException {
        if (Main.jTree1.getSelectionPaths() != null) {
            if (Main.jTree1.getSelectionPaths().length <= 1) {
                File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[0].toString().substring(1, Main.jTree1.getSelectionPaths()[0].toString().length() - 1).replaceAll(", ", "\\\\"));
                Path file = fileFormat.toPath();

                if (!fileFormat.isDirectory()) {
                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                    GUI.labelCutterTreeThread(Main.jCreationDateLabel, GUI.formatDateTime(attr.lastModifiedTime()), 0, 16, 64, true);
                    GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(fileFormat.length()), 0, 16, 64, true);
                }
            } else {
                List<Long> fileSizes = new ArrayList<>();
                for (int i = 0; i < Main.jTree1.getSelectionPaths().length; i++) {
                    File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[i].toString().substring(1, Main.jTree1.getSelectionPaths()[i].toString().length() - 1).replaceAll(", ", "\\\\"));

                    if (i == Main.jTree1.getSelectionPaths().length) {
                        Path file = fileFormat.toPath();
                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                        Main.jCreationDateLabel.setText(GUI.formatDateTime(attr.lastModifiedTime()));
                    }

                    if (!fileFormat.isDirectory()) {
                        long fileSize = fileFormat.length();
                        fileSizes.add(fileSize);
                    }
                }
                long sum = fileSizes.stream().mapToLong(Long::longValue).sum();
                GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(sum), 0, 16, 64, true);
            }
        }
    }

    public static boolean checkFilesAreFromSameFolder(List<Path> treepaths) {
        boolean b = false;
        List<String> pathNames = new ArrayList<>();
        for (int i = 0; i < treepaths.size(); i++) {
            String fileName = treepaths.get(i).toFile().getName();
            String dirName = treepaths.get(i).toString();
            String pathName = dirName.replaceAll(fileName, "");
            pathNames.add(pathName);
        }
        for (String s : pathNames) {
            if (!s.equals(pathNames.get(0))) {
                System.out.println("Found mismatched folder");
                b = false;
            } else {
                b = true;
            }
        }
        return b;
    }
}
