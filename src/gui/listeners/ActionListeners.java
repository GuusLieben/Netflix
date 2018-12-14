/*
 * Copyright © 2018. Guus Lieben.
 * All rights reserved.
 */

package com.netflix.gui.listeners;

import com.netflix.Netflix;
import com.netflix.commons.Commons;
import com.netflix.gui.NetflixGUI;
import com.netflix.gui.panes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;

public class ActionListeners {

  private static String usernameBoxValue;

  public static void mouseEventUnderline(JButton button) {
    // MouseOver effects for the menu (underline and cursor effect)
    HashMap<TextAttribute, Object> textAttrMap = new HashMap<>();

    // Listens for mouse events
    button.addMouseListener(
        new MouseAdapter() {
          // If you hover it ...
          @Override
          public void mouseEntered(MouseEvent evt) {
            // Underline the text...
            textAttrMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
            button.setFont(button.getFont().deriveFont(textAttrMap));
            // And change the cursor
            Cursor hoverCursor = new Cursor(Cursor.HAND_CURSOR);
            button.setCursor(hoverCursor);
          }

          // If you are no longer hovering it ...
          @Override
          public void mouseExited(MouseEvent evt) {
            // Remove the underline effect ...
            textAttrMap.put(TextAttribute.UNDERLINE, null);
            button.setFont(button.getFont().deriveFont(textAttrMap));
            // And reset the cursor ...
            Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
            button.setCursor(normalCursor);
          }
        });
  }

  public static void loginClickEvent(JButton login, String passwordMD5) {
    login.addActionListener(
        (ActionEvent e) ->
            Commons.users.forEach(
                (key, value) -> { // Loop through the users
                  // and check if they match the input
                  if (usernameBoxValue.equals(key) && Commons.hashMD5(passwordMD5).equals(value)) {
                    Commons.logger.info(
                        "Authorized login '"
                            + usernameBoxValue
                            + "', '"
                            + Commons.hashMD5(passwordMD5)
                            + "'");
                    // Clear the mainPanel (removing login panel), set loggedIn status to true and
                    // load the media panels
                    Overview.clearPane(NetflixGUI.mainPanel);
                    NetflixGUI.loggedIn = true;
                    NetflixGUI.loadPanels();

                  } else {
                    // If it doesn't match, show an error
                    JOptionPane.showMessageDialog(
                        NetflixGUI.frame,
                        "Incorrect credentials, please try again",
                        null,
                        JOptionPane.ERROR_MESSAGE);
                  }
                }));
  }

  public static void simulateClickOnEnter(JPasswordField textField, JButton button) {
    textField.addKeyListener(
        new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) button.doClick();
          }

          public void keyPressed(KeyEvent e) {
            // Ignored
          }

          public void keyReleased(KeyEvent e) {
            // Ignored
          }
        });
  }

  public static void onEmptyFieldSet(JTextField textField, String s) {
    textField.addKeyListener(
        new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            // If the text is "" then add out placeholder
            if (textField.getText().equals("")) textField.setText(s);
          }

          public void keyPressed(KeyEvent e) {
            // Ignored
          }

          @Override
          public void keyReleased(KeyEvent e) {
            usernameBoxValue = textField.getText();
          }
        });
  }

  public static void switchRegisterPane(JButton register) {
    register.addActionListener(
        (ActionEvent e) -> {
          Overview.clearPane(NetflixGUI.mainPanel);
          NetflixGUI.mainPanel.add(AccountRegister.registerPanel(NetflixGUI.frame));
        });
  }

  public static void switchPaneOnClick(JButton button, String pane) {
    // Use lambda to handle button pressing to switch panes
    button.addActionListener(
        (ActionEvent e) -> {
          Overview.clearPane(NetflixGUI.lpane);

          switch (pane) {
            case "Series":
              NetflixGUI.lpane.add(Series.pane());
              break;
            case "Films":
              NetflixGUI.lpane.add(Films.pane());
              break;
            case "Account":
              NetflixGUI.lpane.add(AccountView.pane());
              break;
            default:
              break;
          }
        });
  }

  public static void logoutClickEvent(JButton logoutButton) {
    logoutButton.addActionListener(
        (ActionEvent e) -> {
          Overview.clearPane(NetflixGUI.mainPanel);
          NetflixGUI.loggedIn = false;
          NetflixGUI.usernameBox.setText("Username...");
          NetflixGUI.passwordBox.setText("");
          usernameBoxValue = "";

          Netflix.gui.setFrame(Netflix.width, Netflix.height);
        });
  }
}
