package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameView;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.TeamFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.TeamStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.StringFormatter;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;

/**
 * DBManager class, handles connection to database.
 * @author sjl2
 */
public class DBManager {

  public static final int THREE = 3;
  public static final int FOUR = 4;
  public static final int FIVE = 5;
  public static final int SIX = 6;
  public static final int SEVEN = 7;
  public static final int EIGHT = 8;
  public static final int TOTAL_BINS = 25;

  private Connection conn;
  private PlayerFactory pf;
  private TeamFactory tf;
  private Multiset<String> nextIDs;
  private Trie trie;

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
    this.trie = fillTrie();
  }
  
  public PlaymakerDB getPlaymakerDB() {
    return new PlaymakerDB(conn);
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

  public Connection getConnection() {
    return conn;
  }
  
  /* LOGIN METHODS */
  public boolean doesUsernameExist(String username) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM user WHERE name = ? LIMIT 1;")) {
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
        "SELECT password, clearance FROM user WHERE name = ? LIMIT 1;")) {
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


  /* TEAM AND PLAYER METHODS */
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
  
  
  /******* STAT METHODS *******/

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
          /* Setting the where clause */
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

  public Map<Integer, PlayerStats> getPlayerStats(int gameID, Team team)
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
        allGameStats.put(values.get(2), new PlayerStats(values, gameID, team, getPlayer(values.get(2))));
        values.clear();
      }

    } catch (SQLException e) {
      String message = "Failed to load player game stats: " + e.getMessage();
      throw new GameException(message);
    }

    return allGameStats;
  }

  public TeamStats getTeamStats(int gameID, Team team) throws GameException {

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

  public void createBoxScore(Collection<PlayerStats> stats, TeamStats ts) {
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
    createTeamStats(ts);
  }

  private void createTeamStats(TeamStats ts) {
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

  public void createStat(Stat s, int game) {

    String query = "INSERT INTO stat VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(query)) {

      ps.setInt(1, s.getID());
      ps.setInt(2, game);
      ps.setInt(THREE, s.getPlayer().getTeamID());
      ps.setInt(FOUR, s.getPlayer().getID());
      ps.setString(FIVE, s.getStatType());
      ps.setInt(SIX, s.getPeriod());
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

  public void deleteStat(Stat s) throws GameException {
    String query = "DELETE FROM stat WHERE id = ? AND player = ?;";
    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, s.getID());
      ps.setInt(2, s.getPlayer().getID());
    } catch (SQLException e) {
      String message = "Failure to remove " + s + " from database.";
      throw new GameException(message + e.getMessage());
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
    createTeam(t, myTeam);

    return t;
  }

  public void updateTeam(int id, String name, String coach, String primary,
      String secondary) {

    Team t = tf.getTeam(id, name, coach, primary, secondary);
    System.out.println(t.getCoach());
    updateTeam(t);
  }

  public void updateTeam(Team team) {
    String query = "UPDATE team "
        + "SET name = ?, coach = ?, color1 = ?, color2 = ? "
        + "WHERE id = ?";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, team.getName());
      prep.setString(2, team.getCoach());
      prep.setString(THREE,  team.getPrimary());
      prep.setString(FOUR,  team.getSecondary());

      prep.setInt(FIVE, team.getID());

      prep.execute();
    } catch (SQLException e) {
      String message = "Failed to update " + team + " in the database."
          + e.getMessage();
      close();
      throw new RuntimeException(message);
    }
  }

  private void createTeam(Team team, boolean myTeam) {
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
    createPlayer(p);

    return p;
  }

  public void updatePlayer(int id, String name, int teamID, int number, boolean curr) {
    Player oldPlayer = getPlayer(id);
    if (oldPlayer.getTeamID() != teamID) {
      getTeam(oldPlayer.getTeamID()).removePlayer(oldPlayer);
    }
    Team t = getTeam(teamID);
    Player p = pf.getPlayer(id, name, teamID, t.getName(), number, curr);
    t.addPlayer(p);
    updatePlayer(p);
  }

  private void updatePlayer(Player player) {
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

  private void createPlayer(Player player) {
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
  
  /****** LINK METHODS ******/
  public Link getGameLink(int id) {
    String query = "SELECT g.date, home.name, away.name "
        + "FROM game AS g, team AS home, team AS away "
        + "WHERE g.id = ? AND home.id = g.home AND g.away = away.id";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();

      String date = rs.getString(1);
      String home = rs.getString(2);
      String away = rs.getString(THREE);

      String path = "/game/view/";
      String value = away + " @ " + home + "(" + date + ")";

      return new Link(id, path, value);
    } catch (SQLException e) {
      String message = "Could not obtain link for game " + id + ". ";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  /**
   * Generates a list of team names and id's for the create-player handler.
   */
  private List<Link> getTeamLinks(boolean includeMyTeam) {
    String query = "SELECT id, name FROM team";

    if (!includeMyTeam) {
      query = query + " WHERE my_team = 0";
    }
    query = query + ";";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      List<Link> teams = new ArrayList<>();
      ResultSet rs = prep.executeQuery();
      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String path = "/team/view/";
        teams.add(new Link(id, path, name));
      }

      return teams;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  public List<Link> getAllTeams() {
    return getTeamLinks(true);
  }

  /**
   * @return Returns a list of opposing teams (all teams except my team)
   * @author sjl2
   */
  public List<Link> getOpposingTeams() {
    return getTeamLinks(false);
  }

  public void createGame(Game game) {

    String query = "INSERT INTO game VALUES(?, ?, ?, ?, ?);";
    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, game.getID());
      prep.setString(2, game.getDate().toString());
      prep.setInt(THREE, game.getHome().getID());
      prep.setInt(FOUR, game.getAway().getID());
      prep.setInt(FIVE, getChampionshipYear(game.getDate()));

      prep.executeUpdate();
    } catch (SQLException e) {
      String message = "Failed to save game data (" + game + ").";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  int createGame(LocalDate date, int home, int away) {

    String query = "INSERT INTO game VALUES(?, ?, ?, ?, ?);";
    try (PreparedStatement prep = conn.prepareStatement(query)) {

      int id = getNextID("game");
      prep.setInt(1, id);
      prep.setString(2, date.toString());
      prep.setInt(THREE, home);
      prep.setInt(FOUR, away);
      prep.setInt(FIVE, getChampionshipYear(date));

      prep.executeUpdate();

      return id;
    } catch (SQLException e) {
      String message = "Failed to save game data fake game date. ";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  private int getChampionshipYear(LocalDate date) {
    if (date.getMonthValue() < SEVEN) {
      return date.getYear();
    } else {
      return date.getYear() + 1;
    }
  }


  public GameView getGameByID(int id)
      throws DashboardException, GameException {
    String query = "SELECT date, home, away FROM game WHERE id = ?";
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

  
  /****** PLAYER AND TEAM STAT PAGE POPULATION METHODS ******/
  // table = either player_stats or team_stats, depending on whether you are getting player or team years, and id is for either team or player
  public List<Integer> getYearsActive(String table, int id) {
    String entity = "";
    if (table.equals("player_stats")) {
      entity = "player";
    } else if (table.equals("team_stats")) {
      entity = "team";
    } else {
      throw new RuntimeException("You messed up calling getYearsActive...");
    }

    String query = "SELECT DISTINCT championship_year "
        + "FROM game, " + table + " WHERE game.id = " + table + ".game AND "
        + table + "." + entity + " = ? ORDER BY championship_year DESC;";

    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      ResultSet rs = prep.executeQuery();

      List<Integer> years = new ArrayList<>();
      while (rs.next()) {
        years.add(rs.getInt(1));
      }

      return years;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public List<GameStats> getSeparateGameStatsForYear(int year, String table, int id) {
    String entity = "";
    int cols;
    if (table.equals("player_stats")) {
      entity = "player";
      cols = PlayerStats.getNumCols();
    } else if (table.equals("team_stats")) {
      entity = "team";
      cols = TeamStats.getNumCols();
    } else {
      throw new RuntimeException("You messed up calling getYearsActive...");
    }

    String query = "SELECT * FROM " + table + ", game "
        + "WHERE " + table + "." + entity + " = ? AND game.id = " + table + ".game "
            + "AND game.championship_year = ? ORDER BY game.date ASC;";


    try (PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setInt(1, id);
      prep.setInt(2, year);
      ResultSet rs = prep.executeQuery();

      List<GameStats> gameStats = new ArrayList<>();

      while (rs.next()) {
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= cols; i++) {
          values.add(rs.getInt(i));
        }
        int gameID = values.get(0);
        int teamID = values.get(1);
        int playerID = values.get(2);
        GameStats toAdd = null;
        if (table.equals("player_stats")) {
          toAdd = new PlayerStats(values, gameID, getTeam(teamID), getPlayer(playerID));
        } else if (table.equals("team_stats")) {
          toAdd = new TeamStats(values, gameID, getTeam(teamID));
        }

        gameStats.add(toAdd);
      }

      return gameStats;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * PLEASE PASS IN "SUM" or "AVG" for type!!!
   * @param type
   * @param year
   * @param table
   * @param id
   * @return
   * @author sjl2
   */
  private GameStats getAggregateGameStatsForYearOfType(String type, int year, String table, int id) {
    StringBuilder query = new StringBuilder("SELECT ");
    String entity = "";
    int cols;
    List<String> nonStatCols = null;
    List<String> statCols = null;

    if (table.equals("player_stats")) {
      nonStatCols = PlayerStats.getNonStatCols();
      statCols = PlayerStats.getStatCols();
      entity = "player";
      cols = PlayerStats.getNumCols();
    } else if (table.equals("team_stats")) {
      nonStatCols = TeamStats.getNonStatCols();
      statCols = TeamStats.getStatCols();
      entity = "team";
      cols = TeamStats.getNumCols();
    } else {
      throw new IllegalArgumentException("You messed up aggregategamestatsforyearoftype!!!");
    }

    for (String nonStat : nonStatCols) {
      query.append(nonStat);
      query.append(", ");
    }

    int i;
    for (i = 0; i < statCols.size() - 1; i++) {
      query.append(type);
      query.append("(");
      query.append(statCols.get(i));
      query.append("), ");
    }

    query.append(type);
    query.append("(");
    query.append(statCols.get(i));
    query.append(") FROM ");
    query.append(table);
    query.append(", game WHERE ");
    query.append(table);
    query.append(".");
    query.append(entity);
    query.append(" = ? AND game.id = ");
    query.append(table);
    query.append(".game AND game.championship_year = ?;");

    try (PreparedStatement prep = conn.prepareStatement(query.toString())) {
      prep.setInt(1, id);
      prep.setInt(2, year);
      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        List<Integer> values = new ArrayList<>();
        for (i = 1; i <= cols; i++) {
          values.add(rs.getInt(i));
        }
        int gameID = values.get(0);
        int teamID = values.get(1);
        int playerID = values.get(2);
        GameStats toReturn = null;
        if (table.equals("player_stats")) {
          toReturn = new PlayerStats(values, gameID, getTeam(teamID), getPlayer(playerID));
        } else if (table.equals("team_stats")) {
          toReturn = new TeamStats(values, gameID, getTeam(teamID));
        }

        return toReturn;
      } else {
        throw new RuntimeException("No aggregate gamestats... this is bad");
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public List<GameStats> getAggregateGameStats(String type, String table, int id) {
    List<GameStats> careerStats = new ArrayList<>();
    List<Integer> yearsActive = getYearsActive(table, id);
    for (int year : yearsActive) {
      GameStats gs = getAggregateGameStatsForYearOfType(type, year, table, id);
      careerStats.add(gs);
    }

    return careerStats;
  }
  
  
  
  /****** SHOT CHART AND HEAT MAP METHODS ******/

  /**
   * Generates list of shot locations
   * @param gameIDs - List of IDs of game to get data for
   * @param entityID - either player or team id
   * @param makes - True if you want makes, false if you want misses
   * @param chartType - "team" or "player"
   * @return
   */
  private List<Location> getShotsForEntityInGames(List<Integer> gameIDs, int entityID, boolean makes, String chartType) {
    int numGames = gameIDs.size();
    String statType = "";
    if (makes) {
      statType = "(type = \"TwoPointer\" OR type = \"ThreePointer\")";
    } else {
      statType = "(type = \"MissedTwoPointer\" OR type = \"MissedThreePointer\")";
    }

    StringBuilder query = new StringBuilder("SELECT x, y FROM stat WHERE ");
    query.append(chartType);
    query.append(" = ? AND ");
    query.append(statType);
    query.append(" AND game in (");
    for (int i = 0; i < numGames -1; i++) {
      query.append("?, ");
    }
    query.append("?);");
    try (PreparedStatement prep = conn.prepareStatement(query.toString())) {
      prep.setInt(1, entityID);
      int i = 2;
      for (int gameID : gameIDs) {
        prep.setInt(i, gameID);
        i++;
      }
      ResultSet rs = prep.executeQuery();

      List<Location> shots = new ArrayList<>();
      while(rs.next()) {
        double x = rs.getDouble(1);
        double y = rs.getDouble(2);
        Location adjustedLoc = Location.adjustForVerticalHalfCourt(x, y);
        shots.add(adjustedLoc);
      }

      return shots;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public List<Integer> getGameIDsInYear(int championshipYear) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM game WHERE championship_year = ?;")) {
      prep.setInt(1, championshipYear);
      ResultSet rs = prep.executeQuery();

      List<Integer> gameIDs = new ArrayList<>();
      while (rs.next()) {
        gameIDs.add(rs.getInt("id"));
      }
      return gameIDs;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  public List<Location> getMakesForEntityInGame(int gameID, int entityID, String chartType) {
    return getShotsForEntityInGames(Arrays.asList(gameID), entityID, true, chartType);
  }

  public List<Location> getMissesForEntityInGame(int gameID, int entityID, String chartType) {
    return getShotsForEntityInGames(Arrays.asList(gameID), entityID, false, chartType);
  }

  public List<Location> getMakesForYear(int championshipYear, int entityID, String chartType) {
    List<Integer> gameIDs = getGameIDsInYear(championshipYear);
    return getShotsForEntityInGames(gameIDs, entityID, true, chartType);
  }

  public List<Location> getMissesForYear(int championshipYear, int entityID, String chartType) {
    List<Integer> gameIDs = getGameIDsInYear(championshipYear);
    return getShotsForEntityInGames(gameIDs, entityID, false, chartType);
  }


  /****** AUTOCORRECT METHODS ******/
  private Trie fillTrie() {
    ArrayList<Character> c = new ArrayList<Character>();
    c.add('@');
    c.add('$');
    Trie t = new Trie(c);

    try {
      PreparedStatement prep = conn.prepareStatement(
          "select name from player;");
      ResultSet r = prep.executeQuery();
      while (r.next()) {
        t.addFirstWord(r.getString(1), StringFormatter.treat(r.getString(1).toLowerCase()));
      }
      prep.close();
      r.close();
      prep = conn.prepareStatement(
          "select name from team;");
      r = prep.executeQuery();
      while (r.next()) {
        t.addFirstWord(r.getString(1), StringFormatter.treat(r.getString(1).toLowerCase()));
      }
      prep.close();
      r.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return t;
  }

  public Trie getTrie() {
    return trie;
  }

  /**
   * Lookup requested name in database, return first matching id.
   * @param name - Name of player or team
   * @param isPlayer - Boolean, representing if name is of player or team
   * @return - int, id of matching player / team
   */
  public List<Integer> searchBarResults(String name, boolean isPlayer) {
    String table = "";
    if (isPlayer) {
      table = "player";
    } else {
      table = "team";
    }

    
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM " + table + " WHERE name = ?;")) {
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();
      
      List<Integer> ids = new ArrayList<Integer>();
      while (rs.next()) {
         ids.add(rs.getInt(1));
      }

      return ids;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }


  /**
   * Used to rank lineups based on offensive and defensive balance.
   * @param playerIDs - Id's of players in lineup
   * @return - int, ranking of combination of players in lineup
   */
  public double lineupRanking(List<Integer> playerIDs) {
    int numPlayerIDs = playerIDs.size();
    int championshipYear = getChampionshipYear(LocalDate.now());
    String statsToExclude = " AND NOT type IN (\"Block\", \"DefensiveRebound\""
        + ", \"Steal\", \"TechnicalFoul\", \"DefensiveFoul\") ";
    StringBuilder query = new StringBuilder(
        "SELECT type, x, y FROM stat as s, game as g "
        + "WHERE g.id = s.game AND g.championship_year = ?"
            + statsToExclude + "AND s.player IN (");
    for (int i = 0; i < numPlayerIDs - 1; i++) {
      query.append("?, ");
    }
    query.append("?);");

    try (PreparedStatement prep = conn.prepareStatement(query.toString())) {
      prep.setInt(1, championshipYear);
      int count = 2;
      for (int playerID : playerIDs) {
        prep.setInt(count, playerID);
        count++;
      }

      ResultSet rs = prep.executeQuery();
      StatBin[][] statBins = new StatBin[FIVE][FIVE];
      for (int i = 0; i < FIVE; i++) {
        StatBin[] bin = new StatBin[FIVE];
        for (int j = 0; j < FIVE; j++) {
          bin[j] = new StatBin();
        }
        statBins[i] = bin;
      }

      while (rs.next()) {
        String stat = rs.getString("type");
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        Location adjustedLoc = Location.adjustForHorizontalHalfCourt(x, y);
        int xBin = (int) Math.floor(adjustedLoc.getX() * 10.0);
        int yBin = (int) Math.floor(adjustedLoc.getY() * 5.0);

        statBins[xBin][yBin].add(stat);
      }

      int qualifiedBins = 0;
      double totalValue = 0;
      for (StatBin[] col : statBins) {
        for (StatBin bin : col) {
          if (bin.exceedsThreshold()) {
            totalValue += bin.getValue();
            qualifiedBins++;
          }
        }
      }

      double scaledValue = totalValue * (qualifiedBins / TOTAL_BINS);
      return scaledValue;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * StatBin class, used to track stats in all 
   * areas of court and sum their values.
   * @author awainger
   */
  private static class StatBin {
    // 10 ~= (10 stats/player/game * 5 players * 5 games) / (25 bins)
    private static final int THRESHOLD = 10;
    private static final double FT_PERCENTAGE = .7;
    private static final double TWO_PT_PERCENTAGE = .4;
    private static final double THREE_PT_PERCENTAGE = .3;

    // Hollinger estimate
    private static final double ASSIST_VALUE = .67;
    // Value of possession based on offensive efficiency
    private static final double POSSESSION_VALUE = 1.0;

    private List<String> stats;
    private double value;

    /**
     * Constructor for new StatBin.
     * @author awainger
     */
    public StatBin() {
      stats = new ArrayList<>();
      value = 0;
    }
    
    public boolean exceedsThreshold() {
      return stats.size() > THRESHOLD;
    }

    /**
     * Adds a stat to the list, adds stat's value to bin.
     * @param stat - String, stat to add
     */
    public void add(String stat) {
      stats.add(stat);
      value += getStatValue(stat);
    }

    /**
     * Returns the value of the bin.
     * @return Double, estimate of the value of the stats in this bin
     */
    public double getValue() {
        return value;
    }

    /**
     * Determines value of various types of stats.
     * @param stat -String, indicating type of stat 
     * @return double, value of that stat
     */
    private double getStatValue(String stat) {
      switch (stat) {
      case "FreeThrow":
        return 1.0;
      case "MissedFreeThrow":
        return -FT_PERCENTAGE / (1.0 - FT_PERCENTAGE);
      case "TwoPointer":
        return 2.0;
      case "MissedTwoPointer":
        return -2.0 * (TWO_PT_PERCENTAGE / (1.0 - TWO_PT_PERCENTAGE));
      case "ThreePointer":
        return 3.0;
      case "MissedThreePointer":
        return -3.0 * (THREE_PT_PERCENTAGE / (1.0 - THREE_PT_PERCENTAGE));
      case "OffensiveFoul":
        return -POSSESSION_VALUE;
      case "OffensiveRebound":
        return POSSESSION_VALUE;
      case "Turnover":
        return -POSSESSION_VALUE;
      case "Assist":
        return ASSIST_VALUE;
      default:
        return 0;
      }
    }
  }
}
