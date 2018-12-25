package com.netflix.gui.views;

import com.netflix.entities.Film;
import com.netflix.entities.Serie;

import javax.swing.*;

@SuppressWarnings("deprecation")
public class SerieMediaView extends MediaView {

  public static JPanel pane() {
      // Set the super.combobox to use serie titles
    comboBox = new JComboBox<>(Serie.serieTitles.toArray());
    // Make sure we are using series
    type = "serie";
    mediaType = 2;
    // Return the panel in the super-class
    return MediaView.panel();
  }
}