package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

/**
 * DBManager class, handles connection to database.
 * @author awainger
 */
public class DBManager {

  private static final int GAME_STATS_LENGTH = 20;
  private Connection conn;
  private Multiset<String> nextIDs;

  /**
   * Constructor for DBManager class, sets up connection.
   * @param path - String representing path to db file
   * @author awainger
   */
  public DBManager(String path) {
    try {
      nextIDs = HashMultiset.create();
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
   * @param play - Play, with name, frames and paths set from front end.
   * @author awainger
   */
  public void savePlay(Play play) {
    String name = play.getName();
    if (!doesPlayExist(name)) {
      saveToPlaysTable(name, play.getNumFrames());
    }

    Location[][] paths = play.getPaths();

    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;

    try (
        PreparedStatement prep1 = conn.prepareStatement(
            "DELETE FROM play_detail WHERE play = ?");
        PreparedStatement prep2 = conn.prepareStatement(
            "INSERT INTO play_detail VALUES(?, ?, ?, ?, ?);")) {

      prep1.setString(1, name);
      prep1.executeUpdate();

      // Loops through entire play, each location[] represents a given
      // player's path, each entry in the location[] represents a frame
      for (int position = 0; position < length; position++) {
        for (int frame = 0; frame < paths[position].length; frame++) {
          Location l = paths[position][frame];
          prep2.setString(1, name);
          prep2.setString(2, bballPositions[position].getName());
          prep2.setInt(3, frame);
          prep2.setDouble(4, l.getX());
          prep2.setDouble(5, l.getY());
          prep2.addBatch();
        }
      }

      prep2.executeBatch();

    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  private void saveToPlaysTable(String name, int numFrames) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO play VALUES(?, ?);")) {

      prep.setString(1, name);
      prep.setInt(2, numFrames);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  private boolean doesPlayExist(String name) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM play WHERE name == ? LIMIT 1;")) {
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Fetches all the data associated with a single play to send
   * to the front end.
   * @param name - String, corresponding to play front end is requesting
   * @return Play, with all fields set.
   */
  public Play loadPlay(String name) {
    Play play = loadPlayMetaData(name);
    int numFrames = play.getNumFrames();
    BasketballPosition[] bballPositions = BasketballPosition.values();
    int length = bballPositions.length;
    Location[][] paths = new Location[length][];

    for (int i = 0; i < length; i++) {
      try (PreparedStatement prep = conn.prepareStatement(
          "SELECT frame, x, y "
          + "FROM play_detail "
          + "WHERE play = ? AND position = ?;")) {

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

    play.setPaths(paths);
    return play;
  }

  private Play loadPlayMetaData(String name) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT numFrames FROM play WHERE name = ?")) {
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
   * @return List strings, play names
   * @author awainger
   */
  public List<String> loadPlayNames() {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM play;")) {
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
   * @param name - String, name of play to delete
   * @author awainger
   */
  public void deletePlay(String name) {
    try (
        PreparedStatement prep1 = conn.prepareStatement(
            "DELETE FROM play WHERE name = ?");
        PreparedStatement prep2 = conn.prepareStatement(
            "DELETE FROM play_details WHERE play = ?") ) {
      prep1.setString(1, name);
      prep2.setString(1, name);
      prep1.executeUpdate();
      prep2.executeUpdate(); 
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets player from database.
   * @param id - Int, corresponding to player to get
   * @return - Player, with fields populated from db info
   * @author awainger
   */
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

  /**
   * Gets team from database.
   * @param id - Int, corresponding to team
   * @param pf - PlayerFactor, to create players later
   * @return - Team, populated with db info
   * @author awainger
   */
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

  public Collection<Integer> getTeamPlayers(Team team) {
    String query = "SELECT id "
        + "FROM player "
        + "WHERE team = ?;";

    Collection<Integer> players = new ArrayList<>();

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, team.getID());
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        players.add(rs.getInt("id"));
      }

    } catch (SQLException e) {
      String message = "Could not retrieve players for team " + team + " from"
          + " the database.";
      close();
      throw new RuntimeException(message);
    }

    return players;
  }

  public void updateBoxscore(Collection<GameStats> gameStats) {
    StringBuilder query = new StringBuilder("UPDATE boxscore SET ");
    List<String> cols = GameStats.getStatTitles();

    for (int i = 0; i < (GAME_STATS_LENGTH - 1); i++) {
      query.append(cols.get(i) + " = ?, ");
    }
    query.append(cols.get(GAME_STATS_LENGTH -1) + " = ?)");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      for (GameStats gs : gameStats) {
        List<Integer> values = gs.getStatValues();
        for (int i = 1; i <= GAME_STATS_LENGTH; i++) {
          ps.setInt(i, values.get(i));
        }

        ps.addBatch();

      }

      ps.execute();
    } catch (SQLException e) {
      String message = "Failed to update gameStats in database: ";
      throw new RuntimeException(message + e.getMessage());
    }



  }

  public void storeGameStats(GameStats gs) throws GameException {
    if (gs.getID() == -1 || gs.getPlayer() == null) {
      throw new GameException("Cannot store game stats of a team.");
    } else {

      StringBuilder query = new StringBuilder("INSERT INTO boxscore (");
      for (int i = 0; i < (GAME_STATS_LENGTH - 1); i++) {
        query.append("?, ");
      }
      query.append("?)");

      try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
        List<Integer> values = gs.getStatValues();
        for (int i = 1; i <= GAME_STATS_LENGTH; i++) {
          ps.setInt(i, values.get(i));
        }

        ps.execute();
      } catch (SQLException e) {
        String message = "Failed to add games stats for " + gs.getPlayer()
            + " to database: ";
        throw new RuntimeException(message + e.getMessage());
      }
    }

  }

  public void storeStat(Stat s, String statID, Game game) {

    String query = "INSERT INTO stat VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

      ps.setInt(1, s.getID());
      ps.setInt(2, game.getID());
      ps.setInt(3, s.getPlayer().getID());
      ps.setString(4, statID);
      ps.setInt(5, game.getPeriod());
      ps.setDouble(6, s.getLocation().getX());
      ps.setDouble(7, s.getLocation().getY());

      // TODO Maybe Batch? See if this works
      ps.execute();

    } catch (SQLException e) {
      String message = "Failed add " + s + " to the database: ";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public void remove(Stat s) {
    // TODO Auto-generated method stub
    String query = "DELETE FROM stat WHERE id = ? AND player = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, s.getID());
      ps.setInt(2, s.getPlayer().getID());
    } catch (SQLException e) {
      String message = "Failure to remove " + s + " from database.";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public int getNextID(String table) {
    int nextID = nextIDs.count(table);

    if (nextID == 0) {
      String query = "SELECT MAX(id) FROM " + table + ";";

      try (PreparedStatement prep = conn.prepareStatement(query)) {
        ResultSet rs = prep.executeQuery();

        if (rs.next()) {
          nextID = rs.getInt(1) + 1;
        } else {
          nextID = 1;
        }

        nextIDs.setCount(table, nextID);

      } catch (SQLException e) {
        String message = "Could not get next id for " + table + "table: ";
        close();
        throw new RuntimeException(message + e.getMessage());
      }
    }

    return nextIDs.add(table, 1);
  }

  /**
   * Saves a team to the database.
   * @param name
   * @param color1
   * @param color2
   */
  public void saveTeam(String name, String color1, String color2) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO team VALUES(?, ?, ?, ?);")) {
      prep.setInt(1, getNextID("team"));
      prep.setString(2, name);
      prep.setString(3, color1);
      prep.setString(4, color2);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Saves a player to the database.
   * @param team - Int, team id
   * @param number - Int, player's jersey number
   * @param current - Boolean (represented as int), whether player is currently on team
   * @author awainger
   */
  public void savePlayer(int team, int number, int current) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO player VALUES(?, ?, ?, ?);")) {
      prep.setInt(1, getNextID("player"));
      prep.setInt(2, team);
      prep.setInt(3, number);
      prep.setInt(4, current);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }
  
  public List<String> getTeams() {
    return null;
  }
}
