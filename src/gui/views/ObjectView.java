package com.netflix.gui.views;

import com.netflix.*;
import com.netflix.commons.*;
import com.netflix.entities.*;
import com.netflix.entities.abstracts.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import static java.awt.BorderLayout.*;

public class ObjectView {

  public static Serie serie;
  // Default panels
  private static JPanel main = new JPanel(new BorderLayout());
  private static JPanel inner = new JPanel(new BorderLayout());
  private static JPanel aboutMediaInner = new JPanel(new BorderLayout());
  private static JPanel overviewPanel = new JPanel(new BorderLayout());
  // Common stuff
  private JLabel title;
  private String description;
  public static JTable table;
  public JLabel descriptionLabel;
  public MediaObject obj;

  public ObjectView() {}

  private ObjectView(MediaObject object) {
    obj = object;
    title = new JLabel(object.getTitle());

    // If it's a serie
    if (object.getType() == 2) {
      Serie serie = Serie.getSerieByName(object.getTitle());
      description =
          String.format(
              "<html>Taal : %s<br>Genre : %s<br>Seizoenen : %d<br>Afleveringen : %d<br>Leeftijdsclassificatie %s<br>Bekeken door %s%% van het totaal aantal gebruikers<br><br>Vergelijkbaar : %s (%s%%)</html>",
              object.getLang().getLanguageName(),
              object.getGenre(),
              serie.getSeasonCount(),
              serie.getEpisodeCount(),
              object.getRating(),
              Commons.percentage(object.getWatchedPercentage()),
              serie.getSimilarObject().getTitle(),
              serie.getSimilarObject().getWatchedPercentage());
    }

    // If it's a film
    if (object.getType() == 1) {
      Film film = Film.getFilmByName(object.getTitle());
      description =
          String.format(
              "<html>Genre : %s<br>Taal : %s<br>Leeftijdsclassificatie : %s<br>Regisseur : %s<br>Tijdsduur : %s<br>Bekeken door %s%% van het totaal aantal gebruikers<br><br>Vergelijkbaar : %s (%s%%)</html>",
              object.getGenre(),
              object.getLang().getLanguageName(),
              object.getRating(),
              film.getDirector(),
              film.getDuration(),
              Commons.percentage(object.getWatchedPercentage()),
              film.getSimilarObject().getTitle(),
              film.getSimilarObject().getWatchedPercentage());
    }
  }

  // Clear the overview on demand
  void clearOverview() {
    Commons.clearPane(overviewPanel);
  }

  JPanel getOverview(MediaObject media) {

    // Add sub-panels
    ObjectView objectView = null;

    switch (media.getType()) {
      case 2: // Serie
        ObjectView.serie = (Serie) media;
        break;
      case 1: // Film, make sure it doesn't check for episodes
        ObjectView.serie = null;
        break;
      default:
        Commons.exception(new Exception("Could not collect series/films"));
        break;
    }

    objectView = new ObjectView(media);

    overviewPanel.add(objectView.getPanel());
    overviewPanel.setBackground(Color.WHITE);

    return overviewPanel;
  }

  private JPanel getPanel() {
    Commons.clearPane(main);
    Commons.clearPane(inner);
    Commons.clearPane(aboutMediaInner);

    title.setFont(new Font(title.getFont().getFontName(), Font.BOLD, 18));

    descriptionLabel = new JLabel();
    descriptionLabel.setText(description);
    descriptionLabel.setFont(
        new Font(
            descriptionLabel.getFont().getFontName(),
            Font.PLAIN,
            descriptionLabel.getFont().getSize()));

    JPanel quickView = new JPanel(new BorderLayout());

    // Set background colors
    inner.setBackground(Color.WHITE);
    descriptionLabel.setBackground(Color.WHITE);
    aboutMediaInner.setBackground(Color.WHITE);
    title.setBackground(Color.WHITE);
    quickView.setBackground(Color.WHITE);
    main.setBackground(Color.WHITE);

    // Add all the things!
    aboutMediaInner.add(descriptionLabel, CENTER);

    if (obj.type == 1) {
      JCheckBox cb = new JCheckBox("Bekeken");

      if (Profile.currentUser.getFilmsWatched().contains(obj)) cb.setSelected(true);

      cb.addItemListener(
          e -> {
            if (cb.isSelected()) {
              Profile.currentUser.viewFilmNoDB(Film.getByDbId(obj.databaseId));
              String qr =
                  "INSERT INTO WatchedFilms (FilmId, UserId, FilmsWatched) VALUES (?, ?, ?)";
              Object[] arr = {
                obj.databaseId,
                Profile.currentUser.databaseId,
                Profile.currentUser.getFilmsWatched().size()
              };

              Netflix.database.executeSqlNoResult(qr, arr);

            } else {
              Profile.currentUser.unviewFilm(Film.getByDbId(obj.databaseId));
              String qr = "DELETE FROM WatchedFilms WHERE UserId=? AND FilmId=?";
              Object[] arr = {Profile.currentUser.databaseId, obj.databaseId};

              Netflix.database.executeSqlNoResult(qr, arr);
            }

            // If it's a film
            Film film = Film.getFilmByName(obj.getTitle());
            descriptionLabel.setText(
                String.format(
                    "<html>Genre : %s<br>Taal : %s<br>Leeftijdsclassificatie : %s<br>Regisseur : %s<br>Tijdsduur : %s<br>Bekeken door %s%% van het totaal aantal gebruikers<br><br>Vergelijkbaar : %s (%s%%)</html>",
                    obj.getGenre(),
                    obj.getLang().getLanguageName(),
                    obj.getRating(),
                    film.getDirector(),
                    film.getDuration(),
                    Commons.percentage(obj.getWatchedPercentage()),
                    film.getSimilarObject().getTitle(),
                    film.getSimilarObject().getWatchedPercentage()));
          });
      aboutMediaInner.add(cb, BorderLayout.SOUTH);
    }

    quickView.add(title, NORTH);
    quickView.add(descriptionLabel, SOUTH);
    inner.add(quickView, NORTH);

    if (ObjectView.serie != null) { // If it's a film this will be null
      // Generate a table
      table =
          new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
              return column == 4;
            }
          };

