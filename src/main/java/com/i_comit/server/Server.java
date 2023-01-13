/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package com.i_comit.server;

import static com.i_comit.shared.Hasher.SQLHasher;
import com.i_comit.shared.Miscs;
import com.i_comit.windows.GUI;
import static com.i_comit.windows.Main.masterFolder;
import com.i_comit.windows.Memory;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static String dbPath = Paths.get("").toFile().getAbsolutePath().substring(0, 3)
            + "'--------'"
            + File.separator
            + "runtime"
            + File.separator
            + "bin"
            + File.separator
            + "server"
            + File.separator
            + "i-ncript️.db";
//    private static String dbPath = "D:\\" + masterFolder + "runtime" + File.separator + "bin" + File.separator + "server" + File.separator + "i-ncript️.db";
    private static File dbFile = new File(dbPath);
    private static String url = "jdbc:sqlite:" + dbPath;
    private static boolean serverBool = true;

    public static synchronized void socketStart(Main main) {
        Sessions session = new Sessions();
        Tables table = new Tables();
        Records record = new Records();
        Admin admin = new Admin();

        try {
            InetAddress addr = InetAddress.getByName(Server.getIP());
            serverSocket = new ServerSocket(8665, 100, addr);
            Thread serverConnection = new Thread(() -> {
                while (serverBool) {
                    try {
                        clientSocket = serverSocket.accept();
                        ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                        System.out.println(ois.available());
                        byte[][] message = (byte[][]) ois.readObject();
                        ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

                        if (Arrays.equals(message[0], "GET_USER".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            List<String> fileRecords = findFiles(userName);
                            if (!fileRecords.isEmpty()) {
                                oos.writeObject(fileRecords);
                            }
                        }
                        if (Arrays.equals(message[0], "GET_RECD".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            String fileName = new String(message[2], StandardCharsets.UTF_8);
                            if (record.findRecord(userName, fileName)) {
                                byte[][] b = record.retrieveRecord(userName, fileName);
                                Main.jTextArea1.append("delivered: " + fileName + "\n");
                                Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());

                                oos.writeObject(b);
                                record.deleteRecord(userName, fileName);
                                admin.vacuumDB();
                            } else {
                                byte[][] b = {"NA".getBytes()};
                                oos.writeObject(b);
                            }
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        if (Arrays.equals(message[0], "PST_FILESIZE".getBytes())) {
                            try {
                                ByteBuffer fileSize = ByteBuffer.allocate(Long.BYTES);
                                fileSize.put(message[1]);
                                fileSize.flip();//need flip 
                                boolean b = admin.checkAvailableSpace(fileSize.getLong());
                                oos.writeObject(b);
                                ois.close();
                                oos.close();
                                clientSocket.close();
                            } catch (UnsupportedEncodingException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (Arrays.equals(message[0], "PST_RECD".getBytes())) {
                            try {
                                String userName = new String(message[1], StandardCharsets.UTF_8);
                                String fileName = new String(message[2], StandardCharsets.UTF_8);
                                String fileDate = new String(message[3], StandardCharsets.UTF_8);
                                record.insertRecord(userName, fileName, fileDate, message[4]);
                                Main.jTextArea1.append("received: " + fileName + "\n");
                                Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());

                                ois.close();
                                clientSocket.close();
                            } catch (UnsupportedEncodingException ex) {
//                                ex.printStackTrace();
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
                            try {
                                String userName = new String(message[1], StandardCharsets.UTF_8);
                                String msg = table.createTable(userName);
                                oos.writeObject(msg);
                                ois.close();
                                oos.close();
                                clientSocket.close();
                            } catch (SocketException ex) {
//                                System.out.println("issue with PST_TABL");
                            }
                        }
                        if (Arrays.equals(message[0], "STR_SESN".getBytes())) {
                            String userName = new String(message[1], StandardCharsets.UTF_8);
                            String ipAddress = new String(message[2], StandardCharsets.UTF_8);
                            String OS = new String(message[3], StandardCharsets.UTF_8);
                            boolean b = session.startSession(userName, ipAddress, OS);
                            if (b) {
                                Main.jTextArea1.append(userName + " started their session at " + Miscs.getCurrentTime() + "\n");
                                Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());

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
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }

                        if (Arrays.equals(message[0], "ADMIN".getBytes())) {
                            if (Arrays.equals(message[1], "SERVR".getBytes())) {
                                admin.showServerPanel(main);
                            }
                            if (Arrays.equals(message[1], "CLOSE".getBytes())) {
                                admin.closeSocket();
                            }
                            ois.close();
                            oos.close();
                            clientSocket.close();
                        }
                        Thread.sleep(100);
                    } catch (EOFException ex) {
                    } catch (SocketException ex) {
                    } catch (IOException ex) {
//                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
//                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
                    }
                }
            });
            serverConnection.start();
        } catch (EOFException ex) {

        } catch (IOException ex) {
//            ex.printStackTrace();
        }
    }

    public static void initDatabase() throws UnsupportedEncodingException {
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
        String tbl2 = "CREATE TABLE IF NOT EXISTS 'SESSIONS' ('user-name' text NOT NULL PRIMARY KEY, 'ip-address' text NOT NULL, 'os' text NOT NULL);";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(tbl);
//            stmt.execute(tbl1);
            stmt.execute(tbl2);
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
        return IPv4Addresses.get(IPv4Addresses.size() - 1);
    }

    public static void appKill(String appName, boolean exitAppBool) {
        String listTasks = String.format("tasklist | findStr %s", appName);
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listTasks);
        String s = "";
        try {
            Process sh = pb.start();
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                while ((s = reader.readLine()) != null) {
                    s = reader.readLine().substring(28, 35).trim();
                    System.out.println(s);
                    String killTask = String.format("taskkill /PID %s /F", s);
                    ProcessBuilder pb1 = new ProcessBuilder("cmd.exe", "/c", killTask);
                    pb1.start();
                    if (reader.readLine() == null) {
                        System.out.println("killed server " + s);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            if (exitAppBool) {
                System.exit(0);
            }
        }
    }

    public static void portKill() {
        try {
            String listUSB = String.format("netstat -ano | findStr %d", 8665);
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", listUSB);
            String s = "";
            Process sh = pb.start();
            try ( BufferedReader reader = new BufferedReader(new InputStreamReader(sh.getInputStream()))) {
                s = reader.readLine();
                while (s != null) {
                    String killTask = String.format("taskkill /PID %s /F", s.substring(71));
                    ProcessBuilder pb1 = new ProcessBuilder("cmd.exe", "/c", killTask);
                    pb1.start();
                    break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static class Sessions {

        public void clearSessions() throws UnsupportedEncodingException {
            String sql = "DELETE FROM \"SESSIONS\";";
            try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql);) {
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        private void listSessions(String username) throws UnsupportedEncodingException {
            String sql = String.format("SELECT * FROM \"SESSIONS\" WHERE \"user-name\"  = \"%s\";", SQLHasher(username));
            String str = "";
            try ( Connection conn = DriverManager.getConnection(Server.url);  Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                // loop through the result set
                while (rs.next()) {
                    str = rs.getString(1);
                    if (rs.getString(1).equals(SQLHasher(username))) {
//                    System.out.println();
                        Main.jTextArea1.append(username + " is connected to session\n");
                    }
                }
                if (str.equals("")) {
//                System.out.println("no session for " + username);
                }
            } catch (SQLException | UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        private boolean startSession(String username, String ipAddress, String OS) throws UnsupportedEncodingException {
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
                        pstmt.execute();
                    } catch (SQLException e) {
//                    System.out.println(e.getMessage());
                    }
                }
            } catch (SQLException ex) {
//            ex.printStackTrace();
            }
            return true;
        }

        private void endSession(String username) throws UnsupportedEncodingException {
            String sql = String.format("DELETE FROM \"SESSIONS\" WHERE \"user-name\"  = \"%s\";", SQLHasher(username));
            try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.executeUpdate();
                Main.jTextArea1.append(username + " ended their session at " + Miscs.getCurrentTime() + "\n");
                Main.jTextArea1.setCaretPosition(Main.jTextArea1.getText().length());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    static class Tables {

        Admin admin = new Admin();
        Sessions sessions = new Sessions();

        private String createTable2(String username) throws UnsupportedEncodingException {
            String tbl = String.format("CREATE TABLE IF NOT EXISTS '%s' ('recipient-name' text NOT NULL, 'comm-text' text NOT NULL, 'comm-date' text NOT NULL);", SQLHasher(username));
            try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
                stmt.execute(tbl);
                Main.jLabel6.setText(admin.getServerCap(true) + " per user");
                if (admin.countTables() == 1) {
                    Main.jLabel7.setText(admin.countTables() + " user in network");
                }
                if (admin.countTables() > 1) {
                    Main.jLabel7.setText(admin.countTables() + " users in network");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            return SQLHasher(username);
        }

        private String createTable(String username) throws UnsupportedEncodingException {
            String s = "";
            boolean b = false;
            admin.countTables();
            try ( Connection conn = DriverManager.getConnection(url)) {
                ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
                while (rs.next()) {
                    if (!rs.getString("TABLE_NAME").equals("SESSIONS") && !rs.getString("TABLE_NAME").equals("FILES-DB")) {
                        if (rs.getString("TABLE_NAME").equals(SQLHasher(username))) {
                            System.out.println("found table match: " + rs.getString("TABLE_NAME"));
                            s = createTable2(username);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    if (admin.userCount < 10) {
                        s = createTable2(username);
                    } else {
                        s = "MAX_USER_ERROR";
                        sessions.endSession(username);
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("S " + s);
            return s;
        }

        public boolean listTables(String username) throws UnsupportedEncodingException {
            boolean b = false;
            try ( Connection conn = DriverManager.getConnection(url)) {
                ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
                while (rs.next()) {
                    if (!rs.getString("TABLE_NAME").equals("SESSIONS") && !rs.getString("TABLE_NAME").equals("FILES-DB")) {
                        if (rs.getString("TABLE_NAME").equals(SQLHasher(username))) {
                            System.out.println("found table match: " + rs.getString("TABLE_NAME"));
                            b = true;
                        }
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

        private void dropTable(String username) {
            String sql = String.format("DROP TABLE '%s'", username);
            try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.execute();
                // loop through the result set
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    static class Records {

        Admin admin = new Admin();

        public void listRecords(String username) throws UnsupportedEncodingException {
            String sql = "SELECT * FROM 'FILES-DB'";
            String s = "";
            try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
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

        private boolean findRecord(String username, String fileName) throws UnsupportedEncodingException {
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

        private void insertRecord(String clientUsername, String fileName, String fileDate, byte[] fileBytes) throws UnsupportedEncodingException {
            String sql = "INSERT INTO 'FILES-DB' ('user-name', 'file-name', 'file-date', 'file-bytes') VALUES( ?, ?, ?, ?)";
            try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, clientUsername);
                pstmt.setString(2, fileName);
                pstmt.setString(3, fileDate);
                pstmt.setBytes(4, fileBytes);
                pstmt.execute();
                admin.checkAvailableSpace(0);
            } catch (SQLException e) {
//            System.out.println(e.getMessage());
            }
        }

        private void deleteRecord(String username, String fileName) throws UnsupportedEncodingException {
            String sql = String.format("DELETE FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\" AND \"file-name\" = \"%s\"", username, fileName);
            try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.execute();
                System.out.println(fileName + " deleted");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        private byte[][] retrieveRecord(String clientUsername, String fileName) throws UnsupportedEncodingException {
            String sql = String.format("SELECT * FROM \"FILES-DB\" WHERE \"user-name\" = \"%s\" AND \"file-name\" = \"%s\"", clientUsername, fileName);
            String blob = "";
            try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(sql);
                // loop through the result set
                while (rs.next()) {
                    blob = rs.getString(2);
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
    }

    static class Admin {

        public int userCount = 0;

        public void vacuumDB() {
            String sql = "VACUUM;";
            try ( Connection conn = DriverManager.getConnection(Server.url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.execute();
                checkAvailableSpace(0);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        public int countTables() {
            userCount = 0;
            try ( Connection conn = DriverManager.getConnection(url)) {
                ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
                while (rs.next()) {
                    if (!rs.getString("TABLE_NAME").equals("SESSIONS") && !rs.getString("TABLE_NAME").equals("FILES-DB")) {
                        userCount++;
                    }
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            if (userCount == 0) {
                return 1;
            }
            return userCount;
        }

        public String getServerCap(boolean divideByUserCount) {
            File diskPartition = new File(dbPath).toPath().getRoot().toFile();
            long memCap = diskPartition.getUsableSpace() / 3;
            String GB = "";

            if (!divideByUserCount) {
                GB = Memory.byteFormatter(memCap);
                return GB;
            } else {
                GB = Memory.byteFormatter(memCap / countTables());
                return GB;
            }
        }

        private void showServerPanel(Main main) {
            if (main.getOpacity() == 1) {
                main.setOpacity(0);
            } else {
                main.setOpacity(1);
            }
        }

        public boolean checkAvailableSpace(long fileSizes) {
            File diskPartition = new File(dbPath).toPath().getRoot().toFile();
            long memCap = diskPartition.getUsableSpace() / 3;
            String GB = Memory.byteFormatter(memCap);
            System.out.println("available GB " + GB);
            if (fileSizes < memCap / countTables()) {
                Main.jLabel5.setText(String.format("%.1f", dbFile.length() / 1000000.0) + "/" + GB + " used");
                Main.jLabel6.setText(getServerCap(true) + " per user");
                return true;
            } else {
                return false;
            }
        }

        public void clearServer() {
            long initialDbSize = dbFile.length();
            String sql = String.format("DELETE FROM \"FILES-DB\"");
            try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.execute();
                vacuumDB();
                long finalDbSize = initialDbSize - dbFile.length();
                Main.jTextArea1.append(Memory.byteFormatter(finalDbSize) + "s cleared from server at " + Miscs.getCurrentTime() + "\n");
                GUI.t.interrupt();
                GUI.labelCutterThread(Main.jAlertLabel, "server data cleared.", 0, 20, 1200, false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        public void closeSocket() {
            try {
                portKill();
                appKill(".server.exe", true);
                Server.appKill("i-ncript.exe", true);
                serverBool = false;
                serverSocket.close();
                Client.clientSocket.close();
                System.exit(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
