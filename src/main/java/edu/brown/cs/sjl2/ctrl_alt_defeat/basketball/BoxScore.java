package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class BoxScore {
  private static final int TEAM = 0;

  private Map<Integer, GameStats> playerStats;
  private Team team;
  private DBManager db;

  public BoxScore(DBManager db, Game game, Team team) {

    Collection<Player> players = team.getPlayers();
    playerStats = new HashMap<>();

    for (Player p : players) {
      GameStats gs = new GameStats(game.getID(), team, p);
      playerStats.put(p.getID(), gs);
    }

    playerStats.put(0, GameStats.newTeamGameStats(game, team));

    // Initialize all of the gamestats in the db
    Collection<GameStats> stats = playerStats.values();
    db.saveBoxScore(stats);


    this.team = team;
    this.db = db;
  }

  private BoxScore(DBManager db, Team team, Map<Integer, GameStats> playerStats) {
    this.playerStats = playerStats;
    this.db = db;
    this.team = team;
  }

  /**
   * Static Instantiator of a stored box score.
   * @param db
   * @param game
   * @param home
   * @return Returns a Boxscore from the database
   * @throws GameException
   */
  public static BoxScore getOldBoxScore(DBManager db, int gameID, Team team)
      throws GameException {

    Map<Integer, GameStats> playerStats = db.loadBoxScore(gameID, team);

    return new BoxScore(db, team, playerStats);
  }

  public GameStats getPlayerStats(Player p) {
    return playerStats.get(p.getID());
  }

  public List<GameStats> getAllPlayerStats() {
    List<GameStats> allStats = new ArrayList<>();
    for (int playerID : playerStats.keySet()) {
      if (playerID != 0) {
        allStats.add(playerStats.get(playerID));
      }
    }
    return allStats;
  }

  public GameStats getTeamStats() {
    return playerStats.get(TEAM);
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
    db.updateBoxscore(playerStats.values());
  }

  public void undoStat(Stat s) {
    s.undo(playerStats.get(s.getPlayer().getID()));
    s.undo(getTeamStats());
    updateDB();
  }


}
