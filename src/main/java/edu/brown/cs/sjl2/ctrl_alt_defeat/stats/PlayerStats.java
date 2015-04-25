package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;

public class PlayerStats implements GameStats {
  private static final int TWO_POINTS = 2;
  private static final int THREE_POINTS = 3;

  private static final String[] COLS = {
    "game", "team", "player", "MIN", "TwoPM", "TwoPA", "ThreePM", "ThreePA",
    "FTM", "FTA", "ORB", "DRB", "AST", "STL", "BLK", "TOV", "OF", "DF"
  };

  private Player player;
  private Team team;
  private int game;
  private Multiset<String> stats;


  public PlayerStats(int game, Team team, Player player) {
    this.player = player;
    this.team = team;
    this.game = game;
    this.stats = HashMultiset.create();

    for (String s : getCols()) {
      stats.setCount(s, 0);
    }

    stats.setCount("game", game);
    stats.setCount("team", team.getID());
    stats.setCount("player", player.getID());

  }

  public PlayerStats(List<Integer> values, int gameID, Team team) {
    this.game = gameID;
    this.team = team;

    int playerID = values.get(2);
    this.player = team.getPlayerById(playerID);

    this.stats = HashMultiset.create();

    for (int i = 0; i < COLS.length; i++) {
      stats.setCount(COLS[i], values.get(i));
    }
  }

  public static List<String> getCols() {
    return Arrays.asList(COLS);
  }

  public static int getNumCols() {
    return COLS.length;
  }

  public List<Integer> getValues() {
    List<Integer> values = new ArrayList<>();

    for (String col : getCols()) {
      values.add(stats.count(col));
    }
    return values;
  }

  /**
   * Getter for the Player that these stats refer to.
   * @return
   */
  public Player getPlayer() {
    return player;
  }

  public Team getTeam() {
    return team;
  }
  /**
   * Getter for the Game of these stats.
   * @return
   */
  public int getGameID() {
    return game;
  }

  @Override
  public int getMinutes() {
    return stats.count("MIN");
  }

  @Override
  public void addMinutes(int minutes) {
    if (minutes < 0) {
      stats.remove("MIN", -1 * minutes);
    } else {
      stats.add("MIN", minutes);
    }
  }

  @Override
  public int getTwoPointers() {
    return stats.count("TwoPM");
  }

  @Override
  public void addTwoPointers(int twoPointers) {
    if (twoPointers < 0) {
      stats.remove("TwoPM", -1 * twoPointers);
    } else {
      stats.add("TwoPM", twoPointers);
    }
  }

  @Override
  public int getTwoPointersA() {
    return stats.count("TwoPA");
  }

  @Override
  public void addTwoPointersA(int twoPointersA) {
    if (twoPointersA < 0) {
      stats.remove("TwoPA", -1 * twoPointersA);
    } else {
      stats.add("TwoPA", twoPointersA);
    }
  }

  @Override
  public int getThreePointers() {
    return stats.count("ThreePM");
  }

  @Override
  public void addThreePointers(int threePointers) {
    if (threePointers < 0) {
      stats.remove("ThreePM", -1 * threePointers);
    } else {
      stats.add("ThreePM", threePointers);
    }
  }

  @Override
  public int getThreePointersA() {
    return stats.count("ThreePA");
  }

  @Override
  public void addThreePointersA(int threePointersA) {
    if (threePointersA < 0) {
      stats.remove("ThreePA", -1 * threePointersA);
    } else {
      stats.add("ThreePA", threePointersA);
    }
  }

  @Override
  public int getFreeThrows() {
    return stats.count("FTM");
  }

  @Override
  public void addFreeThrows(int freeThrows) {
    if (freeThrows < 0) {
      stats.remove("FTM", -1 * freeThrows);
    } else {
      stats.add("FTM", freeThrows);
    }
  }

  @Override
  public int getFreeThrowsA() {
    return stats.count("FTA");
  }

