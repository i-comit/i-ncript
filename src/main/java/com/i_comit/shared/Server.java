/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.shared;

import static com.i_comit.shared.Hasher.SQLHasher;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    private static synchronized void socketStart(int port) {
        Session session = new Session();
        Table table = new Table();
        Record record = new Record();
        try {
            InetAddress addr = InetAddress.getByName(Server.getIP());
            serverSocket = new ServerSocket(port, 50, addr);
            Thread serverConnection = new Thread(() -> {
                while (true) {
                    try {
                        clientSocket = serverSocket.accept();
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                        System.out.println(ois.available());
                        byte[][] message = (byte[][]) ois.readObject();
                        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

                        if (Arrays.equals(message[0], "GET_USER".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            List<String> fileRecords = findFiles(userName);
                            oos.writeObject(fileRecords);
                        }
                        if (Arrays.equals(message[0], "GET_RECD".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            String fileName = new String(message[2], StandardCharsets.UTF_8);
                            if (findRecord(userName, fileName)) {
                                byte[][] b = getFileBlob(userName, fileName);
                                oos.writeObject(b);
                                record.deleteRecord(userName, fileName);
                            } else {
                                byte[][] b = {"NA".getBytes()};
                                oos.writeObject(b);
                            }
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        if (Arrays.equals(message[0], "PST_RECD".getBytes())) {
                            try {
                                String userName = new String(message[1], StandardCharsets.UTF_8);
                                String fileName = new String(message[2], StandardCharsets.UTF_8);
                                String fileDate = new String(message[3], StandardCharsets.UTF_8);
                                insertClientRecord(userName, fileName, fileDate, message[4]);
                                oos.writeObject(fileName + " has been received");

                                ois.close();
                                oos.close();
                                clientSocket.close();
                            } catch (UnsupportedEncodingException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (Arrays.equals(message[0], "GET_TABL".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            boolean b = table.listTables(userName);
                            oos.writeObject(b);
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        if (Arrays.equals(message[0], "PST_TABL".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            String msg = table.createTable(userName);
                            oos.writeObject(msg);
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        if (Arrays.equals(message[0], "STR_SESN".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            String ipAddress = new String(message[2], StandardCharsets.UTF_8);
                            String OS = new String(message[3], StandardCharsets.UTF_8);
                            boolean b = session.requestSession(userName, ipAddress, OS);
                            if (b) {
                                System.out.println(userName + " has connected to a session.");
                            } else {
                                System.out.println(userName + " already has an active session.");
                            }
                            oos.writeObject(b);
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        if (Arrays.equals(message[0], "END_SESN".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            session.endSession(userName);
                            System.out.println(userName + " has ended their session.");
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        Thread.sleep(100);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            serverConnection.start();
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
//            ex.printStackTrace();

    }
//
    private static String dbFileName = ".💽🗄️.db";
//    private static String dbPath = Paths.get("").toAbsolutePath().toString() + File.separator + "runtime" + File.separator + "bin" + File.separator + "server" + File.separator + dbFileName;
    private static String dbPath = root + masterFolder + "runtime" + File.separator + "bin" + File.separator + "server" + File.separator + dbFileName;

    public static String url = "jdbc:sqlite:" + dbPath;

    private static void initDatabase() throws UnsupportedEncodingException {
        File urlF = new File(dbPath);
        if (urlF.exists()) {
            try {
                System.out.println("database already exists.");
                Files.setAttribute(urlF.toPath(), "dos:hidden", true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            try ( Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    DatabaseMetaData meta = conn.getMetaData();
                    Files.setAttribute(urlF.toPath(), "dos:hidden", true);
                    System.out.println("database has been created.");
                }

            } catch (SQLException | IOException e) {
//                System.out.println(e.getMessage());
            }
        }
        String tbl = "CREATE TABLE IF NOT EXISTS 'FILES-DB' ('user-name' text NOT NULL, 'file-name' text NOT NULL PRIMARY KEY, 'file-date' text NOT NULL, 'file-bytes' blob);";
        String tbl1 = "CREATE TABLE IF NOT EXISTS 'I-NCRIPT' ('user-name' text NOT NULL, 'password' text NOT NULL, 'app-version' text);";
        String tbl2 = "CREATE TABLE IF NOT EXISTS 'SESSIONS' ('user-name' text NOT NULL PRIMARY KEY, 'ip-address' text NOT NULL, 'os' text NOT NULL);";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(tbl);
            stmt.execute(tbl1);
            stmt.execute(tbl2);
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }

    }

    private static void insertClientRecord(String clientUsername, String fileName, String fileDate, byte[] fileBytes) throws UnsupportedEncodingException {
        String sql = "INSERT INTO 'FILES-DB' ('user-name', 'file-name', 'file-date', 'file-bytes') VALUES( ?, ?, ?, ?)";
        try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientUsername);
            pstmt.setString(2, fileName);
            pstmt.setString(3, fileDate);
            pstmt.setBytes(4, fileBytes);
            pstmt.executeLargeUpdate();
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        }
    }

    private static List<String> findFiles(String username) throws UnsupportedEncodingException {
        List<String> fileRecords = new ArrayList<>();
        String sql = String.format("SELECT * FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\"", SQLHasher(username));
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
//                System.out.println(rs.getString(2));
                fileRecords.add(rs.getString(2));
            }
            if (fileRecords.isEmpty()) {
                System.out.println("No records found");
            }
        } catch (SQLException ex) {
//        } catch (IOException ex) {
//            ex.printStackTrace();

        }
        return fileRecords;
    }

    private static boolean findRecord(String username, String fileName) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\" AND \"file-name\" = \"%s\"", username, fileName);
        String blob = "";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                blob = rs.getString(2);
                return true;
            }
            if (blob.equals("")) {
                System.out.println("No file found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
        }
        return false;
    }

    private static byte[][] getFileBlob(String clientUsername, String fileName) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\" AND \"file-name\" = \"%s\"", clientUsername, fileName);
        String blob = "";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                blob = rs.getString(2);
//                System.out.println(rs.getString(1) + "\t"
//                        + rs.getString(2) + "\t");
                byte[] dataForWriting = rs.getBytes(3);
                byte[] fileDateByte = rs.getBytes(4);
                byte[][] byteArr = {dataForWriting, fileDateByte};
                return byteArr;
            }
            if (blob.equals("")) {
                System.out.println("No file found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getIP() {
        String listUSB = "ipconfig";
        String s = "";
        List<String> IPv4Addresses = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec(listUSB);
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.readLine();
                while ((s = reader.readLine()) != null) {
                    if (s.trim().startsWith("IPv4 Address.")) {
                        IPv4Addresses.add(s.trim().substring(36));
                    }
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
//        System.out.println(IPv4Addresses.get(IPv4Addresses.size() - 1));
        return IPv4Addresses.get(IPv4Addresses.size() - 1);
    }

    public static void serverKill(String appName, boolean exitAppBool) {
        String listTasks = String.format("tasklist | findStr %s", appName);
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listTasks);
        String s = "";
        try {
            Process sh = pb.start();
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                while ((s = reader.readLine()) != null) {
                    s = reader.readLine().substring(29, 34);
                    System.out.println(s);
                    String killTask = String.format("taskkill /PID %s /F", s);
                    ProcessBuilder pb1 = new ProcessBuilder("cmd.exe", "/c", killTask);
                    pb1.start();
                    Thread.sleep(50);
//                    sh.destroy();
                    if (reader.readLine() == null) {
                        System.out.println("killed server " + s);
                        break;
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            if (exitAppBool) {
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, SQLException, ClassNotFoundException {
        serverKill(".server.exe",false);
        Session session = new Session();
        Record record = new Record();
        initDatabase();
        session.clearSessions();
        record.insertRecord("khiemluong", new File("D:\\resume.pdf"));
        record.insertRecord("khiemluong", new File("D:\\i-comiti.ico"));
        record.insertRecord("khiemluong", new File("D:\\read-me.md"));
        record.listRecords("khiemluong");

        if (serverSocket == null) {
            socketStart(8665);
        } else {
            System.out.println("the server is already active");
            serverSocket.close();
        }
    }
}

class Session {

    public void clearSessions() throws UnsupportedEncodingException {
        String sql = "DELETE FROM \"SESSIONS\";";
        String sql1 = "VACUUM;";
        try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql);  PreparedStatement pstmt1 = conn.prepareStatement(sql1)) {
            pstmt.executeUpdate();
            pstmt1.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void listSessions(String username) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM \"SESSIONS\" WHERE \"user-name\"  = \"%s\";", SQLHasher(username));
        String str = "";
        try ( Connection conn = DriverManager.getConnection(Server.url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                str = rs.getString(1);
                if (rs.getString(1).equals(SQLHasher(username))) {
//                    System.out.println(username + " is connected to session");
                }
            }
            if (str.equals("")) {
//                System.out.println("no session for " + username);
            }
        } catch (SQLException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }

    public boolean requestSession(String username, String ipAddress, String OS) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM \"SESSIONS\" WHERE \"user-name\"  = \"%s\";", SQLHasher(username));
        String str = "";
        try ( Connection conn = DriverManager.getConnection(Server.url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                str = rs.getString(1);
                if (rs.getString(1).equals(SQLHasher(username))) {
                    return false;
                }
            }
            if (str.equals("")) {
                System.out.println("starting session for " + username);
                String sql1 = "INSERT INTO 'SESSIONS' ('user-name', 'ip-address', 'os') VALUES ( ?, ?, ?);";
                try ( PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setString(1, SQLHasher(username));
                    pstmt.setString(2, ipAddress);
                    pstmt.setString(3, OS);
                    pstmt.executeLargeUpdate();
                } catch (SQLException e) {
//                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException ex) {
//            ex.printStackTrace();
        }
        return true;
    }

    public void endSession(String username) throws UnsupportedEncodingException {
        String sql = String.format("DELETE FROM \"SESSIONS\" WHERE \"user-name\"  = \"%s\";", SQLHasher(username));
        try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
            listSessions(SQLHasher(username));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class Table {

    public String createTable(String username) throws UnsupportedEncodingException {
        String tbl = String.format("CREATE TABLE IF NOT EXISTS \"%s\" ('recipient-name' text NOT NULL, 'comm-text' text NOT NULL, 'comm-date' text NOT NULL);", SQLHasher(username));
        try ( Connection conn = DriverManager.getConnection(Server.url);  Statement stmt = conn.createStatement()) {
            stmt.execute(tbl);
            System.out.println(username + " has created an account");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "account created for " + username;
    }

    public boolean listTables(String username) throws UnsupportedEncodingException {
        boolean b = false;
        try ( Connection conn = DriverManager.getConnection(Server.url)) {
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                if (rs.getString("TABLE_NAME").equals(SQLHasher(username))) {
                    System.out.println("found table match: " + rs.getString("TABLE_NAME"));
                    b = true;
                }
            }
            if (!b) {
                System.out.println("no matching table found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return b;
    }

    public void dropTable(String username) {
        String sql = String.format("DROP TABLE '%s'", username);
        try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            // loop through the result set
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class Record {

    public void listRecords(String username) throws UnsupportedEncodingException {
        String sql = "SELECT * FROM 'FILES-DB'";
        String s = "";
        try ( Connection conn = DriverManager.getConnection(Server.url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t"
                        + rs.getString(2) + "\t"
                        + rs.getString(3) + "\t");
                s = rs.getString(1);
            }
            if (s.equals("")) {
                System.out.println("no files found.");
            }
        } catch (SQLException ex) {
//            ex.printStackTrace();
        }
    }

    public static void insertRecord(String username, File inputFile) throws UnsupportedEncodingException {
        String sql = "INSERT INTO 'FILES-DB' ('user-name', 'file-name', 'file-date', 'file-bytes') VALUES( ?, ?, ?, ?)";

        try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SQLHasher(username));
            pstmt.setString(2, inputFile.getName());
            BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);
            FileTime time = attr.creationTime();
            pstmt.setString(3, time.toString());
            byte[] fileByte = Files.readAllBytes(inputFile.toPath());
            pstmt.setBytes(4, fileByte);
            pstmt.executeUpdate();
        } catch (SQLException e) {
//            System.out.println(e.getMessage());
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
    }

    public void deleteRecord(String username, String fileName) throws UnsupportedEncodingException {
        String sql = String.format("DELETE FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\" AND \"file-name\" = \"%s\"", username, fileName);
        try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeLargeUpdate();
            System.out.println(fileName + " deleted");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
