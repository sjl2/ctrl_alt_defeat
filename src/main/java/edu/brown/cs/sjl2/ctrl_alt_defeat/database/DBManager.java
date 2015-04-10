package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;

public class DBManager {
  private Connection conn;
  
  /**
   * Constructor for DBManager class, sets up connection.
   * @param path - String representing path to db file
   */
  public DBManager(String path) {
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:" + path);
      Statement stat = conn.createStatement();
      stat.executeUpdate("PRAGMA foreign_keys = ON;");
      stat.close();
    } catch (SQLException | ClassNotFoundException e) {
      close();
      throw new RuntimeException(e);
    }
  }
  
  /**
   * Call any time there is an error or you are done with the DBManager.
   */
  public void close() {
    try {
      if (conn != null) {
        conn.close(); 
      }
    } catch (SQLException e) {
      throw new RuntimeException("ERROR: Unable to close connection.");
    }
  }
  
  /**
   * Generates new PlayID's, always one greater than the existing max id.
   * @return int, new play ID to be used.
   */
  public int generatePlayID() {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT Max(id) FROM plays;")) {

      ResultSet rs = prep.executeQuery();
      if (rs.next()) {
        int maxID = rs.getInt(1);
        assert (!rs.next());
        return maxID++;
      } else {
        return 0;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  // CHECK SAVING VS OVERWRITING

  /**
   * Saves the inputted play name and data into the database.
   * @param name - String, name of play.
   * @param play - Location[][], representing path data of all players.
   */
  public void savePlay(Play play) {
    int id;
    if (play.isNewPlay()) {
      id = generatePlayID();
      saveToPlaysTable(id, play.getName(), play.getNumFrames());
    } else {
      id = play.getID();
    }

    Location[][] paths = play.getPaths();

    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;

    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO play_details VALUES(?, ?, ?, ?, ?);")) {

      // Loops through entire play, each location[] represents a given
      // player's path, each entry in the location[] represents a frame
      for (int position = 0; position < length; position++) {
        for (int frame = 0; frame < paths[position].length; frame++) {
          Location l = paths[position][frame];
          prep.setInt(1, id);
          prep.setString(2, bballPositions[position].getName());
          prep.setInt(3, frame);
          prep.setInt(4, l.getX());
          prep.setInt(5, l.getY());
          prep.addBatch();
        }
      }

      prep.executeBatch();
      

    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
  
  private void saveToPlaysTable(int id, String name, int numFrames) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO plays VALUES(?, ?, ?);")) {

      prep.setInt(1, id);
      prep.setString(2, name);
      prep.setInt(3, numFrames);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Fetches all the data associated with a single play to send
   * to the front end.
   * @param id - Int, corresponding to play front end is requesting
   * @return Play, with all fields set.
   */
  public Play loadPlay(int id) {
    Play play = getPlayMetaData(id);
    int numFrames = play.getNumFrames();
    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;
    Location[][] paths = new Location[length][];

    for (int i = 0; i < length; i++) {
      try (PreparedStatement prep = conn.prepareStatement(
          "SELECT frame, x, y FROM play_details WHERE play = ? AND position = ?;")) {
        
        prep.setInt(1, id);
        prep.setString(2, bballPositions[i].getName());
        ResultSet rs = prep.executeQuery();
        
        Location[] path = new Location[numFrames];
        while (rs.next()) {
          Location loc = new Location(rs.getInt("x"), rs.getInt("y"));
          path[rs.getInt("frame")] = loc;
        }

        paths[i] = path;
      } catch (SQLException e) {
        close();
        throw new RuntimeException(e);
      }
    }

    play.setPaths(paths);
    return play;
  }
  
  private Play getPlayMetaData(int id) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name, numFrames FROM plays WHERE id = ?")) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString("name");
        int numFrames = rs.getInt("numFrames");
        Play toReturn = new Play(id, name, numFrames);
        assert (!rs.next());
        return toReturn;
      } else {
        throw new RuntimeException("ERROR: Play not found.");
      }

    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
}