  @Override
  public void addFreeThrowsA(int freeThrowsA) {
    if (freeThrowsA < 0) {
      stats.remove("FTA", -1 * freeThrowsA);
    } else {
      stats.add("FTA", freeThrowsA);
    }
  }

  @Override
  public int getOffensiveRebounds() {
    return stats.count("ORB");
  }

  @Override
  public void addOffensiveRebounds(int orb) {
    if (orb < 0) {
      stats.remove("ORB", -1 * orb);
    } else {
      stats.add("ORB", orb);
    }
  }

  @Override
  public int getDefensiveRebounds() {
    return stats.count("DRB");
  }

  @Override
  public void addDefensiveRebounds(int drb) {
    if (drb < 0) {
      stats.remove("DRB", -1 * drb);
    } else {
      stats.add("DRB", drb);
    }
  }

  @Override
  public int getAssists() {
    return stats.count("AST");
  }

  @Override
  public void addAssists (int ast) {
    if (ast < 0) {
      stats.remove("AST", -1 * ast);
    } else {
      stats.add("AST", ast);
    }
  }

  @Override
  public int getSteals() {
    return stats.count("STL");
  }

  @Override
  public void addSteals(int stl) {
    if (stl < 0) {
      stats.remove("STL", -1 * stl);
    } else {
      stats.add("STL", stl);
    }
  }

  @Override
  public int getBlocks() {
    return stats.count("BLK");
  }

  @Override
  public void addBlocks(int blk) {
    if (blk < 0) {
      stats.remove("BLK", -1 * blk);
    } else {
      stats.add("BLK", blk);
    }
  }

  @Override
  public int getTurnovers() {
    return stats.count("TOV");
  }

  @Override
  public void addTurnovers(int tov) {
    if (tov < 0) {
      stats.remove("TOV", -1 * tov);
    } else {
      stats.add("TOV", tov);
    }
  }

  @Override
  public int getOffensiveFouls() {
    return stats.count("OffensiveFouls");
  }


  @Override
  public void addOffensiveFouls(int offensiveFoul) {
    if (offensiveFoul < 0) {
      stats.remove("OffensiveFouls", -1 * offensiveFoul);
    } else {
      stats.add("OffensiveFouls", offensiveFoul);
    }
  }

  @Override
  public int getDefensiveFouls() {
    return stats.count("DF");
  }

  @Override
  public void addDefensiveFouls(int defensiveFoul) {
    if (defensiveFoul < 0) {
      stats.remove("DF", -1 * defensiveFoul);
    } else {
      stats.add("DF", defensiveFoul);
    }
  }

  @Override
  public int getTechnicalFouls() {
    return stats.count("TF");
  }

  @Override
  public void addTechnicalFouls(int technicalFouls) {
    if (technicalFouls < 0) {
      stats.remove("TF", -1 * technicalFouls);
    } else {
      stats.add("TF", technicalFouls);
    }
  }

  public int getFieldGoals() {
    return stats.count("ThreePM") + stats.count("TwoPM");
  }

  public int getFieldGoalsA() {
    return stats.count("ThreePA") + stats.count("TwoPA");
  }

  public double getTwoPointPercentage() {
    return  stats.count("TwoPM") / (double) stats.count("TwoPA");
  }

  public double getThreePointPercentage() {
    return  stats.count("ThreePM") / (double) stats.count("ThreePA");
  }

  public double getFreeThrowPercentage() {
    return stats.count("FTM") / (double) stats.count("FTA");
  }

  public int getPoints() {
    return stats.count("FTM")
        + TWO_POINTS * stats.count("TwoPM")
        + THREE_POINTS * stats.count("ThreePM");
  }

  public int getRebounds() {
    return stats.count("ORB") + stats.count("DRB");
  }

  public int getPersonalFouls() {
    return stats.count("OF") + stats.count("DF");
  }

  @Override
  public String toString() {
    return "GAME STATS FOR " + this.player;
  }

}
