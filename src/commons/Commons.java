package com.netflix.commons;

import java.awt.Container;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static javax.xml.bind.DatatypeConverter.*;

public class Commons {

  // Instantiate a logger for the entire project
  public static final Logger logger = Logger.getLogger("Netflix");

  // This will always execute
  static {
    try {
      // Get the date and time in format d.[date]-t.[time]
      String dateTime =
          String.format("d.%s-t.%s", LocalDate.now().toString(), LocalTime.now().toString())
              .replace(":", "-")
              .replace(" ", ".");

      // Set the log file format to log-[dateTime].log, always append what is logged rather than
      // replacing existing content
      FileHandler handler = new FileHandler(String.format("logs/log-%s.xml", dateTime), true);

      // Add file handle to logger
      Commons.logger.addHandler(handler);
    } catch (IOException e) {
      Commons.exception(e);
    }
  }

  public static String percentage(double num) {
    return new DecimalFormat("0.00").format(num);
  }

  // Exception handle
  public static void exception(Exception ex) {
    StringBuilder stack = new StringBuilder();
    for (StackTraceElement el : ex.getStackTrace()) stack.append(el.toString()).append("\n");
    // Print the suspected cause
    logger.severe(
        String.format(
            "%s%nSuspected cause : %s%n%s", ex.getMessage(), ex.getCause(), stack.toString()));
  }

  public static String hashSHA256(String password) {
    try {
      // Set digest to use SHA-256
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      // Set digest to convert given password
      md.update(password.getBytes());

      // Return it as a string
      return printHexBinary(md.digest()).toLowerCase();
    } catch (NoSuchAlgorithmException ex) {
      // In case SHA-256 is somehow not found
      Commons.exception(ex);
    }
    return null;
  }

  public static void clearPane(Container con) {
    // Clears a container
    con.removeAll();
    con.repaint();
    con.revalidate();
  }
}
