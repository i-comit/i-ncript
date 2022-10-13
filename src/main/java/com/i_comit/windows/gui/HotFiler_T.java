/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import java.io.IOException;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */

public class HotFiler_T implements Runnable{

    public int threadIterator;

    public void run() {

        try {
            HotFiler.folderWatcher();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
