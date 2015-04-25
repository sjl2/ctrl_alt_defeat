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
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameView;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.TeamFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.TeamStats;

/**
 * DBManager class, handles connection to database.
 * @author sjl2
 */
public class DBManager {

  private static final int THREE = 3;
  private static final int FOUR = 4;
  private static final int FIVE = 5;
  private static final int SIX = 6;
  private static final int SEVEN = 7;
  private static final int EIGHT = 8;

  private Connection conn;
  private PlayerFactory pf;
  private TeamFactory tf;
  private Multiset<String> nextIDs;

  /**
   * Constructor for DBManager class, sets up connection.
   * @param path - String representing path to db file
   * @author sjl2
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

    this.pf = new PlayerFactory(this);
    this.tf = new TeamFactory(this);
  }

  /**
   * Call any time there is an error or you are done with the DBManager.
   * @author sjl2
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
   * @author sjl2
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

      conn.setAutoCommit(false);
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
      conn.commit();
      conn.setAutoCommit(true);
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
   * @author sjl2
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
   * @author sjl2
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
   * @author sjl2
   */
  public Player getPlayer(int id) {

    Player p = pf.getPlayer(id);

    if (p != null) {
      return p;
    }

    String query = "SELECT p.name, p.team, t.name, p.number, p.current "
        + "FROM player as p, team as t "
        + "WHERE p.id = ? AND t.id = p.team;";

    Player player = null;

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        String name = rs.getString(1);
        int teamID = rs.getInt(2);
        String teamName = rs.getString(THREE);
        int number = rs.getInt(FOUR);
        boolean current = rs.getBoolean(FIVE);

        player = pf.getPlayer(id, name, teamID, teamName, number, current);

      }
    } catch (SQLException e) {
      String message = "Could not retrieve player " + id + " from the "
          + "database.";
      close();
      throw new RuntimeException(message + e.getMessage());
    }

    return player;
  }

  public String getTeamNameByID(int teamID) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM team WHERE id = ?;")) {
      prep.setInt(1, teamID);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        return rs.getString("name");
      } else {
        return "Team name could not be found ... This should be looked into...";
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets team from database.
   * @param id - Int, corresponding to team
   * @param pf - PlayerFactor, to create players later
   * @return - Team, populated with db info
   * @author sjl2
   */
  public Team getTeam(int id) {
    Team t = tf.getTeam(id);

    if (t != null) {
      return t;
    }

    String query = "SELECT name, coach, color1, color2, my_team "
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

        //team = new Team(id, name, coach, color1, color2, pf);
        team = tf.getTeam(id, name, coach, color1, color2);
      }
    } catch (SQLException e) {
      String message = "Could not retrieve team " + id + " from the database: ";
      close();
      throw new RuntimeException(message + e.getMessage());
    }

    return team;
  }

  public Team getMyTeam() throws DashboardException {
    String query = "Select id, name, coach, color1, color2 "
        + "FROM team "
        + "WHERE my_Team = 1;";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        return getTeam(rs.getInt("id"));
      } else {
        String message = "No myTeam in database. Please create one.";
        throw new DashboardException(message);
      }

    } catch (SQLException e) {
      String message = "No myTeam in database. Please create one.";
      throw new DashboardException(message + " " + e.getMessage());
    }
  }

  public Collection<Player> getTeamPlayers(Team team) {
    return getTeamPlayers(team.getID());
  }

  public Collection<Player> getTeamPlayers(int id) {
    String query = "SELECT id "
        + "FROM player "
        + "WHERE team = ? AND current = 1;";

    Collection<Player> players = new ArrayList<>();

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      while (rs.next()) {
        players.add(getPlayer(rs.getInt("id")));
      }

    } catch (SQLException e) {
      String message = "Could not retrieve players for team " + id + " from"
          + " the database.";
      close();
      throw new RuntimeException(message);
    }

    return players;
  }

  public void updateBoxscore(Collection<PlayerStats> gameStats, TeamStats ts) {
    StringBuilder query = new StringBuilder("UPDATE player_stats SET ");

      List<String> cols = PlayerStats.getCols();
      for (int i = 0; i < cols.size(); i++) {
        if (i < cols.size() - 1) {
          query.append(cols.get(i) + " = ?, ");
        } else {
          query.append(cols.get(i) + " = ? ");
        }
      }
      query.append("WHERE game = ? AND team = ? AND player = ?;");

      try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
        for (PlayerStats gs : gameStats) {
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
        }

        ps.executeBatch();
      } catch (SQLException e) {
        String message = "Failed to update gameStats in database: ";
        throw new RuntimeException(message + e.getMessage());
      }

      updateTeamStats(ts);
  }

  private void updateTeamStats(TeamStats ts) {
    StringBuilder query = new StringBuilder("UPDATE team_stats SET ");

    List<String> cols = TeamStats.getCols();
    for (int i = 0; i < cols.size(); i++) {
      if (i < cols.size() - 1) {
        query.append(cols.get(i) + " = ?, ");
      } else {
        query.append(cols.get(i) + " = ? ");
      }
    }

    query.append("WHERE game = ? AND team = ?;");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      List<Integer> vals = ts.getValues();
      int i = 1;
      for (int v : vals) {
        ps.setInt(i++, v);
      }
      ps.setInt(i++, ts.getGameID());
      ps.setInt(i, ts.getTeam().getID());

      ps.execute();
    } catch (SQLException e) {
      String message = "Failed to update team stats for " + ts.getTeam();
      throw new RuntimeException(message + e.getMessage());
    }
  }

  public Map<Integer, PlayerStats> loadPlayerStats(int gameID, Team team)
      throws GameException {

    // Load All Plays
    String query =
        "SELECT * FROM player_stats "
        + "WHERE game = ? AND team = ?;";

    Map<Integer, PlayerStats> allGameStats = new HashMap<>();

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      ps.setInt(1, gameID);
      ps.setInt(2, team.getID());

      ResultSet rs = ps.executeQuery();

      List<Integer> values = new ArrayList<>();

      while (rs.next()) {
        int len = PlayerStats.getNumCols();
        for (int i = 1; i <= len; i++) {
          values.add(rs.getInt(i));
        }
        allGameStats.put(values.get(2), new PlayerStats(values, gameID, team));
        values.clear();
      }

    } catch (SQLException e) {
      String message = "Failed to load player game stats: " + e.getMessage();
      throw new GameException(message);
    }

    return allGameStats;
  }

  public TeamStats loadTeamStats(int gameID, Team team) throws GameException {

    String query =
        "SELECT * FROM team_stats "
        + "WHERE game = ? AND team = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, gameID);
      ps.setInt(2, team.getID());

      ResultSet rs = ps.executeQuery();

      List<Integer> values = new ArrayList<>();

      if (rs.next()) {
        int len = TeamStats.getNumCols();
        for (int i = 1; i <= len; i++) {
          values.add(rs.getInt(i));
        }
        return new TeamStats(values, gameID, team);
      }

    } catch (SQLException e) {
      String message = "Failed to load team game stats: " + e.getMessage();
      throw new GameException(message);
    }

    return null;
  }

  public void saveBoxScore(Collection<PlayerStats> stats, TeamStats ts) {
    int numCols = PlayerStats.getNumCols();
    StringBuilder query = new StringBuilder("INSERT INTO player_stats VALUES (");

    for (int i = 0; i < (numCols - 1); i++) {
      query.append("?, ");
    }
    query.append("?)");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      for (PlayerStats playerStats : stats) {
        List<Integer> values = playerStats.getValues();
        for (int i = 1; i <= numCols; i++) {
          ps.setInt(i, values.get(i - 1));
        }
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      String message = "Failed to save player stats to database. ";
      throw new RuntimeException(message + e.getMessage());
    }
    saveTeamStats(ts);
  }

  private void saveTeamStats(TeamStats ts) {
    StringBuilder query = new StringBuilder("INSERT INTO team_stats VALUES (");
    int numCols = TeamStats.getNumCols();
    for (int i = 0; i < (numCols - 1); i++) {
      query.append("?, ");
    }
    query.append("?);");

    try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
      List<Integer> values = ts.getValues();
      int i = 1;
      for (int v : values) {
        ps.setInt(i, v);
        i++;
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

  public Team createTeam(String name, String coach, String primary,
      String secondary, boolean myTeam) {

    Team t = tf.getTeam(getNextID("team"), name, coach, primary, secondary);
    saveTeam(t, myTeam);

    return t;
  }

  public Team updateTeam(int id, String name, String coach, String primary,
      String secondary, boolean myTeam) {

    Team t = tf.getTeam(id, name, coach, primary, secondary);
    updateTeam(t, myTeam);
    return t;
  }

  public void updateTeam(Team team, boolean myTeam) {
    String query = "UPDATE team "
        + "SET name = ?, coach = ?, color1 = ?, color2 = ?, my_Team = ? "
        + "WHERE id = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, team.getName());
      prep.setString(2, team.getCoach());
      prep.setString(THREE,  team.getPrimary());
      prep.setString(FOUR,  team.getSecondary());
      prep.setBoolean(FIVE, myTeam);

      prep.setInt(SIX, team.getID());

      prep.execute();
    } catch (SQLException e) {
      String message = "Failed to update " + team + " in the database."
          + e.getMessage();
      close();
      throw new RuntimeException(message);
    }
  }

  private void saveTeam(Team team, boolean myTeam) {
    try (PreparedStatement prep = conn.prepareStatement(
        "INSERT INTO team VALUES(?, ?, ?, ?, ?, ?);")) {
      prep.setInt(1, team.getID());
      prep.setString(2, team.getName());
      prep.setString(THREE, team.getCoach());
      prep.setString(FOUR, team.getPrimary());
      prep.setString(FIVE, team.getSecondary());
      prep.setBoolean(SIX, myTeam);
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  public Player createPlayer(String name, int teamID, int number, boolean curr) {
    Team t = getTeam(teamID);
    Player p = pf.getPlayer(
        getNextID("player"),
        name,
        teamID,
        t.getName(),
        number,
        curr);

    t.addPlayer(p);
    savePlayer(p);

    return p;
  }

  public Player updatePlayer(int id, String name, int teamID, int number, boolean curr) {
    Team t = getTeam(teamID);
    Player p = pf.getPlayer(id, name, teamID, t.getName(), number, curr);
    t.addPlayer(p);
    updatePlayer(p);
    return p;
  }

  public void updatePlayer(Player player) {
    String query = "UPDATE player "
        + "SET name = ?, team = ?, number = ?, current = ? "
        + "WHERE id = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, player.getName());
      prep.setInt(2, player.getTeamID());
      prep.setInt(THREE, player.getNumber());
      prep.setBoolean(FOUR, player.getCurrent());
      prep.setInt(FIVE, player.getID());

      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  private void savePlayer(Player player) {
    String query = "INSERT INTO player VALUES(?, ?, ?, ?, ?);";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, player.getID());
      prep.setString(2, player.getName());
      prep.setInt(3, player.getTeamID());
      prep.setInt(4, player.getNumber());
      prep.setBoolean(5, player.getCurrent());
      prep.executeUpdate();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }


  /**
   * Generates a list of team names and id's for the create-player handler.
   */
  private List<Team> getTeamLinks(boolean includeMyTeam) {
    String query = "SELECT id, name FROM team";

    if (!includeMyTeam) {
      query = query + " WHERE my_team = 0";
    }
    query = query + ";";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      List<Team> teams = new ArrayList<>();
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Team t = Team.newTeamLink(id, name);
        teams.add(t);
      }

      return teams;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  public List<Team> getAllTeams() {
    return getTeamLinks(true);
  }

  /**
   * @return Returns a list of opposing teams (all teams except my team)
   * @author sjl2
   */
  public List<Team> getOpposingTeams() {
    return getTeamLinks(false);
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


  public GameView getGameByID(int id)
      throws DashboardException, GameException {
    String query = "SELECT * FROM game WHERE id = ?";
    GameView g = null;
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);

      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        LocalDate date = LocalDate.parse(rs.getString("date"));
        int homeID = rs.getInt("home");
        int awayID = rs.getInt("away");

        g = new GameView(this, id, getTeam(homeID), getTeam(awayID), date);

      } else {
        throw new DashboardException("No game in database with the id " + id);
      }

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return g;
  }

  public void deleteGame(int id) {
    String query = "";

    List<String> tables = new ArrayList<>();
    tables.add("player_stats");
    tables.add("team_stats");
    tables.add("stat");

    for (String table : tables) {
      query = "DELETE FROM " + table + " WHERE game = ?;";

      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.setInt(1, id);
        prep.execute();
      } catch (SQLException e) {
        String message = "Failed to delete from the " + table + " table for "
            + "incomplete game. " + e.getMessage();
        throw new RuntimeException(message);
      }
    }

    query = "DELETE FROM game WHERE id = ?;";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      prep.execute();
    } catch (SQLException e) {
      String message = "Failed to delete incomplete game from game table. "
          + e.getMessage();
      throw new RuntimeException(message);
    }

  }

  public int getTeamWins(int gameID, int teamID, LocalDate date, boolean inclusive) {
    // TODO Auto-generated method stub
    return 0;
  }

  public int getTeamLosses(int gameID, int teamID, LocalDate date, boolean inclusive) {
    // TODO Auto-generated method stub
    return 0;
  }

  public void setMyTeam(Team team) {
    // TODO Auto-generated method stub
  }
  
  // table = either player_stats or team_stats, depending on whether you are getting player or team years, and id is for either team or player
  public List<String> getYearsActive(String table, String id) {
    String query = "SELECT DISTINCT SUBSTR(date, 1, 4) AS year FROM game WHERE id = (SELECT game FROM " + table + " WHERE id = ?) ORDER BY year DESC;";
    return null;
  }

}
