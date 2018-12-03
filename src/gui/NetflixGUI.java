package com.netflix.gui;

import com.netflix.gui.panes.*;
import com.raphaellevy.fullscreen.*;

import javax.swing.*;
import java.awt.*;

import static com.netflix.commons.Commons.*;
import static java.awt.BorderLayout.*;
import static javax.swing.JFrame.*;

public class NetflixGUI {

  public static JFrame frame;
  private static String currentPanel = "Series";
  private static JPanel lpane = new JPanel(new BorderLayout());
  private static int layer = 1;

  public NetflixGUI(int width, int height) {
    frame = new JFrame();
    setFrame(width, height);
  }

  public static void showOnClick(JButton button, String pane, JLabel label) {
    button.addActionListener(
        e -> {
          lpane.removeAll();
          lpane.repaint();
          lpane.revalidate();

          if ((pane.equals("Series"))) lpane.add(Series.pane());
          if ((pane.equals("Films"))) lpane.add(Films.pane());
          if ((pane.equals("Account"))) lpane.add(Account.pane());

          currentPanel = pane;
          layer++;

          label.setText("Overzicht : " + pane);
        });
  }

  private void setFrame(int width, int height) {
    // Set defaults for frame
    frame.setLayout(new BorderLayout());
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Make sure the given sizes don't exceed the minimum frame size
    if (width < 650) width = 650;
    if (height < 500) height = 500;

    // Set sizes for frame
    frame.setMinimumSize(new Dimension(650, 400));
    frame.setSize(width, height);

    // Add to LayeredPane
    lpane.add(Series.pane());

    // Add all panes
    frame.add(Common.bottomPane(), SOUTH);
    frame.add(Common.menu(), NORTH);
    frame.add(lpane, CENTER);

    // Make sure the application can be used full-screen on MacOS devices
    try {
      if (System.getProperty("os.name").startsWith("Mac"))
        FullScreenMacOS.setFullScreenEnabled(frame, true);
    } catch (FullScreenException ex) {
      exception(ex);
    }

    // Make the frame visible
    frame.setVisible(true);
  }
}
