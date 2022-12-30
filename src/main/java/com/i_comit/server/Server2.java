/*
 * Copyright (C) 2022 Khiem Luong <khiemluong@i-comit.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.i_comit.server;

import static com.i_comit.shared.Hasher.SQLHasher;
import static com.i_comit.windows.Main.masterFolder;
import static com.i_comit.windows.Main.root;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.List;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Server2 {

    private static String dbPath = root + masterFolder + "runtime" + File.separator + "bin" + File.separator + "server" + File.separator + ".💽🗄️.db";

    private static File inputFile = new File("D:\\20221214_201713.jpg");
    private static File dbFile = new File(dbPath);
    private static String url = "jdbc:hsqldb:file:" + dbPath + ";hsqldb.lock_file=false";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try {
            if (!dbFile.exists()) {
                try {
                    Files.createFile(dbFile.toPath());
                    Files.setAttribute(dbFile.toPath(), "dos:hidden", true);

                    System.out.println("creating app lock file");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            initDatabase();
            byte[] inputFileBytes_B = Files.readAllBytes(inputFile.toPath());
            BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.creationTime();
            String fileTimeS = fileTime.toString();
            byte[] inputFileDate_B = fileTimeS.getBytes();
            insertClientRecord("khiemluong", inputFile.getName(), fileTimeS, inputFileBytes_B);
            listRecords("khiemluong");
//            createTable("khiemluong");
            findFiles("khiemluong");
//            getFileBlob("khiemluong");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
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
        String tbl = "CREATE CACHED TABLE IF NOT EXISTS \"FILES-DB\" (\"user-name\" VARCHAR(16) NOT NULL, \"file-name\" VARCHAR(256) NOT NULL PRIMARY KEY, \"file-date\" BLOB NOT NULL, \"file-bytes\" BLOB NOT NULL);";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(tbl);
//            stmt.execute(tbl1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertClientRecord(String clientUsername, String fileName, String fileDate, byte[] fileBytes) throws UnsupportedEncodingException {
        String sql = "INSERT INTO \"FILES-DB\" (\"user-name\", \"file-name\", \"file-date\", \"file-bytes\") VALUES( ?, ?, ?, ?)";
        try ( Connection conn = DriverManager.getConnection(url);  PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, SQLHasher(clientUsername));
            pstmt.setString(2, fileName);
            pstmt.setBytes(3, fileDate.getBytes());
            pstmt.setBytes(4, fileBytes);
            pstmt.executeLargeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void listRecords(String username) throws UnsupportedEncodingException {
        String sql = "SELECT * FROM \"FILES-DB\"";
        String s = "";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t"
                        + rs.getString(2) + "\t"
                        + rs.getBytes(3) + "\t"
                        + rs.getBytes(4) + "\t");
                s = rs.getString(1);
            }
            if (s.equals("")) {
                System.out.println("no files found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static String createTable(String username) throws UnsupportedEncodingException {
        String tbl = String.format("CREATE USER %s PASSWORD '' ADMIN", SQLHasher(username));
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            stmt.execute(tbl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return "";
        }
        return SQLHasher(username);
    }

    public static boolean listTables(String username) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS", SQLHasher(username));
        boolean b = false;
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString(1).equals(SQLHasher(username))) {
                    System.out.println("found table match: " + rs.getString(1));
                    b = true;
                }
            }
            if (!b) {
                System.out.println("no matching table found");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return b;
        }
        return b;
    }

    private static List<String> findFiles(String username) throws UnsupportedEncodingException {
        List<String> fileRecords = new ArrayList<>();
        String sql = String.format("SELECT * FROM \"FILES-DB\" WHERE \"user-name\" = '%s'", SQLHasher(username));
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("user-name"));
                fileRecords.add(rs.getString(2));
            }
            if (fileRecords.isEmpty()) {
                System.out.println("No records found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

//        } catch (IOException ex) {
//
        }
        System.out.println(fileRecords);
        return fileRecords;
    }

    private static byte[][] getFileBlob(String clientUsername) throws UnsupportedEncodingException {
        String sql = String.format("SELECT * FROM \"FILES-DB\"", clientUsername);
        String blob = "";
        try ( Connection conn = DriverManager.getConnection(url);  Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            // loop through the result set
            while (rs.next()) {
                blob = rs.getString(2);
                System.out.println(rs.getString(1) + "\t"
                        + rs.getString(2) + "\t");
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
