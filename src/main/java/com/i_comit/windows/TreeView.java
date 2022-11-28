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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
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
        return model;
    }

    private static List<DefaultMutableTreeNode> dirNodeList = new ArrayList<>();
    private static List<DefaultMutableTreeNode> receiveNodeList = new ArrayList<>();
    private static List<DefaultMutableTreeNode> sendNodeList = new ArrayList<>();

    private static List<TreePath> treePaths = new ArrayList<>();
    private static List<TreePath> receiveTreePaths = new ArrayList<>();
    private static List<TreePath> sendTreePaths = new ArrayList<>();

    public static void expandedNodesSwitch(int toolMode, DefaultMutableTreeNode treeNode) {
        switch (toolMode) {
            case 0:
                System.out.println("I-NCRIPT");
                dirNodeList.add(treeNode);
                break;
            case 1:
                System.out.println("N-BOX");
                receiveNodeList.add(treeNode);
                break;
            case 2:
                System.out.println("O-BOX");
                sendNodeList.add(treeNode);
                break;
            case 3:
                System.out.println("I-NCRIPT");
                dirNodeList.add(treeNode);
                break;
        }
    }

    public static void storeExpandedNodes(int toolMode) {
        switch (toolMode) {
            case 0:
                for (DefaultMutableTreeNode singleNode : dirNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!treePaths.contains(path)) {
                            treePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (treePaths.contains(path)) {
                            treePaths.remove(path);
                        }
                    }
                }
//                System.out.println("DirNodeList " + dirNodeList);
                System.out.println("StoreTreePaths " + treePaths);
                break;
            case 1:
                for (DefaultMutableTreeNode singleNode : receiveNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!receiveTreePaths.contains(path)) {
                            receiveTreePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (receiveTreePaths.contains(path)) {
                            receiveTreePaths.remove(path);
                        }
                    }
                }
//                System.out.println("ReceiveNodeList " + receiveNodeList);
                System.out.println("receiveTreePaths " + receiveTreePaths);
                break;
            case 2:
                for (DefaultMutableTreeNode singleNode : sendNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!sendTreePaths.contains(path)) {
                            sendTreePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (sendTreePaths.contains(path)) {
                            sendTreePaths.remove(path);
                        }
                    }
                }
//                System.out.println("SendNodeList " + sendNodeList);
                System.out.println("sendTreePaths " + sendTreePaths);
                break;
            case 3:
                for (DefaultMutableTreeNode singleNode : dirNodeList) {
                    TreePath path = new TreePath(singleNode.getPath());
                    if (jTree1.isExpanded(path)) {
                        if (!treePaths.contains(path)) {
                            treePaths.add(path);
                        }
                    }
                    if (jTree1.isCollapsed(path)) {
                        if (treePaths.contains(path)) {
                            treePaths.remove(path);
                        }
                    }
                }
//                System.out.println("DirNodeList " + dirNodeList);
                System.out.println("StoreTreePaths " + treePaths);
                break;
        }

//        System.out.println("storeExpandedNodes treePaths " + treePaths);
    }

    public static void expandTreeNode(Path path) {
        String fileName = path.toFile().getName();
        System.out.println("expandTreeNode fileName " + fileName);
        switch (fileName) {
            case "i-ncript":
                for (DefaultMutableTreeNode pathx : dirNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : treePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            System.out.println("MATCH");
                            jTree1.expandPath(path2);
                        }
                    }
                }
                break;
            case "n-box":
                for (DefaultMutableTreeNode pathx : receiveNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : receiveTreePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            System.out.println("MATCH");
                            jTree1.expandPath(path2);
                        }
                    }
                }
                break;
            case "o-box":
                for (DefaultMutableTreeNode pathx : sendNodeList) {
                    TreePath path2 = new TreePath(pathx.getPath());
                    for (TreePath treePath : sendTreePaths) {
                        if (treePath.toString().equals(path2.toString())) {
                            System.out.println("MATCH");
                            jTree1.expandPath(path2);
                        }
                    }
                }
                break;
        }

//        dirNodeList.clear();
//        receiveNodeList.clear();
//        sendNodeList.clear();
    }

    public static void setRootName(String rootName) {
        treeRoot.setUserObject(rootName);
    }

    private static void listRoot(List<Path> directories) {
        dirNodeList.clear();
        receiveNodeList.clear();
        sendNodeList.clear();

        File[] filesArr = directories.get(0).toFile().listFiles();
        for (File filesArr1 : filesArr) {
            DefaultMutableTreeNode dirNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
            if (!filesArr1.isDirectory()) {
                if (!filesArr1.getName().endsWith("Thumbs.db")) {
                    DefaultMutableTreeNode fileNodes = new DefaultMutableTreeNode(filesArr1.getName()); // level 2 (leaf) node
                    treeRoot.add(fileNodes);
                }
            } else {
//                dirNodeList.add(dirNodes);
                treeRoot.add(dirNodes);
                listFiles(filesArr1, dirNodes);
//                System.out.println("dirNode name " + directories.get(0).toFile().getName());
                expandedNodesSwitch(Statics.toolMode, dirNodes);
            }
        }
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
//                dirNodeList.add(subDirNodes0);
                expandedNodesSwitch(Statics.toolMode, subDirNodes0);
                listFiles(filesArr1, subDirNodes0);
            }
        }
//        dirNodeList.add(subDirNodes);
        expandedNodesSwitch(Statics.toolMode, subDirNodes);

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
                    BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class
                    );
                    GUI.labelCutterTreeThread(Main.jCreationDateLabel, GUI.formatDateTime(attr.lastModifiedTime()), 0, 16, 64, true);
                    GUI.labelCutterTreeThread(Main.jFileSizeLabel, Memory.byteFormatter(fileFormat.length()), 0, 16, 64, true);
                }
            } else {
                List<Long> fileSizes = new ArrayList<>();
                for (int i = 0; i < Main.jTree1.getSelectionPaths().length; i++) {
                    File fileFormat = new File(root + masterFolder + Main.jTree1.getSelectionPaths()[i].toString().substring(1, Main.jTree1.getSelectionPaths()[i].toString().length() - 1).replaceAll(", ", "\\\\"));

                    if (i == Main.jTree1.getSelectionPaths().length) {
                        Path file = fileFormat.toPath();
                        BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class
                        );
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
