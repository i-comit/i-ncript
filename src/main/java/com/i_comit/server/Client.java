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
import static com.i_comit.windows.Main.adminBool;
import static com.i_comit.windows.Main.jAlertLabel;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Client {

    private static Socket clientSocket;
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    private static void getServerSocket() throws IOException {
        if (Main.adminBool) {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        } else {
            if (getClientIP()) {
                clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            } else {
                if (Main.jList2.getSelectedValue().equals("GO OFFLINE")) {
                    internetBool = false;
                } else {
                    clientSocket = new Socket(Main.jList2.getSelectedValue(), Statics.portNumber);
                }
            }
//            Main.jList2.setVisible(true);
//            Main.jScrollPane8.setVisible(true);
        }
        oos = new ObjectOutputStream(clientSocket.getOutputStream());

    }

    public static boolean userRequest(String username) {
        boolean b = true;
        if (internetBool) {
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
                        System.out.println("server found: " + fileName);
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException ex) {
//                System.out.println("going to offline mode");
            }
        }
        return b;
    }

    public static void getRecords(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        if (internetBool) {
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
    }

    public static void postRecords(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        if (internetBool) {
            getServerSocket();
            byte[] requestType_B = "PST_RECD".getBytes();
            byte[] userName_B = SQLHasher(username).getBytes();
            byte[] inputFileName_B = inputFile.getName().getBytes();
            BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.creationTime();
            String fileTimeS = fileTime.toString();
            byte[] inputFileDate_B = fileTimeS.getBytes();
            byte[] inputFileBytes_B = Files.readAllBytes(inputFile.toPath());
            byte[][] posRequest_B = {requestType_B, userName_B, inputFileName_B, inputFileDate_B, inputFileBytes_B};

            oos.writeObject(posRequest_B);
            //read the server response message
            ois = new ObjectInputStream(clientSocket.getInputStream());
            String message = (String) ois.readObject();
            //close resources
            ois.close();
            oos.close();
            Thread.sleep(10);
        }
    }

    public static boolean getTable(String username) throws IOException, ClassNotFoundException, InterruptedException {
        getServerSocket();
        byte[] requestType_B = "GET_TABL".getBytes();
        byte[] userName_B = username.getBytes();
        byte[][] getTableRequest_B = {requestType_B, userName_B};

        oos.writeObject(getTableRequest_B);
        //read the server response message
        ois = new ObjectInputStream(clientSocket.getInputStream());
        boolean message = (boolean) ois.readObject();
        return message;
    }

    public static void postTable(String username) throws IOException, ClassNotFoundException, InterruptedException {
        if (internetBool) {
            getServerSocket();
            byte[] requestType_B = "PST_TABL".getBytes();
            byte[] userName_B = username.getBytes();
            byte[][] postTableRequest_B = {requestType_B, userName_B};
            oos.writeObject(postTableRequest_B);
            System.out.println("account connected.");
            oos.close();
        }
    }

    public static boolean startSession(String username) {
        boolean b = false;
        if (internetBool) {
            try {
                getServerSocket();
                byte[] requestType_B = "STR_SESN".getBytes();
                byte[] userName_B = username.getBytes();
                byte[] ipAddress_B = Server.getIP().getBytes();
                byte[] os_B = getOS().getBytes();

                byte[][] startSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
                oos.writeObject(startSession_B);
                //read the server response message
                ois = new ObjectInputStream(clientSocket.getInputStream());
                b = (boolean) ois.readObject();
                System.out.println("you have started your session.");
                ois.close();
                oos.close();
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return b;
    }

    public static void endSession(String username) {
        if (internetBool) {
            try {
                getServerSocket();
                byte[] requestType_B = "END_SESN".getBytes();
                byte[] userName_B = username.getBytes();
                byte[] ipAddress_B = Server.getIP().getBytes();
                byte[] os_B = getOS().getBytes();

                byte[][] endSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
                oos.writeObject(endSession_B);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.exit(0);
    }
    public static boolean internetBool = true;
    private static boolean internetBool1 = false;
    public static Thread clientMonitor_T;

    public static synchronized void clientMonitor() {
        clientMonitor_T = new Thread(() -> {
            while (true) {
                String netstatQuery = String.format("netstat -ano | findStr %s:%d", Server.getIP(), Statics.portNumber);
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", netstatQuery);
                String s = "";
                try {
                    Process sh = pb.start();
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                        s = reader.readLine();
                        if (s == null) {
                            internetBool = false;
                            if (!internetBool1) {
                                System.out.println("network is down");
                                if (Main.adminBool) {
                                    Main.jScrollPane8.setVisible(false);
                                    Main.jAdminLabel.setVisible(true);
//                                    Server.portKill();
//                                    Server.Sessions sessions = new Server.Sessions();
//                                    Server.initDatabase();
//                                    sessions.clearSessions();
//                                    Server.serverKill(".server.exe", false);
//
//                                    if (Server.serverSocket == null) {
//                                        Server.socketStart(8665);
//                                    } else {
//                                        System.out.println("the server is already active");
//                                        Server.serverSocket.close();
//                                    }
                                } else {
                                    Main.jScrollPane8.setVisible(true);
                                    Main.jAdminLabel.setVisible(false);
                                }
                                if (adminBool) {
                                    File serverExeFile = new File(root + masterFolder + ".server.exe");
                                    if (serverExeFile.exists()) {
                                        try {
                                            String exeServer = String.format("start %s", serverExeFile);
                                            ProcessBuilder pb1 = new ProcessBuilder("cmd.exe", "/c", exeServer);
                                            pb1.start();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    } else {
                                        System.out.println("server file does not exists");
                                    }
                                } else {
                                }
                                if (!Statics.username.equals("")) {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "host has disconnected", 0, 25, 1500, false);
                                    Main.sendSQLToggle();
                                }
                                internetBool1 = true;
                            }
                        } else if (s.contains("TIME_WAIT") || (s.contains("CLOSE_WAIT"))) {
                            internetBool = false;
                            if (!internetBool1) {
                                System.out.println("network is in waiting");
                                if (Main.adminBool) {
                                    Main.jScrollPane8.setVisible(false);
                                    Main.jAdminLabel.setVisible(true);
//                                    Server.portKill();
//                                    Server.serverKill(".server.exe", false);
//                                    Server.Sessions sessions = new Server.Sessions();
//                                    Server.initDatabase();
//                                    sessions.clearSessions();
//                                    if (Server.serverSocket == null) {
//                                        Server.socketStart(8665);
//                                    } else {
//                                        System.out.println("the server is already active");
//                                        Server.serverSocket.close();
//                                    }
                                } else {
                                    Main.jScrollPane8.setVisible(true);
                                    Main.jAdminLabel.setVisible(false);
                                }
                                String listUSB1 = String.format("java -jar %s", ".server.jar");
                                ProcessBuilder pb1 = new ProcessBuilder("cmd.exe", "/c", listUSB1);
                                pb1.start();
                                if (!Statics.username.equals("")) {
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "host has disconnected", 0, 25, 1500, false);
                                    Main.sendSQLToggle();
                                }
                                internetBool1 = true;
                            }
                        } else {
                            internetBool = true;
                            if (internetBool1) {
                                System.out.println("network is up");
                                if (Main.adminBool) {
                                    Main.jScrollPane8.setVisible(false);
                                    Main.jAdminLabel.setVisible(true);
                                } else {
                                    Main.jScrollPane8.setVisible(true);
                                    Main.jAdminLabel.setVisible(false);
                                }
                                if (!Statics.username.equals("")) {
                                    getTable(Statics.username);
                                    userRequest(Statics.username);
                                    Statics.inboxMonitor();
                                    GUI.t.interrupt();
                                    GUI.labelCutterThread(jAlertLabel, "host has connected", 0, 25, 1500, false);
                                    Main.sendSQLToggle();
                                }
                                internetBool1 = false;
                            }
                        }
                        Thread.sleep(500);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        clientMonitor_T.start();
    }
    private static DefaultListModel ipList = new DefaultListModel();

    public static List<String> pingCmdWindows() {
        ipList.clear();
        Main.jList2.removeAll();
        List<String> ipAddresses = new ArrayList<>();
        String listInterfaces = String.format("arp -a | findStr dynamic");
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listInterfaces);
        try {
            Process sh = pb.start();
            String s = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()));
            String IPaddress = "";
            ipList.addElement("⇒ offline mode");
            while ((s = reader.readLine()) != null) {
                IPaddress = s.substring(0, 16).trim();
                if (IPaddress.length() > 12) {
                    ipAddresses.add(IPaddress);
                    ipList.addElement(IPaddress);
                }
            }
            Main.jList2.setModel(ipList);
            Main.jList2.setSelectedIndex(0);
        } catch (IOException e) {
            System.out.println(e);
        }
        return ipAddresses;
    }

    public static void adminRequest(int requestType) {
        if (internetBool) {
            try {
                getServerSocket();
                byte[] adminReq_B = "ADMIN".getBytes();
                if (requestType == 0) {
                    byte[] serverReq_B = "SERVR".getBytes();
                    byte[][] startSession_B = {adminReq_B, serverReq_B};
                    oos.writeObject(startSession_B);
                }
                //read the server response message
                oos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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

}
