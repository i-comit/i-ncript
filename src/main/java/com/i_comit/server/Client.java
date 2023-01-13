/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.server;

import com.i_comit.shared.Miscs;
import static com.i_comit.shared.Enums.getOS;
import com.i_comit.windows.Main;
import com.i_comit.windows.Statics;
import static com.i_comit.shared.Hasher.SQLHasher;
import com.i_comit.windows.GUI;
import static com.i_comit.windows.Main.jAdminLabel;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.jClientIPInput;
import static com.i_comit.windows.Main.root;
import com.i_comit.windows.TreeView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.tree.TreePath;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Client {

    public static Socket clientSocket;
    public static ObjectOutputStream oos = null;
    public static ObjectInputStream ois = null;

    public static void getServerSocket() throws IOException {
        try {
            if (Main.adminBool) {
                clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            } else {
                if (getClientIP()) {
                    jAdminLabel.setVisible(true);
                    jAdminLabel.setToolTipText(Server.getIP());
                    jClientIPInput.setVisible(false);
                    Main.jClientIPInput.setText(Server.getIP());
                } else {
                    jAdminLabel.setVisible(false);
                    jClientIPInput.setVisible(true);
                }
                clientSocket = new Socket(Main.jClientIPInput.getText(), Statics.portNumber);
            }
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (UnknownHostException | ConnectException ex) {
            Main.sendSQLToggle();
        }
    }

    public static boolean userRequest(String username) {
        boolean b = true;
        try {
            getServerSocket();
            byte[] requestType_B = "GET_USER".getBytes();
            byte[] userName_B = username.getBytes();
            byte[][] userRequest_B = {requestType_B, userName_B};
            oos.writeObject(userRequest_B);
            //read the server response message
            ois = new ObjectInputStream(clientSocket.getInputStream());
            List<String> message = (List<String>) ois.readObject();
            if (message.isEmpty()) {
                System.out.println("no files found.");
                b = false;
            } else {
                for (String fileName : message) {
                    getRecords(username, new File(fileName));
                    System.out.println("retrieved: " + fileName);
                    Main.refreshTreeView(Statics.receiveFolder, TreeView.receiveCaretPos);
                    GUI.getGB();
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            System.out.println("host is offline");
            b = false;
        }
//        }
        return b;
    }

    public static void getRecords(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        getServerSocket();
        byte[] requestType_B = "GET_RECD".getBytes();
        byte[] userName_B = SQLHasher(username).getBytes();
        byte[] inputFileName_B = inputFile.getName().getBytes();
        byte[][] getRequest_B = {requestType_B, userName_B, inputFileName_B};

        oos.writeObject(getRequest_B);
        //read the server response message
        ois = new ObjectInputStream(clientSocket.getInputStream());
        byte[][] message = (byte[][]) ois.readObject();
        byte[][] bytes = {"NA".getBytes()};

        if (!Arrays.equals(message[0], bytes[0])) {
            Files.write(Paths.get(root + Main.masterFolder + Statics.nBoxName + File.separator + inputFile.getName()), message[1]);
            Files.setAttribute(Paths.get(root + Main.masterFolder + Statics.nBoxName + File.separator + inputFile.getName()), "basic:creationTime", Miscs.byteArrToFileTime(message[0]), NOFOLLOW_LINKS);
            System.out.println(Miscs.byteArrToFileTime(message[0]));
        } else {
            System.out.println("File not found");
        }
        //close resources
        ois.close();
        oos.close();
    }

    public static boolean getServerCap(TreePath[] treePaths) throws IOException, ClassNotFoundException {
        List<Long> fileSizes = new ArrayList<>();

        for (int i = 0; i < treePaths.length; i++) {
            Path filePath = TreeView.convertTreePathToFile(Main.jTree1.getSelectionPaths(), i).toPath();
            long fileSize = filePath.toFile().length();
            fileSizes.add(fileSize);
        }
        long sum = fileSizes.stream().mapToLong(Long::longValue).sum();
        System.out.println("filesizeSum " + sum);
        getServerSocket();
        byte[] requestType_B = "PST_FILESIZE".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(sum);
        byte[] bytesNum = buffer.array();
        byte[][] postRequest_B = {requestType_B, bytesNum};
        oos.writeObject(postRequest_B);
        ois = new ObjectInputStream(clientSocket.getInputStream());
        boolean message = (boolean) ois.readObject();
        ois.close();
        oos.close();
        if (message) {
            System.out.println("server has enough space");
            return true;
        } else {
            System.out.println("server does not have enough space");
            return false;
        }
    }

    public static void postRecords(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        getServerSocket();
        byte[] requestType_B = "PST_RECD".getBytes();
        byte[] userName_B = SQLHasher(username).getBytes();
        byte[] inputFileName_B = inputFile.getName().getBytes();
        BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);
        FileTime fileTime = attr.creationTime();
        String fileTimeS = fileTime.toString();
        byte[] inputFileDate_B = fileTimeS.getBytes();
        byte[] inputFileBytes_B = Files.readAllBytes(inputFile.toPath());
        byte[][] postRequest_B = {requestType_B, userName_B, inputFileName_B, inputFileDate_B, inputFileBytes_B};

        oos.writeObject(postRequest_B);
        oos.close();
    }

    public static boolean getTable(String username) throws IOException, ClassNotFoundException, InterruptedException {
        getServerSocket();
        byte[] requestType_B = "GET_TABL".getBytes();
        byte[] userName_B = username.getBytes();
        byte[][] getTableRequest_B = {requestType_B, userName_B};

        oos.writeObject(getTableRequest_B);
        ois = new ObjectInputStream(clientSocket.getInputStream());
        boolean message = (boolean) ois.readObject();
        ois.close();
        oos.close();
        return message;
    }

    public static boolean postTable(String username, Main main) throws IOException, ClassNotFoundException, InterruptedException {
        getServerSocket();
        byte[] requestType_B = "PST_TABL".getBytes();
        byte[] userName_B = username.getBytes();
        byte[][] postTableRequest_B = {requestType_B, userName_B};
        oos.writeObject(postTableRequest_B);
        ois = new ObjectInputStream(clientSocket.getInputStream());
        String message = (String) ois.readObject();
        System.out.println(message);
        ois.close();
        oos.close();
        if (!message.equals("MAX_USER_ERROR")) {
            System.out.println(message + " account connected");
            return true;
        } else {
            GUI.t.interrupt();
            GUI.labelCutterLoginThread(jAlertLabel, "server has max amount of users.", 20, 40, 1600, main);
            return false;
        }
    }

    public static boolean startSession(String username, Main main) {
        boolean b = false;
        try {
            getServerSocket();
            byte[] requestType_B = "STR_SESN".getBytes();
            byte[] userName_B = username.getBytes();
            byte[] ipAddress_B = Server.getIP().getBytes();
            byte[] os_B = getOS().getBytes();

            byte[][] startSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
            oos.writeObject(startSession_B);
            ois = new ObjectInputStream(clientSocket.getInputStream());
            b = (boolean) ois.readObject();
            if (b) {
                System.out.println("you have started your session.");
            } else {
                GUI.t.interrupt();
                GUI.labelCutterLoginThread(jAlertLabel, "user is already logged in.", 30, 50, 1500, main);
            }
            ois.close();
            oos.close();
        } catch (UnknownHostException | SocketException | NullPointerException ex) {
            GUI.t.interrupt();
            GUI.labelCutterLoginThread(jAlertLabel, "invalid IP address provided.", 20, 40, 2000, main);
            Main.jTextField1.setText("");
            Main.jPasswordField1.setText("");
            Main.jTextField1.requestFocus();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return b;
    }

    public static void endSession(String username) {
        try {
            getServerSocket();
            byte[] requestType_B = "END_SESN".getBytes();
            byte[] userName_B = username.getBytes();
            byte[] ipAddress_B = Server.getIP().getBytes();
            byte[] os_B = getOS().getBytes();
            byte[][] endSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
            oos.writeObject(endSession_B);
            oos.close();
            clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean getClientIP() {
        String netstatQuery = String.format("netstat -ano | findStr %s:%d", Server.getIP(), Statics.portNumber);
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", netstatQuery);
        String s = "";
        try {
            Process sh = pb.start();
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                while ((s = reader.readLine()) != null) {
                    if (!s.equals("")) {
                        return true;
                    }
                }
                if (s == null) {
                    return false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private static byte[] adminReq_B = "ADMIN".getBytes();

    public static void adminRequests(int requestType) {
        try {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            if (requestType == 0) {
                byte[] serverReq_B = "SERVR".getBytes();
                byte[][] startSession_B = {adminReq_B, serverReq_B};
                oos.writeObject(startSession_B);
            }
            if (requestType == 1) {
                byte[] serverReq_B = "CLOSE".getBytes();
                byte[][] startSession_B = {adminReq_B, serverReq_B};
                oos.writeObject(startSession_B);
            }
            oos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
