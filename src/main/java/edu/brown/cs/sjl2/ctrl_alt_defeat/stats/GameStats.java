package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;

public class GameStats {
  private static final int TWO_POINTS = 2;
  private static final int THREE_POINTS = 3;
  private static final int FIRST_STAT_COL = 3;
  private static final String[] COLS = {
    "game", "team", "player", "MIN", "TwoPM", "TwoPA", "ThreePM", "ThreePA",
    "FTM", "FTA", "ORB", "DRB", "AST", "STL", "BLK", "TOV", "OF", "DF"
  };

  private Player player;
  private Team team;
  private int game;
  private Multiset<String> stats;


  public GameStats(int game, Team team, Player player) {
    this.player = player;
    this.team = team;
    this.game = game;
    this.stats = HashMultiset.create();

    for (String s : getCols()) {
      stats.setCount(s, 0);
    }

    stats.setCount("game", game);
    stats.setCount("team", team.getID());
    if (player != null) {
      //TODO
      stats.setCount("player", player.getID());
    } else {
      stats.setCount("player", 0); // Team Stats Don't Have player id.
    }

  }

  /**
   * Constructor that initializes the values of the stats to the values. Ensure
   * that the values correspond to the correct columns by matching the stats to
   * the output of getCols(). Note, the length mus be equal to
   * GameStats.getNumCols() as well.
   * @param values A list of Integers representing in order the stats found in
   * GameStats.getCols().
   * @param game The game object to be found.
   * @param team The team referring for the gamestats
   * @param player The player of the gamestats.
   */
  private GameStats(List<Integer> values, Game game, Team team, Player player) {
    this.game = game.getID();
    this.team = team;
    this.player = player;
    this.stats = HashMultiset.create();

    for (int i = FIRST_STAT_COL; i < COLS.length; i++) {
      stats.setCount(COLS[i], values.get(i));
    }
  }

  public GameStats(Multiset<String> values, Game game, Team team, Player player) {
    this.game = game.getID();
    this.team = team;
    this.player = player;
    this.stats = values;
  }

  public GameStats(List<Integer> values, int gameID, Team team) {
    this.game = gameID;
    this.team = team;

    int playerID = values.get(2);
    if (playerID != -1) {
      this.player = team.getPlayerById(playerID);
    } else {
      this.player = null;
    }
    
    this.stats = HashMultiset.create();

    for (int i = FIRST_STAT_COL; i < COLS.length; i++) {
      stats.setCount(COLS[i], values.get(i));
    }
  }

  public static String[] getCols() {
    return COLS;
  }

  public static int getNumCols() {
    return COLS.length;
  }

  public static GameStats newTeamGameStats(Game game, Team team) {
    return new GameStats(game.getID(), team, null);
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

  public int getMinutes() {
    return stats.count("MIN");
  }

  void addMinutes(int minutes) {
    if (minutes < 0) {
      stats.remove("MIN", -1 * minutes);
    } else {
      stats.add("MIN", minutes);
    }
  }

  public int getTwoPointers() {
    return stats.count("TwoPM");
  }

  void addTwoPointers(int twoPointers) {
    if (twoPointers < 0) {
      stats.remove("TwoPM", -1 * twoPointers);
    } else {
      stats.add("TwoPM", twoPointers);
    }
  }

  public int getTwoPointersA() {
    return stats.count("TwoPA");
  }

  void addTwoPointersA(int twoPointersA) {
    if (twoPointersA < 0) {
      stats.remove("TwoPA", -1 * twoPointersA);
    } else {
      stats.add("TwoPA", twoPointersA);
    }
  }

  public int getThreePointers() {
    return stats.count("ThreePM");
  }

  void addThreePointers(int threePointers) {
    if (threePointers < 0) {
      stats.remove("ThreePM", -1 * threePointers);
    } else {
      stats.add("ThreePM", threePointers);
    }
  }

  public int getThreePointersA() {
    return stats.count("ThreePA");
  }

  void addThreePointersA(int threePointersA) {
    if (threePointersA < 0) {
      stats.remove("ThreePA", -1 * threePointersA);
    } else {
      stats.add("ThreePA", threePointersA);
    }
  }

  public int getFreeThrows() {
    return stats.count("FTM");
  }

  void addFreeThrows(int freeThrows) {
    if (freeThrows < 0) {
      stats.remove("FTM", -1 * freeThrows);
    } else {
      stats.add("FTM", freeThrows);
    }
  }

  public int getFreeThrowsA() {
    return stats.count("FTA");
  }

  void addFreeThrowsA(int freeThrowsA) {
    if (freeThrowsA < 0) {
      stats.remove("FTA", -1 * freeThrowsA);
    } else {
      stats.add("FTA", freeThrowsA);
    }
  }

  public int getOffensiveRebounds() {
    return stats.count("ORB");
  }

  void addOffensiveRebounds(int orb) {
    if (orb < 0) {
      stats.remove("ORB", -1 * orb);
    } else {
      stats.add("ORB", orb);
    }
  }

  public int getDefensiveRebounds() {
    return stats.count("DRB");
  }

  void addDefensiveRebounds(int drb) {
    if (drb < 0) {
      stats.remove("DRB", -1 * drb);
    } else {
      stats.add("DRB", drb);
    }
  }

  public int getAssists() {
    return stats.count("AST");
  }

  void addAssists (int ast) {
    if (ast < 0) {
      stats.remove("AST", -1 * ast);
    } else {
      stats.add("AST", ast);
    }
  }

  public int getSteals() {
    return stats.count("STL");
  }

  void addSteals(int stl) {
    if (stl < 0) {
      stats.remove("STL", -1 * stl);
    } else {
      stats.add("STL", stl);
    }
  }

  public int getBlocks() {
    return stats.count("BLK");
  }

  void addBlocks(int blk) {
    if (blk < 0) {
      stats.remove("BLK", -1 * blk);
    } else {
      stats.add("BLK", blk);
    }
  }

  public int getTurnovers() {
    return stats.count("TOV");
  }

  void addTurnovers(int tov) {
    if (tov < 0) {
      stats.remove("TOV", -1 * tov);
    } else {
      stats.add("TOV", tov);
    }
  }

  public int getOffensiveFouls() {
    return stats.count("OffensiveFouls");
  }


  void addOffensiveFouls(int offensiveFoul) {
    if (offensiveFoul < 0) {
      stats.remove("OffensiveFouls", -1 * offensiveFoul);
    } else {
      stats.add("OffensiveFouls", offensiveFoul);
    }
  }

  public int getDefensiveFouls() {
    return stats.count("DF");
  }

  void addDefensiveFouls(int defensiveFoul) {
    if (defensiveFoul < 0) {
      stats.remove("DF", -1 * defensiveFoul);
    } else {
      stats.add("DF", defensiveFoul);
    }
  }

  public int getTechnicalFouls() {
    return stats.count("TF");
  }

  void addTechnicalFouls(int technicalFouls) {
    if (technicalFouls < 0) {
      stats.remove("TF", -1 * technicalFouls);
    } else {
      stats.add("TF", technicalFouls);
    }
  }

  public int getFieldGoals() {
    return stats.count("ThreePM") + stats.count("TwoPM");
  }

  public int getFieldGoalsAttempted() {
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

}
