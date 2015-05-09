package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;

public class PlaymakerDB {

  private Connection conn;

  /**
   * Constructor for playmakerDB class.
   * 
   * @param conn - Connection, comes from DBManager
   */
  public PlaymakerDB(Connection conn) {
    this.conn = conn;
  }

  /**
   * Call any time there is an error.
   * 
   * @author awainger
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
   * Saves the inputted play name and data into the database.
   * 
   * @param play - Play, with name, frames and paths set from front end.
   * @author awainger
   */
  public void savePlay(Play play) {
    String name = play.getName();
    int numFrames = play.getNumFrames();
    Location[][] playerPaths = play.getPlayerPaths();
    int[] ballPath = play.getBallPath();

    if (!doesPlayExist(name)) {
      saveToPlaysTable(name, play.getNumFrames());
    }

    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;

    try (PreparedStatement prep1 = conn.prepareStatement(
        "DELETE FROM play_detail WHERE play = ?;");
        PreparedStatement prep2 = conn.prepareStatement(
            "INSERT INTO play_detail VALUES(?, ?, ?, ?, ?);");
        PreparedStatement prep3 = conn.prepareStatement(
            "DELETE FROM play_detail_ball WHERE play = ?;");
        PreparedStatement prep4 = conn.prepareStatement(
            "INSERT into play_detail_ball VALUES(?, ?, ?);")) {

      prep1.setString(1, name);
      prep1.executeUpdate();
      prep3.setString(1, name);
      prep3.executeUpdate();

      conn.setAutoCommit(false);

      // Loops through entire play, each location[] represents a given
      // player's path, each entry in the location[] represents a frame
      for (int position = 0; position < length; position++) {
        for (int frame = 0; frame < numFrames; frame++) {
          Location l = playerPaths[position][frame];
          prep2.setString(1, name);
          prep2.setString(2, bballPositions[position].getName());
          prep2.setInt(DBManager.THREE, frame);
          prep2.setDouble(DBManager.FOUR, l.getX());
          prep2.setDouble(DBManager.FIVE, l.getY());
          prep2.addBatch();
        }
      }
      prep2.executeBatch();

      // Adding ball
      for (int frame = 0; frame < numFrames; frame++) {
        prep4.setString(1, name);
        prep4.setInt(2, frame);
        prep4.setInt(3, ballPath[frame]);
        prep4.addBatch();
      }

      prep4.executeBatch();
      conn.commit();
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  private void saveToPlaysTable(String name, int numFrames) {
    try (PreparedStatement prep = conn
        .prepareStatement("INSERT INTO play VALUES(?, ?);")) {

      prep.setString(1, name);
      prep.setInt(2, numFrames);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  private boolean doesPlayExist(String name) {
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT name FROM play WHERE name = ? LIMIT 1;")) {
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Fetches all the data associated with a single play to send to the front
   * end.
   * 
   * @param name - String, corresponding to play front end is requesting
   * @return Play, with all fields set.
   * @author awainger
   */
  public Play loadPlay(String name) {
    Play play = loadPlayMetaData(name);
    int numFrames = play.getNumFrames();
    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;
    Location[][] paths = new Location[length][];

    for (int i = 0; i < length; i++) {
      try (PreparedStatement prep = conn.prepareStatement("SELECT frame, x, y"
          + " FROM play_detail WHERE play = ? AND position = ?;")) {

        prep.setString(1, name);
        prep.setString(2, bballPositions[i].getName());
        ResultSet rs = prep.executeQuery();

        Location[] path = new Location[numFrames];
        while (rs.next()) {
          Location loc = new Location(rs.getDouble("x"), rs.getDouble("y"));
          path[rs.getInt("frame")] = loc;
        }

        paths[i] = path;
      } catch (SQLException e) {
        close();
        throw new RuntimeException(e);
      }
    }

    int[] ballPath = loadBallPath(name, numFrames);
    play.setPlayerPaths(paths);
    play.setBallPath(ballPath);
    return play;
  }

  private int[] loadBallPath(String name, int numFrames) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT frame, player_index FROM play_detail_ball WHERE play = ?;")) {

      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();

      int[] ballPath = new int[numFrames];
      while (rs.next()) {
        ballPath[rs.getInt("frame")] = rs.getInt("player_index");
      }

      return ballPath;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  private Play loadPlayMetaData(String name) {
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT numFrames FROM play WHERE name = ?;")) {
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        int numFrames = rs.getInt("numFrames");
        Play toReturn = new Play(name, numFrames);
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

  /**
   * Loads play names to pass to front end
   *
   * @return List strings, play names
   * @author awainger
   */
  public List<String> loadPlayNames() {
    try (PreparedStatement prep = conn
        .prepareStatement("SELECT name FROM play;")) {
      List<String> plays = new ArrayList<>();
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        plays.add(rs.getString("name"));
      }
      return plays;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Deletes the indicated play from the database.
   * 
   * @param name - String, name of play to delete
   * @author awainger
   */
  public void deletePlay(String name) {
    try (PreparedStatement prep1 = conn.prepareStatement(
        "DELETE FROM play WHERE name = ?;");
        PreparedStatement prep2 = conn.prepareStatement(
            "DELETE FROM play_detail WHERE play = ?;");
        PreparedStatement prep3 = conn.prepareStatement(
            "DELETE FROM play_detail_ball WHERE play = ?;")) {
      prep1.setString(1, name);
      prep2.setString(1, name);
      prep3.setString(1, name);
      prep3.executeUpdate();
      prep2.executeUpdate();
      prep1.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }
}
