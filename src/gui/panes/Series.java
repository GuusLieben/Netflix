package com.netflix.gui.panes;

import com.netflix.objects.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import static java.awt.BorderLayout.*;

public class Series {
  private static String title = "House of Cards";
  private static int episodes = 73;
  private static String[] choices = {
    "House of Cards",
    "Daredevil",
    "Stranger Things",
    "Orange Is the New Black",
    "Narcos",
    "The Crown"
  };

  public static JPanel pane() {
    // Create panel with 10px padding
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    mainPanel.setBackground(Color.WHITE);

    Serie serie =
        new Serie(new Genre("Drama"), new Language("nl_NL", "Dutch"), "House of Cards", 8.6);
    Season season = new Season(serie, "newSeason", 1, 1);
    Episode episode = new Episode(season, "Pilot", serie, 16.57);
    Episode episode2 = new Episode(season, "Pilot Continued", serie, 12.35);

    // Add sub-panels
    mainPanel.add(selectSeries(), NORTH);
    mainPanel.add(Overview.newOverview(null, serie), CENTER);

    return mainPanel;
  }

  static JPanel selectSeries() {
    // Create dropdown with sample values
    JPanel selectSeries = new JPanel();
    JLabel selectSerie = new JLabel("Selecteer een serie : ");
    JComboBox<String> comboBox = new JComboBox<>(choices);
    comboBox.setVisible(true);

    // Add label + dropdown
    selectSeries.add(selectSerie);
    selectSeries.add(comboBox);

    selectSeries.setBackground(Color.WHITE);

    return selectSeries;
  }
}