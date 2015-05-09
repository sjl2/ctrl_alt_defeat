package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.TeamStats;

/**
 * Class representing a boxscore for a single team in a game.
 *
 * @author sjl2
 *
 */
public class BoxScore {

  private Map<Integer, PlayerStats> playerStats;
  private TeamStats teamStats;
  private Team team;
  private DBManager db;

  /**
   * Constructs a boxscore for a team in a game.
   *
   * @param db
   *          The database to store the boxscore.
   * @param game
   *          The game associated with the boxscore.
   * @param team
   *          The team associated with the boxscore.
   */
  public BoxScore(DBManager db, Game game, Team team) {

    Collection<Player> players = team.getPlayers();
    playerStats = new HashMap<>();

    for (Player p : players) {
      PlayerStats gs = new PlayerStats(game.getID(), team, p);
      playerStats.put(p.getID(), gs);
    }

    this.teamStats = new TeamStats(game.getID(), team);

    // Initialize all of the game stats in the db
    Collection<PlayerStats> stats = playerStats.values();
    db.createBoxScore(stats, teamStats);

    this.team = team;
    this.db = db;
  }

  private BoxScore(DBManager db, Team team,
      Map<Integer, PlayerStats> playerStats, TeamStats teamStats) {

    this.playerStats = playerStats;
    this.teamStats = teamStats;
    this.db = db;
    this.team = team;
  }

  /**
   * Static Instantiator of a stored box score.
   *
   * @param db
   *          The database to retrieve data from.
   * @param gameID
   *          The id of the game.
   * @param team
   *          The boxscore's team.
   * @return Returns a Boxscore from the database
   * @throws GameException
   *           Throws a game exception if a boxscore could not be obtained.
   */
  public static BoxScore getOldBoxScore(DBManager db, int gameID, Team team)
    throws GameException {

    Map<Integer, PlayerStats> playerStats = db.getPlayerStats(gameID, team);
    TeamStats teamStats = db.getTeamStats(gameID, team);
    return new BoxScore(db, team, playerStats, teamStats);
  }

  /**
   * Get Player Stats for a player.
   *
   * @param p
   *          The player to find stats for.
   * @return Returns the player stats for a player. Null if the player is not in
   *         the boxscore.
   */
  public PlayerStats getPlayerStats(Player p) {
    return playerStats.get(p.getID());
  }

  /**
   * Getter for all of the player stats in the boxscore.
   *
   * @return Returns a list of player stats for each player.
   */
  public List<PlayerStats> getAllPlayerStats() {
    List<PlayerStats> allStats = new ArrayList<>();
    for (int playerID : playerStats.keySet()) {
      if (playerID != 0) {
        allStats.add(playerStats.get(playerID));
      }
    }
    return allStats;
  }

  /**
   * Getter for the teamstats of the boxscore.
   *
   * @return Returns the teamstats of the team.
   */
  public TeamStats getTeamStats() {
    return teamStats;
  }

  /**
   * Getter for the team.
   *
   * @return Returns the team object for the boxscore.
   */
  public Team getTeam() {
    return team;
  }

  /**
   * Getter for the team's score in the game.
   *
   * @return Returns an int of the points scored.
   */
  public int getScore() {
    return getTeamStats().getFreeThrows()
        + (getTeamStats().getTwoPointers() * 2)
        + (getTeamStats().getThreePointers() * 3);
  }

  /**
   * Getter for the team fouls.
   *
   * @return Returns the number of team fouls.
   */
  public int getFouls() {
    return getTeamStats().getPersonalFouls();
  }

  /**
   * Adds a stat s to the boxscore.
   *
   * @param s
   *          The stat to be added.
   */
  public void addStat(Stat s) {
    s.execute(playerStats.get(s.getPlayer().getID()));
    s.execute(getTeamStats());
    updateDB();
  }

  /**
   * Update the DB with the latest stats stored within the box score.
   */
  private void updateDB() {
    db.updateBoxscore(playerStats.values(), teamStats);
  }

  /**
   * Undoes a stat from the boxscore.
   *
   * @param s
   *          The stat to undo.
   */
  public void undoStat(Stat s) {
    s.undo(playerStats.get(s.getPlayer().getID()));
    s.undo(getTeamStats());
    updateDB();
  }

}
