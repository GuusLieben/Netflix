package com.netflix.commons;

import java.sql.*;

public class SqlConnection {

  private Connection connection = null;

  public boolean connectDatabase(String connectionUrl) {
    try {
      // Use MS Sql server
      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      // Use the connectionUrl to connect (jdbc connection string)
      connection = DriverManager.getConnection(connectionUrl);
      return true;

    } catch (ClassNotFoundException | SQLException e) {
      Commons.exception(e);
      connection = null;
      return false;
    }
  }

  public void disconnectDatabase() {
    // Check if it isn't already disconnected
    if (connection != null)
      try {
        connection.close();
      } catch (SQLException e) {
        Commons.exception(e);
      }
    // Set connection to null, if it's already disconnected it'd be the same anyway
    connection = null;
  }

  public ResultSet executeSql(String sqlQuery) {
    ResultSet results = null;
    try (Statement statement = this.connection.createStatement()) {
      // Make sure the results are passed
      results = statement.executeQuery(sqlQuery);
    } catch (SQLException ex) {
      Commons.exception(ex);
    }
    return results;
  }

  public boolean executeSqlNoResult(String sqlQuery) {
    // Return true if the query succeeded, even if it has no resultset
    try (Statement statement = this.connection.createStatement()) {
      return statement.execute(sqlQuery);
    } catch (Exception ex) {
      Commons.exception(ex);
    }
    return false;
  }
}
