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

public class BoxScore {

  private Map<Integer, PlayerStats> playerStats;
  private TeamStats teamStats;
  private Team team;
  private DBManager db;

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
   * @param db
   * @param gameID
   * @param team
   * @return Returns a Boxscore from the database
   * @throws GameException
   */
  public static BoxScore getOldBoxScore(DBManager db, int gameID, Team team)
      throws GameException {

    Map<Integer, PlayerStats> playerStats = db.getPlayerStats(gameID, team);
    TeamStats teamStats = db.getTeamStats(gameID, team);
    return new BoxScore(db, team, playerStats, teamStats);
  }

  public PlayerStats getPlayerStats(Player p) {
    return playerStats.get(p.getID());
  }

  public List<PlayerStats> getAllPlayerStats() {
    List<PlayerStats> allStats = new ArrayList<>();
    for (int playerID : playerStats.keySet()) {
      if (playerID != 0) {
        allStats.add(playerStats.get(playerID));
      }
    }
    return allStats;
  }

  public TeamStats getTeamStats() {
    return teamStats;
  }

  public Team getTeam() {
    return team;
  }

  public int getScore() {
    return getTeamStats().getFreeThrows()
        + (getTeamStats().getTwoPointers() * 2)
        + (getTeamStats().getThreePointers() * 3);
  }

  public int getFouls() {
    return getTeamStats().getPersonalFouls();
  }

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

  public void undoStat(Stat s) {
    s.undo(playerStats.get(s.getPlayer().getID()));
    s.undo(getTeamStats());
    updateDB();
  }


}
