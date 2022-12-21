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
    private static PrintWriter out;
    private static BufferedReader in;

    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    public static void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public static String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public static void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
//        sendFileRequest("khiemluong", new File("D:\\resume.pdf"));
//        postRequest(Statics.username, new File("C:\\Users\\User1\\OneDrive\\Pictures\\i-comiti - zoomed-out.png"));
//        tableRequest("khiemluong1");
        userRequest("khiemluong");
//        endSession("khiemluong");
    }

    public static boolean serverMonitor() {
        Thread serverConnection = new Thread(() -> {
            boolean b = true;
            while (b) {
                String listUSB = String.format("netstat -ano | findStr %s:%d", Server.getIP(), Statics.portNumber);
                System.out.println(listUSB);
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listUSB);
                String s = "";
                try {
                    Process sh = pb.start();
                    try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                        s = reader.readLine();
//                        while (b) {
                        System.out.println(s);
                        if (s == null) {
                            System.out.println("network is down");
                            b = false;

                        }
                        Thread.sleep(300);
                        sh.destroy();
//                        sh.destroyForcibly();
//                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if (!b) {
                System.out.println("network is down");
            }
        });
        serverConnection.start();
        return true;
    }

    public static void userRequest(String username) throws IOException, ClassNotFoundException, InterruptedException {
        clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        byte[] requestType_B = "USER".getBytes();
        byte[] userName_B = username.getBytes();
        byte[][] sendUserRequest_B = {requestType_B, userName_B};

        oos.writeObject(sendUserRequest_B);
        //read the server response message
        ois = new ObjectInputStream(clientSocket.getInputStream());
        List<String> message = (List<String>) ois.readObject();
        System.out.println("message from server: " + message);
        if (message.isEmpty()) {
            System.out.println("no files found.");
        } else {
            for (String fileName : message) {
                getRequest(username, new File(fileName));
            }
        }
    }

    public static boolean tableRequest(String username) throws IOException, ClassNotFoundException, InterruptedException {
        clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        byte[] requestType_B = "TABL".getBytes();
        byte[] userName_B = username.getBytes();
        byte[][] sendUserRequest_B = {requestType_B, userName_B};

        oos.writeObject(sendUserRequest_B);
        //read the server response message
        ois = new ObjectInputStream(clientSocket.getInputStream());
        boolean message = (boolean) ois.readObject();
        System.out.println("message from server: " + message);
        return message;
    }

    public static void getRequest(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        byte[] requestType_B = "GET".getBytes();
        byte[] userName_B = SQLHasher(username).getBytes();
        byte[] inputFileName_B = inputFile.getName().getBytes();
        byte[][] sendRequest_B = {requestType_B, userName_B, inputFileName_B};

        oos.writeObject(sendRequest_B);
        //read the server response message
        ois = new ObjectInputStream(clientSocket.getInputStream());
        byte[][] message = (byte[][]) ois.readObject();
        byte[][] bytes = {"NA".getBytes()};

        if (!Arrays.equals(message[0], bytes[0])) {
            Files.write(Paths.get(root + Main.masterFolder + Statics.nBoxName + File.separator + inputFile.getName()), message[1]);
            Files.setAttribute(Paths.get(root + Main.masterFolder + Statics.nBoxName + File.separator + inputFile.getName()), "basic:creationTime", Miscs.byteArrToFileTime(message[0]), NOFOLLOW_LINKS);
        } else {
            System.out.println("File not found");
        }
        //close resources
        ois.close();
        oos.close();
        Thread.sleep(10);
    }

    public static void postRequest(String username, File inputFile) throws IOException, InterruptedException, ClassNotFoundException {
        clientSocket = new Socket(Server.getIP(), Statics.portNumber);
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        byte[] requestType_B = "POST".getBytes();
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
        System.out.println(message);

        //close resources
        ois.close();
        oos.close();
        Thread.sleep(10);
    }

    public static boolean getSession(String username) {
        boolean b = false;
        try {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            byte[] requestType_B = "SESSION_GET".getBytes();
            byte[] userName_B = username.getBytes();
            byte[] ipAddress_B = Server.getIP().getBytes();
            byte[] os_B = getOS().getBytes();

            byte[][] getSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
            oos.writeObject(getSession_B);
            //read the server response message
            ois = new ObjectInputStream(clientSocket.getInputStream());
            b = (boolean) ois.readObject();
            System.out.println("SESSION " + b);
            ois.close();
            oos.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return b;
    }

    public static void endSession(String username) {
        try {
            clientSocket = new Socket(Server.getIP(), Statics.portNumber);
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            byte[] requestType_B = "SESSION_END".getBytes();
            byte[] userName_B = username.getBytes();
            byte[] ipAddress_B = Server.getIP().getBytes();
            byte[] os_B = getOS().getBytes();

            byte[][] getSession_B = {requestType_B, userName_B, ipAddress_B, os_B};
            oos.writeObject(getSession_B);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
