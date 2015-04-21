package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class BoxScore {
  private Map<Integer, GameStats> playerStats;
  private GameStats teamStats;
  private Team team;
  private boolean isHome;
  private DBManager db;

  public BoxScore(DBManager db, Game game, Team team) {

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

    this.team = team;
    this.isHome = game.isHome(team);
    this.db = db;
  }

  private BoxScore(Map<Integer, GameStats> playerStats, Game game, Team team) {

  }

  /**
   * Static Instantiator of a stored box score.
   * @param db
   * @param game
   * @param home
   * @return Returns a Boxscore from the database
   */
  public static BoxScore getBoxScore(DBManager db, Game game, Team team) {
    Collection<Player> players = team.getPlayers();
    Map<Integer, GameStats> playerStats = new HashMap<>();

    for (Player player : players) {
      try {
        playerStats.put(player.getID(), db.loadGameStats(game, team, player));
      } catch (GameException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return new BoxScore(playerStats, game, team);
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

  public void addStat(Stat s) {
    s.execute(playerStats.get(s.getPlayer().getID()));
    s.execute(teamStats);
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
    s.undo(teamStats);
    updateDB();
  }


}
