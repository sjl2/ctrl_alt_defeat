package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class BoxScore {
  private Map<Player, GameStats> playerStats;
  private GameStats teamStats;
  private Team team;
  private boolean isHome;
  private DBManager db;
  private Game game;

  public BoxScore(DBManager db, Game game, boolean isHome, Team team) {
    this.db = db;
    this.game = game;
    this.isHome = isHome;
    this.team = team;
    Collection<Player> players = team.getPlayers();

    for (Player p : players) {
      playerStats.put(p, new GameStats(db.getNextID("boxscore"), p, game));
    }

    teamStats = GameStats.TeamGameStats(game);
  }

  public GameStats getPlayerStats(Player p) {
    return playerStats.get(p).copy();
  }

  public GameStats getTeamStats() {
    return teamStats.copy();
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

}
