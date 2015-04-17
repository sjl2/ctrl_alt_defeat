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
import com.google.common.collect.Multiset.Entry;

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

  private static final int THREE = 3;
  private static final int FOUR = 4;
  private static final int FIVE = 5;
  private static final int SIX = 6;
  private static final int SEVEN = 7;
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
        PreparedStatement prep = conn.prepareStatement(
            "INSERT INTO play_detail VALUES(?, ?, ?, ?, ?);");
        PreparedStatement prep2 = conn.prepareStatement(
            "DELETE FROM play_detail WHERE play = ?")) {

      prep2.setString(1, name);
      prep2.executeUpdate();

      // Loops through entire play, each location[] represents a given
      // player's path, each entry in the location[] represents a frame
      for (int position = 0; position < length; position++) {
        for (int frame = 0; frame < paths[position].length; frame++) {
          Location l = paths[position][frame];
          prep.setString(1, name);
          prep.setString(2, bballPositions[position].getName());
          prep.setInt(THREE, frame);
          prep.setDouble(FOUR, l.getX());
          prep.setDouble(FIVE, l.getY());
          prep.addBatch();
        }
      }

      prep.executeBatch();

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
    for (GameStats gs : gameStats) {

      StringBuilder query = new StringBuilder("UPDATE boxscore SET ");

      Collection<Entry<String>> cols = gs.getStats();
      int i = 0;
      for (Entry<String> e : cols) {
        if (i < cols.size()) {
          query.append(e.getElement() + " = ?, ");
        } else {
          query.append(e.getElement() + " = ?) ");
        }
        i++;
      }
      query.append("WHERE game = ? AND team = ? AND player = ?;");

      try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
          i = 1;
          for (Entry<String> e : cols) {
            ps.setInt(i, e.getCount());
            i++;
          }
          ps.setInt(i++, gs.getGame().getID());
          ps.setInt(i++, gs.getTeamID());
          ps.setInt(i, gs.getPlayer().getID());
          ps.execute();
      } catch (SQLException e) {
        String message = "Failed to update gameStats in database: ";
        throw new RuntimeException(message + e.getMessage());
      }
    }
  }

  public GameStats loadGameStats(Game game, Team team, Player player)
      throws GameException {

    String query =
        "SELECT * FROM boxscore "
        + "WHERE game = ? AND team = ? AND player = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      ps.setInt(1, game.getID());
      ps.setInt(2, team.getID());
      ps.setInt(THREE, player.getID());

      ResultSet rs = ps.executeQuery();

      List<Integer> values = new ArrayList<>();

      if (rs.next()) {
        List<String> titles = GameStats.getNames();
        for (int i = 1; i <= GAME_STATS_LENGTH; i++) {
          values.add(rs.getInt(i));
        }
      } else {
        throw new GameException("No Game Stats for " + game + " in the DB.");
      }

      return new GameStats(values, game, player);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
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
        List<Integer> values = gs.getStats();
        for (int i = 1; i <= GAME_STATS_LENGTH; i++) {
          ps.setInt(i, values.get(i - 1));
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
      ps.setInt(THREE, s.getPlayer().getID());
      ps.setString(FOUR, statID);
      ps.setInt(FIVE, game.getPeriod());
      ps.setDouble(SIX, s.getLocation().getX());
      ps.setDouble(SEVEN, s.getLocation().getY());

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

  public void saveTeam(String name, String color1, String color2) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO team VALUES(?, ?, ?, ?);")) {
      prep.setInt(1, getNextID("team"));
      prep.setString(2, name);
      prep.setString(THREE, color1);
      prep.setString(FOUR, color2);
      prep.executeUpdate();

    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }


}
