package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class BoxScore {
  private Map<Player, GameStats> playerStats;
  private Map<Team, GameStats> teamStats;

  public GameStats getPlayerStats(Player p) {
    return null;
  }

  public int getTeamPoints() {
    return -1;
  }

  public int getTeamRebounds() {
    return -1;
  }

  public int getTeamAssists() {
    return -1;
  }

  public int getTeamSteals() {
    return -1;
  }

  public int getTeamBlock() {
    return -1;
  }

  public int getTeamTurnovers() {
    return -1;
  }

  public int getTeamFouls() {
    return -1;
  }

  public int getTeamShots() {
    return -1;
  }

  public double getTeamFGPercentage() {
    return -1;
  }

  public double getTeamFreeThrowPercentage() {
    return -1;
  }

  public double getTeamThreePointPercentage() {
    return -1;
  }

  public Map<Player, GameStats> getStats() {
    return playerStats;
  }

}
