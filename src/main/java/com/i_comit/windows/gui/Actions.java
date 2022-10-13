/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.i_comit.windows.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPasswordField;

/**
 *
 * @author Khiem Luong <khiemluong@i-comit.com>
 */
public class Actions implements ActionListener {

    public void ActionGUI() {

    }

    public void actionPerformed(ActionEvent e) {
        Main getPass = new Main();

        String str = e.getActionCommand();
        switch (str) {
            case "encrypted":
                System.out.println(0);
                break;
            case "decrypted":
                System.out.println(1);

                break;
            case "enter":
                System.out.println(2);
                break;

            default:
            // code block
        }
        System.out.println("Clicked = " + str);
    }
};
