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
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
  public static final int TOTAL_BINS = 16;

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
   * Getter for a new PlaymakerDB
   * @return Returns a new PlaymakerDB
   */
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

  /**
   * Getter for the connection.
   * @return Returns the connection of the datbase.
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * Clears the database.
   */
  public void clearDatabase() {
    List<String> tables =
        Arrays.asList("play_detail", "play_detail_ball", "play", "team_stats",
            "player_stats", "stat", "user", "game", "player", "team");

    for (String table : tables) {
      String query = "DELETE FROM " + table + ";";
      try (PreparedStatement prep = conn.prepareStatement(query)) {
        prep.execute();
      } catch (SQLException e) {
        throw new RuntimeException("Failed to clear db. " + e.getMessage());
      }
    }
  }


  /* LOGIN METHODS */

  /**
   * Checks for whether the string username exists.
   * @param username The string username to check.
   * @return Returns true if the username exists.
   */
  public boolean doesUsernameExist(String username) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT name FROM user WHERE name = ? LIMIT 1;")) {
      prep.setString(1, username);
      ResultSet rs = prep.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Returns a list of all the usernames.
   * @return Returns a list of all the usernames in the database.
   */
  public List<String> getUsernames() {
    String query = "SELECT name FROM user";
    List<String> users = new LinkedList<>();
    try(PreparedStatement prep = conn.prepareStatement(query)) {
      ResultSet rs = prep.executeQuery();
      while(rs.next()) {
        String name = rs.getString(1);
        users.add(name);
      }
      rs.close();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }

    return users;
  }

  /**
   * Updates a user with new information.
   * @param oldUsername The old username
   * @param newUsername The new username
   * @param newPassword The new password
   */
  public void updateUser(
      String oldUsername,
      String newUsername,
      String newPassword) {

    String query = "UPDATE user SET name = ?, password = ? where name = ?;";
    try(PreparedStatement prep = conn.prepareStatement(query)) {
      prep.setString(1, newUsername);
      prep.setString(2, Integer.valueOf(newPassword.hashCode()).toString());
      prep.setString(3, oldUsername);

      prep.execute();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Checks whether a password works.
   * @param username The username
   * @param password The password
   * @return Returns an int with the clearance level of the user. -1 if the
   * password or user does not check out.
   */
  public int checkPassword(String username, String password) {
    if(!doesUsernameExist(username)) {
      return -1;
    }
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT password, clearance FROM user WHERE name = ? LIMIT 1;")) {
      prep.setString(1, username);
      ResultSet rs = prep.executeQuery();
      if(rs.next()) {
        int dbPassword = rs.getInt(1);
        int clearance = rs.getInt(2);
        if(dbPassword == password.hashCode()) {
          return clearance;
        } else {
          return -1;
        }
      } else {
        return -1;
      }
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e.getMessage());
    }
  }


  /* TEAM AND PLAYER METHODS */
  /**
   * Gets player from database.
   * @param id  Int, corresponding to player to get
   * @return Returns a player Player, with fields populated from db info
   *
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

  /**
   * Get My Team from the database.
   * @return Returns the team instance associated with my team. If there are
   * multiple, the first one is returned.
   * @throws DashboardException Throws dashboard exception if my team doesn't
   * exist. One will need to be created.
   */
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

  /**
   * Get the players for the team team.
   * @param team The team for which to get players.
   * @return Returns a collection of players for the team.
   */
  public Collection<Player> getTeamPlayers(Team team) {
    return getTeamPlayers(team.getID());
  }

  /**
   * Get the team players for an id of a team.
   * @param id The id of the team.
   * @return Returns the collection of players.
   */
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

  /**
   * Updates a boxscore in the database with updated stats.
   * @param playerStats PlayerStats to populate the database with.
   * @param ts The teamstats of the boxscore.
   */
  public void updateBoxscore(Collection<PlayerStats> playerStats, TeamStats ts) {
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
        for (PlayerStats gs : playerStats) {
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

  /**
   * Get PlayerStats for a game and team.
   * @param gameID The id of the game.
   * @param team The team to find stats for.
   * @return Returns a Map of player id to the player stats for that player in
   * that game.
   * @throws GameException Throws new game exception if the player stats cannot
   * be obtained.
   */
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

        allGameStats.put(
            values.get(2),
            new PlayerStats(values, gameID, team, getPlayer(values.get(2))));

        values.clear();
      }

    } catch (SQLException e) {
      String message = "Failed to load player game stats: " + e.getMessage();
      throw new GameException(message);
    }

    return allGameStats;
  }

  /**
   * Gets teamstats for a team in a game.
   * @param gameID The id of the game.
   * @param team The team of the stats
   * @return Returns the appropriate teamstats
   * @throws GameException Throws a new game exception if teamstats can't be
   * found.
   */
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

  /**
   * Creates a boxscore for the player stats and team stats inputted.
   * @param stats The player stats to initialize in the database.
   * @param ts The team stats to initialize in the database.
   */
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

  /**
   * Creates a stat in the database.
   * @param s The stat to insert
   * @param game The game of the stat
   */
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

  /**
   * Updates a stat in the database.
   * @param s The updated stat with the same id as the old stat.
   */
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

  /**
   * Deletes a stat from the database.
   * @param s The stat to delete.
   * @throws GameException Throws a new game exception if stat does not match
   * a stat in the database or some other failure caused the stat not to be
   * deleted.
   */
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

  /**
   * Gets the next available int id for the table.
   * @param table The table in question.
   * @return The int of the next available id in the table.
   */
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
   * Create's a team object and stores it in the database.
   * @param name The name of the team.
   * @param coach The name of the coach
   * @param primary The string of the primary color
   * @param secondary The string of the secondary color
   * @param myTeam The boolean for whether the team is myTeam
   * @return Returns the new team object, cached and stored in the db.
   */
  public Team createTeam(String name, String coach, String primary,
      String secondary, boolean myTeam) {

    Team t = tf.getTeam(getNextID("team"), name, coach, primary, secondary);
    createTeam(t, myTeam);

    return t;
  }

  /**
   * Updates a team in the datbase with the new information.
   * @param id The id of the team to be updated.
   * @param name The name of the team
   * @param coach The name of the coach
   * @param primary The primary color
   * @param secondary The secondary color
   */
  public void updateTeam(int id, String name, String coach, String primary,
      String secondary) {

    Team t = tf.getTeam(id, name, coach, primary, secondary);
    updateTeam(t);
  }

  /**
   * Updates the id of team in the database with team data.
   * @param team The updated team
   */
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

  /**
   * Creates a player in the database and returns the object.
   * @param name The name of the player
   * @param teamID the int id of the team
   * @param number the number of the player
   * @param curr the boolean for whether the player is current
   * @return Returns new player object, stored and cached.
   */
  public Player createPlayer(
      String name,
      int teamID,
      int number,
      boolean curr) {

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

  /**
   * Updates the player in the database.
   * @param id The id of the player ot be updated
   * @param name The name of the player
   * @param teamID The id of the team.
   * @param number The player's number
   * @param curr The boolean if the player is currently playing.
   */
  public void updatePlayer(
      int id,
      String name,
      int teamID,
      int number,
      boolean curr) {

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
  /**
   * Getter for a game link.
   * @param id The id of the game.
   * @return Returns the link for this game.
   */
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

  /**
   * Get all teams in the database in the form of links.
   * @return Returns a list of all the links of teams in the database.
   */
  public List<Link> getAllTeams() {
    return getTeamLinks(true);
  }

  /**
   * Getter of all opposing teams in the database.
   * @return Returns a list of opposing teams (all teams except my team)
   * @author sjl2
   */
  public List<Link> getOpposingTeams() {
    return getTeamLinks(false);
  }

  /**
   * Creates a game in the database.
   * @param game The game to insert into the database.
   */
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

  /**
   * Creates a game in the database returning it's id.
   * @param date The date of the game.
   * @param home The home team id.
   * @param away The away team id.
   * @return Returns the id of the game in the database.
   */
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

  /**
   * Getter for the championship year of the date. Lumps date into the season.
   * @param date The date to find the championship year for.
   * @return Returns the int of the year.
   */
  public static int getChampionshipYear(LocalDate date) {
    if (date.getMonthValue() < SEVEN) {
      return date.getYear();
    } else {
      return date.getYear() + 1;
    }
  }

  /**
   * Getter for a GameView based on a game id.
   * @param id The game id
   * @return Returns the view of the old game.
   * @throws DashboardException Throws a Dashboard Exception if there is no
   * game in the database.
   * @throws GameException Throws new game exception if view is not
   * instantiated
   */
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
      throw new DashboardException("No game in the database with the id " + id);
    }
    return g;
  }

  /**
   * Deletes a game with the game id.
   * @param id The id of the game to delete.
   */
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

  /**
   * Deletes a player with the player id id.
   * @param id The id of the player to delete.
   * @return Returns true if the player is successfully deleted, false if not.
   */
  public boolean deletePlayer(int id) {
    String query = "SELECT count(*) FROM stat WHERE player = ?;";
    String query2 = "DELETE FROM player WHERE id = ?;";
    try (PreparedStatement prep = conn.prepareStatement(query);
         PreparedStatement prep2 = conn.prepareStatement(query2)) {
      prep.setInt(1, id);

      ResultSet rs = prep.executeQuery();

      if (rs.next()) {
        int count = rs.getInt(1);
        if (count != 0) {
          return false;
        }
      }

      rs.close();

      Player p = getPlayer(id);
      Team t = getTeam(p.getTeamID());
      t.removePlayer(p);
      pf.removePlayer(id);
      prep2.setInt(1, id);
      prep2.executeUpdate();
    } catch(SQLException e) {
      String message = "Failed to delete player from player table."
          + e.getMessage();
      throw new RuntimeException(message);
    }

    return true;
  }

  /****** PLAYER AND TEAM STAT PAGE POPULATION METHODS ******/

  /**
   * Getter for the years active for a team or player.
   * @param table The table team_stats for teams or player_stats for players
   * @param id the id of the team or player
   * @return Returns a list of years for the team or player.
   */
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

  /**
   * Getter for gamestats from a certain years.
   * @param year The year of the stats.
   * @param table Player_Stats for player and team_stats for teams
   * @param id The id of the player or team.
   * @return Returns a list of the GameStats for that year for that player
   * or team.
   */
  public List<GameStats> getSeparateGameStatsForYear(int year, String table,
      int id) {

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
        + "WHERE " + table + "." + entity + " = ? AND game.id = "
          + table + ".game "
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
          toAdd = new PlayerStats(
              values,
              gameID,
              getTeam(teamID),
              getPlayer(playerID));

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
   * @param type The type of aggregate
   * @param year The year of the stats.
   * @param table The Table player_stats for players, team_stats for teams
   * @param id The id of the player or team
   * @return Returns a GameStats of the aggregrate data for that year.
   *
   * @author sjl2
   */
  private GameStats getAggregateGameStatsForYearOfType(
      String type,
      int year,
      String table,
      int id) {

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
      String message = "You messed up aggregategamestatsforyearoftype!!!";
      throw new IllegalArgumentException(message);
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

  /**
   * Getter for aggregate Stats for a player or team.
   * @param type The type of aggregate stats
   * @param table player_stats for players, team_stats for teams
   * @param id The id of the player or team
   * @return Returns a list of aggregate stats for all active years.
   */
  public List<GameStats> getAggregateGameStats(
      String type,
      String table,
      int id) {

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
   * @return Returns a list of locations of the shots.
   */
  private List<Location> getShotsForEntityInGames(
      List<Integer> gameIDs,
      List<Integer> entityIDs,
      boolean makes,
      String chartType) {

    int numGames = gameIDs.size();
    int numEntities = entityIDs.size();
    String statType = "";
    if (makes) {
      statType = "(type = \"TwoPointer\" OR type = \"ThreePointer\")";
    } else {
      statType =
          "(type = \"MissedTwoPointer\" OR type = \"MissedThreePointer\")";
    }

    StringBuilder query = new StringBuilder("SELECT x, y FROM stat WHERE ");
    query.append(chartType);
    query.append(" in (");

    if (numEntities == 0) {
      query.append(")");
    } else {
      for (int i = 0; i < numEntities - 1; i++) {
        query.append("?, ");
      }
      query.append("?)");
    }

    query.append(" AND ");
    query.append(statType);
    query.append(" AND game in (");
    for (int i = 0; i < numGames -1; i++) {
      query.append("?, ");
    }
    query.append("?);");

    try (PreparedStatement prep = conn.prepareStatement(query.toString())) {
      int i = 1;
      for (int entity : entityIDs) {
        prep.setInt(i, entity);
        i++;
      }
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

  /**
   * Getter for a list of game ids for a year.
   * @param championshipYear The championship  year of the season.
   * @return Returns a list of game ids for that season.
   */
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

  /**
   * Getter for the list of the last 5 gameIDs.
   * @return Returns a list of game ids for the last five ids.
   */
  public List<Integer> getLast5GameIDs() {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT g.id FROM game as g, team as t "
        + "WHERE (g.home = t.id OR g.away = t.id) "
        + "AND t.my_team = \"1\" ORDER BY g.date DESC LIMIT 5;")) {
      ResultSet rs = prep.executeQuery();
      List<Integer> gameIDs = new ArrayList<>();
      while (rs.next()) {
        gameIDs.add(rs.getInt(1));
      }
      return gameIDs;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Getter for the the makes in a game.
   * @param gameIDs The game ids in question
   * @param entityIDs The ids of the entitites
   * @param chartType The type of the chart
   * @return Returns a list of locations of the makes.
   */
  public List<Location> getMakesForEntityInGames(
      List<Integer> gameIDs, List<Integer> entityIDs, String chartType) {

    return getShotsForEntityInGames(gameIDs, entityIDs, true, chartType);
  }

  /**
   * Getter for the misses in a list of games.
   * @param gameIDs The list of game ids
   * @param entityIDs List of entity ids
   * @param chartType The type of chart
   * @return Returns a list of locations for the misses
   */
  public List<Location> getMissesForEntityInGames(
      List<Integer> gameIDs, List<Integer> entityIDs, String chartType) {

    return getShotsForEntityInGames(gameIDs, entityIDs, false, chartType);
  }

  /**
   * Getter for the makes in a year.
   * @param championshipYear The championship year
   * @param entityIDs The ids of the entities
   * @param chartType The chart type
   * @return Returns the makes for the years
   */
  public List<Location> getMakesForYear(
      int championshipYear, List<Integer> entityIDs, String chartType) {
    List<Integer> gameIDs = getGameIDsInYear(championshipYear);
    return getShotsForEntityInGames(gameIDs, entityIDs, true, chartType);
  }

  /**
   * Getter for the misses of of the year.
   * @param championshipYear The championship year.
   * @param entityIDs The ids of the entities
   * @param chartType The type of chart.
   * @return The list of locations for misses.
   */
  public List<Location> getMissesForYear(
      int championshipYear, List<Integer> entityIDs, String chartType) {
    List<Integer> gameIDs = getGameIDsInYear(championshipYear);
    return getShotsForEntityInGames(gameIDs, entityIDs, false, chartType);
  }


  /****** AUTOCORRECT METHODS ******/
  /**
   * Initializes the trie with players and teams.
   * @return Returns the trie.
   */
  public Trie fillTrie() {
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

      return t;
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  /**
   * Lookup requested name in database, return first matching id.
   * @param name - Name of player or team
   * @return - int, id of matching player / team
   */
  public List<List<Integer>> searchBarResults(String name) {
    try (PreparedStatement prep = conn.prepareStatement(
        "SELECT id FROM player WHERE name = ?;");
        PreparedStatement prep2 = conn.prepareStatement(
        "SELECT id FROM team WHERE name = ?;")) {
      prep.setString(1, name);
      ResultSet rs = prep.executeQuery();

      List<Integer> playerIds = new ArrayList<Integer>();
      while (rs.next()) {
        playerIds.add(rs.getInt(1));
      }

      prep2.setString(1, name);
      ResultSet rs2 = prep2.executeQuery();
      List<Integer> teamIds = new ArrayList<Integer>();
      while (rs2.next()) {
        teamIds.add(rs2.getInt(1));
      }

      List<List<Integer>> bothIds = new ArrayList<List<Integer>>();
      bothIds.add(playerIds);
      bothIds.add(teamIds);
      return bothIds;
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
    String statsToExclude = " AND type IN (\"FreeThrow\", \"MissedFreeThrow\","
        + " \"TwoPointer\", \"MissedTwoPointer\", \"ThreePointer\","
        + " \"MissedThreePointer\") ";
    StringBuilder query = new StringBuilder(
        "SELECT g.id, type, x, y FROM stat as s, game as g "
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
      StatBin[][] statBins = new StatBin[FOUR][FOUR];
      for (int i = 0; i < FOUR; i++) {
        StatBin[] bin = new StatBin[FOUR];
        for (int j = 0; j < FOUR; j++) {
          bin[j] = new StatBin();
        }
        statBins[i] = bin;
      }

      Set<Integer> games = new HashSet<>();
      int numTotalStats = 0;
      while (rs.next()) {
        games.add(rs.getInt(1));
        numTotalStats++;
        String stat = rs.getString("type");
        double x = rs.getDouble("x");
        double y = rs.getDouble("y");
        if (x >= 0 && y >= 0) {
          Location adjustedLoc = Location.adjustForHorizontalHalfCourt(x, y);
          int xBin = (int) Math.floor(adjustedLoc.getX() * 10.0);
          int yBin = (int) Math.floor(adjustedLoc.getY() * 4.0);
          if (xBin < FOUR) {
            statBins[xBin][yBin].add(stat);
          }
        }
      }

      double totalValue = 0.0;
      for (StatBin[] col : statBins) {
        for (StatBin bin : col) {
          if (bin.exceedsThreshold(games.size(), numTotalStats)) {
            totalValue += bin.getValue();
          }
        }
      }

      double scaledValue = totalValue / TOTAL_BINS;
      if (scaledValue == 0) {
        return 0;
      } else if (scaledValue < 0) {
        return -25 * Math.log10(100 * (-scaledValue) + 1) + 50;
      } else {
        return 25 * Math.log10((100 * scaledValue) + 1) + 50;
      }
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
    private static final double FT_PERCENTAGE = .6;
    private static final double TWO_PT_PERCENTAGE = .45;
    private static final double THREE_PT_PERCENTAGE = .33;

    private int numStats;
    private double value;
    private double possibleValue;

    /**
     * Constructor for new StatBin.
     * @author awainger
     */
    public StatBin() {
      numStats = 0;
      value = 0;
      possibleValue = 0;
    }

    /**
     * Returns whether or not the bin meets the minimum requirements.
     * @param numGames - int, number of games we are calculating over
     * @param numTotalStats - int, number of stats we are calculating over
     * @return Boolean, whether or not the bin qualifies for the calculation
     */
    public boolean exceedsThreshold(int numGames, int numTotalStats) {
      return numStats >= ((double) numTotalStats / (double) (numGames * TOTAL_BINS));
    }

    /**
     * Adds a stat to the list, adds stat's value to bin.
     * @param stat - String, stat to add
     */
    public void add(String stat) {
      numStats++;
      value += getStatValue(stat);
      possibleValue += Math.abs(getStatValue(stat));
    }

    /**
     * Returns the ratio of earned to possible points for bin.
     * @return Double, estimate of the value of the stats in this bin
     */
    public double getValue() {
      if (numStats > 0) {
        return value / possibleValue;
      } else {
        return 0;
      }
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
      default:
        throw new RuntimeException("Default case should never be reached");
      }
    }
  }
}
