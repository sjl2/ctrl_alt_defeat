package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.OldGame;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.TeamFactory;
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
  private static final int EIGHT = 8;
  private static final int DUMMY_PLAYER_ID = 0;

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
          prep2.setInt(THREE, frame);
          prep2.setDouble(FOUR, l.getX());
          prep2.setDouble(FIVE, l.getY());
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
            "DELETE FROM play_detail WHERE play = ?") ) {
      prep1.setString(1, name);
      prep2.setString(1, name);
      prep2.executeUpdate();
      prep1.executeUpdate();
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
    String query = "SELECT name, team, number "
        + "FROM player "
        + "WHERE id = ?;";

    Player player = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString("name");
        int team = rs.getInt("team");
        int number = rs.getInt("number");

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
    String query = "SELECT name, coach, color1, color2 "
        + "FROM team "
        + "WHERE team.id = ?;";

    Team team = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString("name");
        String coach = rs.getString("coach");
        String color1 = rs.getString("color1");
        String color2 = rs.getString("color2");

        team = new Team(id, name, coach, color1, color2, pf);
      }
    } catch (SQLException e) {
      String message = "Could not retrieve team " + id + " from the database: ";
      close();
      throw new RuntimeException(message + e.getMessage());
    }

    return team;
  }

  public Collection<Integer> getTeamPlayers(Team team) {
    String query = "SELECT id "
        + "FROM player "
        + "WHERE team = ? AND current = 1;";

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
    StringBuilder query = new StringBuilder("UPDATE player_stats SET ");

      String[] cols = GameStats.getCols();
      for (int i = 0; i < cols.length; i++) {
        if (i < cols.length - 1) {
          query.append(cols[i] + " = ?, ");
        } else {
          query.append(cols[i] + " = ? ");
        }
      }
      query.append("WHERE game = ? AND team = ? AND player = ?;");

      try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
        for (GameStats gs : gameStats) {
          if (gs.getPlayer() == null) {
            List<Integer> vals = gs.getValues();
            int i = 1;
            for (int v : vals) {
              ps.setInt(i, v);
              i++;
            }
            ps.setInt(i++, gs.getGameID());
            ps.setInt(i++, gs.getTeam().getID());
            ps.setInt(i, gs.getPlayer().getID());

            ps.addBatch();
          } else {
            updateTeamStats(gs);
          }
        }

        ps.executeBatch();
      } catch (SQLException e) {
        String message = "Failed to update gameStats in database: ";
        throw new RuntimeException(message + e.getMessage());
      }
  }

  private void updateTeamStats(GameStats gs) {
    StringBuilder query = new StringBuilder("UPDATE team_stats SET ");

    String[] cols = GameStats.getCols();
    for (int i = 0; i < (cols.length - 1); i++) {
      if (i < cols.length - 1) {
        query.append(cols[i] + " = ?, ");
      } else {
        query.append(cols[i] + " = ? ");
      }
    }

    query.append("WHERE game = ? AND team = ?;");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      List<Integer> vals = gs.getTeamValues();
      int i = 1;
      for (int v : vals) {
        ps.setInt(i, v);
        i++;
      }
      ps.setInt(i++, gs.getGameID());
      ps.setInt(i, gs.getTeam().getID());

      ps.addBatch();
    } catch (SQLException e) {
      String message = "Failed to update team stats for " + gs.getTeam();
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public Map<Integer, GameStats> loadBoxScore(int id, Team team)
      throws GameException {

    // Load All Plays
    String query =
        "SELECT * FROM player_stats "
        + "WHERE game = ? AND team = ?;";

    Map<Integer, GameStats> allGameStats = new HashMap<>();

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      ps.setInt(1, id);
      ps.setInt(2, team.getID());

      ResultSet rs = ps.executeQuery();

      List<Integer> values = new ArrayList<>();

      while (rs.next()) {
        int len = GameStats.getNumCols();
        for (int i = 1; i <= len; i++) {
          values.add(rs.getInt(i));
        }
        allGameStats.put(values.get(2), new GameStats(values, id, team));
      }

    } catch (SQLException e) {
      throw new GameException("Failed to load game stats: " + e.getMessage());
    }

    // Load Team Stats
    query =
        "SELECT * FROM team_stats "
        + "WHERE game = ? AND team = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      ps.setInt(1, id);
      ps.setInt(2, team.getID());

      ResultSet rs = ps.executeQuery();

      List<Integer> values = new ArrayList<>();

      if (rs.next()) {
        int len = GameStats.getNumCols();
        for (int i = 1; i <= len; i++) {
          if (i == 3) {
            values.add(DUMMY_PLAYER_ID);
          } else {
            values.add(rs.getInt(i));
          }
        }
        allGameStats.put(values.get(2), new GameStats(values, id, team));
      }

    } catch (SQLException e) {
      throw new GameException("Failed to load game stats: " + e.getMessage());
    }

    return allGameStats;
  }

  public void saveBoxScore(Collection<GameStats> stats) {
    int numCols = GameStats.getCols().length;
    StringBuilder query = new StringBuilder("INSERT INTO player_stats VALUES (");

    for (int i = 0; i < (numCols - 1); i++) {
      query.append("?, ");
    }
    query.append("?)");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      for (GameStats gs : stats) {
        if (gs.getPlayer() != null) {
          List<Integer> values = gs.getValues();
          for (int i = 1; i <= numCols; i++) {
            ps.setInt(i, values.get(i - 1));
          }
          ps.addBatch();
        } else {
          saveTeamStats(gs);
        }
      }
      ps.executeBatch();
    } catch (SQLException e) {
      String message = "Failed to save player stats to database. ";
      throw new RuntimeException(message + e.getMessage());
    }

  }

  private void saveTeamStats(GameStats gs) {
    StringBuilder query = new StringBuilder("INSERT INTO team_stats VALUES (");
    int numCols = GameStats.getCols().length - 1;
    for (int i = 0; i < (numCols - 1); i++) {
      query.append("?, ");
    }
    query.append("?)");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      List<Integer> values = gs.getTeamValues();
      for (int i = 1; i <= numCols; i++) {
        if (i != 3) {
          ps.setInt(i, values.get(i - 1));
        }
      }
      ps.execute();
    } catch (SQLException e) {
      String message = "Failed to save team stats to database. ";
      throw new RuntimeException(message + e.getMessage());
    }

  }

  public void storeStat(Stat s, String statType, Game game) {

    String query = "INSERT INTO stat VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

      ps.setInt(1, s.getID());
      ps.setInt(2, game.getID());
      ps.setInt(THREE, s.getPlayer().getTeamID());
      ps.setInt(FOUR, s.getPlayer().getID());
      ps.setString(FIVE, statType);
      ps.setInt(SIX, game.getPeriod());
      ps.setDouble(SEVEN, s.getLocation().getX());
      ps.setDouble(EIGHT, s.getLocation().getY());

      ps.execute();

    } catch (SQLException e) {
      String message = "Failed to add " + s + " to the database: ";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public void updateStat(Stat s) {
    String query = "UPDATE stat "
        + "SET player = ?, type = ?, x = ?, y = ? "
        + "WHERE id = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, s.getPlayer().getID());
      ps.setString(2, s.getStatType());
      ps.setDouble(THREE, s.getLocation().getX());
      ps.setDouble(FOUR, s.getLocation().getY());
      ps.setDouble(FIVE, s.getID());

      ps.execute();
    } catch (SQLException e) {
      String message = "Failed to update stat " + s + " in database: ";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public void removeStat(Stat s) throws GameException {
    String query = "DELETE FROM stat WHERE id = ? AND player = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, s.getID());
      ps.setInt(2, s.getPlayer().getID());
    } catch (SQLException e) {
      String message = "Failure to remove " + s + " from database.";
      throw new GameException(message + e.getMessage());
    }
  }

  public boolean doesUsernameExist(String username) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM user WHERE name == ? LIMIT 1;")) {
      prep.setString(1, username);
      ResultSet rs = prep.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public int checkPassword(String username, String password) {
    if(!doesUsernameExist(username)) {
      return -1;
    }
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT password, clearance FROM user WHERE name == ? LIMIT 1;")) {
      prep.setString(1, username);
      ResultSet rs = prep.executeQuery();
      if(rs.next()) {
        String dbPassword = rs.getString(1);
        int clearance = rs.getInt(2);
        if(dbPassword.equals(password)) {
          return clearance;
        } else {
          return -1;
        }
      } else {
        return -1;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
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

  public void saveTeam(Team team, boolean b) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO team VALUES(?, ?, ?, ?, ?, ?);")) {
      prep.setInt(1, team.getID());
      prep.setString(2, team.getName());
      prep.setString(THREE, team.getCoach());
      prep.setString(FOUR, team.getPrimary());
      prep.setString(FIVE, team.getSecondary());
      prep.setBoolean(SIX, b);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Saves a player to the database.
   * @param name - String, player's name
   * @param team - Int, team id
   * @param number - Int, player's jersey number
   * @param current - Boolean (represented as int), whether player is currently on team
   * @author awainger
   */
  public void savePlayer(String name, int team, int number, int current) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO player VALUES(?, ?, ?, ?, ?);")) {
      prep.setInt(1, getNextID("player"));
      prep.setString(2, name);
      prep.setInt(3, team);
      prep.setInt(4, number);
      prep.setInt(5, current);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }


  /**
   * Generates a list of team names and id's for the create-player handler.
   * @return - List of teams, (dummy teams though)
   * @author awainger
   */
  public List<Team> getTeams() {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id, name FROM team;")) {
      List<Team> teams = new ArrayList<>();
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Team t = new Team(id, name);
        teams.add(t);
      }

      return teams;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  public void saveGame(Game game) {

    String query = "INSERT INTO game VALUES(?, ?, ?, ?);";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, game.getID());
      prep.setString(2, game.getDate().toString());
      prep.setInt(3, game.getHome().getID());
      prep.setInt(4, game.getAway().getID());

      prep.executeUpdate();
    } catch (SQLException e) {
      String message = "Failed to save game data (" + game + ").";
      throw new RuntimeException(message + e.getMessage());
    }
  }


  public OldGame getGameByID(int id, TeamFactory tf)
      throws DashboardException, GameException {
    String query = "SELECT * FROM game WHERE id = ?";
    OldGame g = null;
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);

      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        LocalDate date = LocalDate.parse(rs.getString("date"));
        int homeID = rs.getInt("home");
        int awayID = rs.getInt("away");

        g = new OldGame(this, id, tf.getTeam(homeID), tf.getTeam(awayID), date);

      } else {
        throw new DashboardException("No game in database with the id " + id);
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return g;
  }

  public Team getMyTeam(PlayerFactory pf) throws DashboardException {
    String query = "Select id, name, coach, color1, color2 "
        + "FROM team "
        + "WHERE my_Team = 1;";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        return new Team(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("coach"),
            rs.getString("color1"),
            rs.getString("color2"),
            pf);
      } else {
        String message = "No myTeam in database. Please create one.";
        throw new DashboardException(message);
      }

    } catch (SQLException e) {
      String message = "No myTeam in database. Please create one.";
      throw new DashboardException(message + " " + e.getMessage());
    }
  }


}









