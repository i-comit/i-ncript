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

import static com.i_comit.server.Client.getServerSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Admin {

    private static Socket clientSocket;
    private static ObjectOutputStream oos = null;
    private static ObjectInputStream ois = null;

    private static byte[] adminReq_B = "ADMIN".getBytes();

    public static void showHidePanel(int requestType) {
        try {
            getServerSocket();
            if (requestType == 0) {
                byte[] serverReq_B = "SERVR".getBytes();
                byte[][] startSession_B = {adminReq_B, serverReq_B};
                oos.writeObject(startSession_B);
            }
            oos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
        public static void closeSocket(int requestType) {
        try {
            getServerSocket();
            if (requestType == 0) {
                byte[] serverReq_B = "CLOSE".getBytes();
                byte[][] startSession_B = {adminReq_B, serverReq_B};
                oos.writeObject(startSession_B);
            }
            oos.close();
            Client.clientSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