      DefaultTableModel tableModel = new DefaultTableModel(0, 0);
      CheckBoxModelListener cml = new CheckBoxModelListener();
      cml.lable = descriptionLabel;
      cml.obj = obj;
      tableModel.addTableModelListener(cml);

      // Table headers
      String[] columnNames = {"Aflevering", "Titel", "Seizoen", "Duratie", "Bekeken", "DatabaseId"};

      tableModel.setColumnIdentifiers(columnNames);
      table.setModel(tableModel);

      TableColumnModel tcm = table.getColumnModel();
      tcm.removeColumn(
          tcm.getColumn(5)); // Hide DatabaseId from user, but store the data in the table

      // Add all episodes in the serie
      for (Season season : ObjectView.serie.getSeasons()) {
        for (Episode episode : season.getEpisodes()) {
          tableModel.addRow(
              new Object[] {
                episode.getEpisodeNumber(),
                episode.getTitle(),
                episode.getSeason(),
                episode.getDuration(),
                episode.watchedByProfile(),
                episode.databaseId
              });
        }
      }

      TableColumn tc = table.getColumnModel().getColumn(4);
      tc.setCellEditor(table.getDefaultEditor(Boolean.class));
      tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));

      JTableHeader header = table.getTableHeader();
      header.setForeground(new Color(151, 2, 4));
      header.setFont(new Font(header.getFont().getName(), Font.BOLD, 12));
      header.setOpaque(false);

      table.setShowGrid(true);
      table.setGridColor(Color.LIGHT_GRAY);

      TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
      table.setRowSorter(sorter);

      ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
      sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING)); // First sort it by season
      sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING)); // Then sort it by episode
      sorter.setSortKeys(sortKeys);

      // Make it scrollable
      JScrollPane tableScroll =
          new JScrollPane(
              table,
              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

      tableScroll.setPreferredSize(new Dimension(inner.getWidth(), main.getHeight() / 2));

      JPanel episodes = new JPanel(new BorderLayout());

      episodes.addComponentListener(new ResizeListener(tableScroll));

      episodes.setOpaque(false);
      episodes.add(header, BorderLayout.NORTH);
      episodes.add(tableScroll, BorderLayout.CENTER);

      inner.add(episodes, SOUTH);
    }

    inner.add(aboutMediaInner, CENTER);
    main.add(inner);

    return main;
  }

  // Resize the scrollpane with the inner panel to make it easily adjustable
  class ResizeListener extends ComponentAdapter {
    private JScrollPane pane;

    ResizeListener(JScrollPane pane) {
      this.pane = pane;
    }

    public void componentResized(ComponentEvent e) {
      pane.setPreferredSize(new Dimension(inner.getWidth(), main.getHeight() / 2));
    }
  }

  public class CheckBoxModelListener implements TableModelListener {
    JLabel lable;
    MediaObject obj;

    @Override
    public void tableChanged(TableModelEvent e) {
      int row = e.getFirstRow();
      int column = e.getColumn();

      episodeTableUpdate(row, column, e); // Series
    }

    private void episodeTableUpdate(int row, int column, TableModelEvent e) {
      if (column == 4) {
        TableModel model = (TableModel) e.getSource();
        Boolean checked = (Boolean) model.getValueAt(row, column);
        int dbID = (int) ObjectView.table.getModel().getValueAt(row, column + 1);
        if (checked) {
          Profile.currentUser.viewEpisodeNoDB(Episode.getByDbId(dbID));

          String qr =
              "INSERT INTO WatchedEpisodes (EpisodesWatched, UserId, EpisodeId) VALUES (?, ?, ?)";
          Object[] arr = {
            Profile.currentUser.getEpisodesWatched().size(), Profile.currentUser.databaseId, dbID
          };

          Netflix.database.executeSqlNoResult(qr, arr);

        } else {
          Profile.currentUser.unviewEpisode(Episode.getByDbId(dbID));

          String qr = "DELETE FROM WatchedEpisodes WHERE UserId=? AND EpisodeId=?";
          Object[] arr = {Profile.currentUser.databaseId, dbID};

          Netflix.database.executeSqlNoResult(qr, arr);
        }

        Serie serie = Serie.getSerieByName(obj.getTitle());
        lable.setText(
            String.format(
                "<html>Taal : %s<br>Genre : %s<br>Seizoenen : %d<br>Afleveringen : %d<br>Leeftijdsclassificatie %s<br>Bekeken door %s%% van het totaal aantal gebruikers<br><br>Vergelijkbaar : %s (%s%%)</html>",
                obj.getLang().getLanguageName(),
                obj.getGenre(),
                serie.getSeasonCount(),
                serie.getEpisodeCount(),
                obj.getRating(),
                Commons.percentage(obj.getWatchedPercentage()),
                serie.getSimilarObject().getTitle(),
                serie.getSimilarObject().getWatchedPercentage()));
      }
    }
  }
}