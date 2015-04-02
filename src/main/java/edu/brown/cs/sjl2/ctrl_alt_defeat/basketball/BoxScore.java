package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class BoxScore {
  private Map<Player, GameStats> playerStats;
  private GameStats teamStats;

  public GameStats getPlayerStats(Player p) {
    return playerStats.get(p).copy();
  }

  public GameStats getTeamStats() {
    return teamStats.copy();
  }


}
