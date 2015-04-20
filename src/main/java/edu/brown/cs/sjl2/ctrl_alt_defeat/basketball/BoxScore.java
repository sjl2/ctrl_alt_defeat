package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class BoxScore {
  private Map<Integer, GameStats> playerStats;
  private GameStats teamStats;
  private Team team;
  private boolean isHome;
  private DBManager db;

  public BoxScore(DBManager db, Game game, Team team) {
    this.db = db;
    this.team = team;
    Collection<Player> players = team.getPlayers();
    playerStats = new HashMap<>();

    for (Player p : players) {
      GameStats gs = new GameStats(game, team, p);
      try {
        db.storeGameStats(gs);
      } catch (GameException e) {
        throw new RuntimeException(e.getMessage());
      }
      playerStats.put(p.getID(), gs);
    }

    teamStats = GameStats.TeamGameStats(game, team);
  }

  /**
   * Static Instantiator of a stored box score.
   * @param db
   * @param game
   * @param home
   * @return Returns a Boxscore from a past game.
   */
  public static BoxScore OldBoxScore(DBManager db, Game game, Team team) {
    // TODO
    return null;
  }

  public GameStats getPlayerStats(Player p) {
    return playerStats.get(p.getID());
  }

  public GameStats getTeamStats() {
    return teamStats;
  }

  public Team getTeam() {
    return team;
  }

  public boolean isHome() {
    return isHome;
  }

  public int getScore() {
    return teamStats.getFreeThrows()
        + (teamStats.getTwoPointers() * 2)
        + (teamStats.getThreePointers() * 3);
  }

  public int getFouls() {
    return teamStats.getPersonalFouls();
  }

  /**
   * Update the DB with the latest stats stored within the box score.
   */
  public void updateDB() {
    db.updateBoxscore(playerStats.values());
  }

}
