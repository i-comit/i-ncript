/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.shared;

import static com.i_comit.shared.Enums.getOS;
import com.i_comit.windows.Main;
import com.i_comit.windows.Statics;
import static com.i_comit.shared.Hasher.SQLHasher;
import static com.i_comit.windows.Main.root;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Client {

    public static Socket clientSocket;

    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    public static boolean userRequest(String username) {
        boolean b = true;
        try {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
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
                }
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
//            ex.printStackTrace();
            System.out.println("switching to offline mode");
            internetBool = false;
        }
        return b;
    }

    public static void getRecords(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        if (internetBool) {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
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
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
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
        clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
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
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            byte[] requestType_B = "PST_TABL".getBytes();
            byte[] userName_B = username.getBytes();
            byte[][] postTableRequest_B = {requestType_B, userName_B};

            oos.writeObject(postTableRequest_B);
            //read the server response message
            ois = new ObjectInputStream(clientSocket.getInputStream());
            String message = (String) ois.readObject();
        }
    }

    public static boolean startSession(String username) {
        boolean b = false;
        if (internetBool) {
            try {
                clientSocket = new Socket(Server.getIP(), Statics.portNumber);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
                byte[] requestType_B = "STR_SESN".getBytes();
                byte[] userName_B = username.getBytes();
                byte[] ipAddress_B = Server.getIP().getBytes();
                byte[] os_B = getOS().getBytes();

                byte[][] startSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
                oos.writeObject(startSession_B);
                //read the server response message
                ois = new ObjectInputStream(clientSocket.getInputStream());
                b = (boolean) ois.readObject();
                System.out.println("SESSION " + b);
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
                clientSocket = new Socket(Server.getIP(), Statics.portNumber);
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
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
    }
    public static boolean internetBool = false;

    public static void internetMonitor() {
        Thread serverConnection = new Thread(() -> {
            while (true) {
                String listUSB = String.format("netstat -ano | findStr %s:%d", Server.getIP(), Statics.portNumber);
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listUSB);
                String s = "";
                try {
                    Process sh = pb.start();
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                        s = reader.readLine();
                        if (s == null) {
                            internetBool = false;
                            System.out.println(internetBool);
                        } else {
                            internetBool = true;
                            System.out.println(internetBool);
                        }
                        Thread.sleep(250);
                        sh.destroy();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        serverConnection.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
//        sendFileRequest("khiemluong", new File("D:\\resume.pdf"));
//        postRequest(Statics.username, new File("C:\\Users\\User1\\OneDrive\\Pictures\\i-comiti - zoomed-out.png"));
//        tableRequest("khiemluong1");
//        userRequest("khiemluong");
//        endSession("khiemluong");
        internetMonitor();
    }
}
