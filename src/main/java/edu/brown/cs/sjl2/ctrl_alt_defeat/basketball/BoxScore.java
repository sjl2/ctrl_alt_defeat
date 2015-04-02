package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class BoxScore {
  private Map<Player, GameStats> playerStats;
  private GameStats teamStats;

  public BoxScore(Team team) {
    Collection<Player> players = team.getPlayers();

    for (Player p : players) {
      playerStats.put(p, new GameStats());
    }

    teamStats = new GameStats();
  }

  public GameStats getPlayerStats(Player p) {
    return playerStats.get(p).copy();
  }

  public GameStats getTeamStats() {
    return teamStats.copy();
  }


}
