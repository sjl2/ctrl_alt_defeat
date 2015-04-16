package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
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
        "SELECT MAX(id) FROM play;")) {

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
        "INSERT INTO play_detail VALUES(?, ?, ?, ?, ?);")) {

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
        "INSERT INTO play VALUES(?, ?, ?);")) {

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
    Play play = loadPlayMetaData(id);
    int numFrames = play.getNumFrames();
    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;
    Location[][] paths = new Location[length][];

    for (int i = 0; i < length; i++) {
      try (PreparedStatement prep = conn.prepareStatement(
          "SELECT frame, x, y "
          + "FROM play_detail "
          + "WHERE play = ? AND position = ?;")) {

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

  private Play loadPlayMetaData(int id) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name, numFrames FROM play WHERE id = ?")) {
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
  
  /**
   * Loads play ids and names to pass to front end
   * @return Map, integer to string
   */
  public Map<Integer, String> loadPlayIDs() {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id, name FROM play;")) {
      Map<Integer, String> plays = new HashMap<>();
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        plays.put(rs.getInt("id"), rs.getString("name"));
      }
      return plays;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public Player getPlayer(int id) {
    String query = "SELECT person.name, player.team, player.number "
        + "FROM person, player "
        + "WHERE player.id = ?;";

    Player player = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString("person.name");
        int team = rs.getInt("player.team");
        int number = rs.getInt("player.number");

        player = new Player(id, name, number, team);
      }
    } catch (SQLException e) {
      String message = "Could not retrieve player " + id + " from the "
          + "database.";
      close();
      throw new RuntimeException(message);
    }

    return player;
  }

  public Team getTeam(int id, PlayerFactory pf) {
    String query = "SELECT team.name, team.color1, team.color2 "
        + "FROM team "
        + "WHERE team.id = ?;";

    Team team = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString("team.name");
        String color1 = rs.getString("team.color1");
        String color2 = rs.getString("team.color2");

        team = new Team(id, name, color1, color2, pf);
      }
    } catch (SQLException e) {
      String message = "Could not retrieve team " + id + " from the database.";
      close();
      throw new RuntimeException(message);
    }

    return team;
  }

  public Collection<Integer> getTeamPlayers(int teamID) {
    String query = "SELECT id "
        + "FROM player "
        + "WHERE team = ?;";

    Collection<Integer> players = new ArrayList<>();

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, teamID);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        players.add(rs.getInt("id"));
      }

    } catch (SQLException e) {
      String message = "Could not retrieve players for team " + teamID + " from"
          + " the database.";
      close();
      throw new RuntimeException(message);
    }

    return players;
  }
}
