package com.netflix.gui.views;

import com.netflix.commons.Commons;
import com.netflix.entities.Account;
import com.netflix.entities.Profile;
import com.netflix.gui.NetflixFrame;
import com.netflix.gui.commons.Common;
import com.netflix.gui.commons.GradientPanel;
import com.netflix.gui.commons.NButton;
import com.netflix.handles.SQLResults;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;

import static com.netflix.Netflix.database;

public class createProfileView {

  private static JTextField nameField;
  private static JXDatePicker datePicker;
  private static GridBagConstraints constraints = new GridBagConstraints();

  public static JPanel panel() {
    JPanel main = new JPanel(new BorderLayout());

    JPanel profileWrapper = new GradientPanel().getGradientPanel();
    profileWrapper.setLayout(new GridBagLayout());
    profileWrapper.setBackground(new Color(34, 34, 34));

    constraints.gridy = 0;

    JLabel nameLabel = new JLabel("Naam");
    nameField = new JTextField(20);

    JLabel ageLabel = new JLabel("Leeftijd");
    datePicker = new JXDatePicker();
    datePicker.setDate(Calendar.getInstance().getTime());
    datePicker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));

    nameField.setBackground(new Color(20, 20, 20));

    nameField.setCaretColor(Color.LIGHT_GRAY);

    JPanel spacer = new JPanel();
    spacer.setOpaque(false);

    JLabel header = new JLabel("Nieuw profiel");
    header.setFont(new Font(header.getFont().getName(), header.getFont().getStyle(), 18));
    header.setBorder(new EmptyBorder(0, 0, 10, 0));

    JButton addProfile = new NButton("Toevoegen");

    addComponent(header, profileWrapper);
    addComponent(nameLabel, profileWrapper);
    addComponent(nameField, profileWrapper);
    addComponent(spacer, profileWrapper);
    addComponent(ageLabel, profileWrapper);
    addComponent(datePicker, profileWrapper);
    addComponent(spacer, profileWrapper);
    addComponent(addProfile, profileWrapper);

    addLoginEvent(addProfile);

    // Add all the things
    main.add(Common.logo(), BorderLayout.NORTH);
    main.add(profileWrapper, BorderLayout.CENTER);
    main.add(Common.bottomPane(), BorderLayout.SOUTH);

    return main;
  }

  private static void addComponent(JComponent com, JPanel panel) {
    constraints.gridy++;
    com.setForeground(Color.LIGHT_GRAY);
    com.setBorder(new EmptyBorder(8, 8, 8, 8));
    panel.add(com, constraints);
  }

  private static void addLoginEvent(JButton button) {
    button.addActionListener(
        (ActionEvent e) -> {
          String name = nameField.getText();
          java.util.Date birthday = datePicker.getDate();
          java.sql.Date birthdate = new java.sql.Date(birthday.getTime());

          if (birthday.before(
              Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)))) {
            Account.currentAccount.addProfile(
                new Profile(
                    Account.currentAccount,
                    name,
                    birthdate,
                    Account.currentAccount.getProfiles().size() + 1));
            String qr =
                "INSERT INTO Users (AccountId, Name, EpisodesWatched, FilmsWatched, Birthday) VALUES (?, ?, ?, ?, ?)";

            Object[] arr = {Account.currentAccount.databaseId, name, 0, 0, birthday};

            if (database.executeSqlNoResult(qr, arr) == SQLResults.PASS)
              JOptionPane.showMessageDialog(NetflixFrame.frame, "Succes!");

            Commons.clearPane(NetflixFrame.mainPanel);
            NetflixFrame.mainPanel.add(LoginView.ProfileLogin.profileSelection());
          }
        });
  }
}
