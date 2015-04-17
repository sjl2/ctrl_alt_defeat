package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class GameStats {
  private static final int TWO_POINTS = 2;
  private static final int THREE_POINTS = 3;

  private int id;
  private Player player;
  private Game game;
  private Multiset<String> stats = HashMultiset.create();


  public GameStats(int id, Game game, Player player) {
    this.id = id;
    this.player = player;
    this.game = game;

    for (String s : getCols()) {
      stats.setCount(s, 0);
    }
  }

  public static List<String> getCols() {
    List<String> titles = new ArrayList<>();
    titles.add("game");
    titles.add("team");
    titles.add("player");
    titles.add("MIN");
    titles.add("2PM");
    titles.add("2PA");
    titles.add("3PM");
    titles.add("3PA");
    titles.add("FTM");
    titles.add("FTA");
    titles.add("ORB");
    titles.add("DRB");
    titles.add("AST");
    titles.add("STL");
    titles.add("BLK");
    titles.add("TOV");
    titles.add("OF");
    titles.add("DF");

    return titles;
  }

  public static GameStats TeamGameStats(Game game) {
    return new GameStats(-1, game, null);
  }

  public Collection<Entry<String>> getStats() {
    return stats.entrySet();
  }

  public Collection<String> getStatTitles() {
    return stats.elementSet();
  }

  /**
   * Getter for the DB ID of this GameStats. -1 If the game stats is for a team
   * as that is not stored, but built up from the players.
   * @return Returns the id of the GameStats of -1 if the stats are for a team.
   */
  public int getID() {
    return id;
  }

  /**
   * Getter for the Player that these stats refer to.
   * @return
   */
  public Player getPlayer() {
    return player;
  }

  public int getTeamID() {
    return player.getTeamID();
  }
  /**
   * Getter for the Game of these stats.
   * @return
   */
  public Game getGame() {
    return game;
  }

  public int getMinutes() {
    return stats.count("MIN");
  }

  public void addMinutes(int minutes) {
    stats.add("MIN", minutes);
  }

  public int getTwoPointers() {
    return stats.count("2PM");
  }

  void addTwoPointers(int twoPointers) {
    stats.add("2PM", twoPointers);
  }

  public int getTwoPointersA() {
    return stats.count("2PA");
  }

  void addTwoPointersA(int twoPointersA) {
    stats.add("2PA", twoPointersA);
  }

  public int getThreePointers() {
    return stats.count("3PM");
  }

  void addThreePointers(int threePointers) {
    stats.add("3PM", threePointers);
  }

  public int getThreePointersA() {
    return stats.count("3PA");
  }

  void addThreePointersA(int threePointersA) {
    stats.add("3PA", threePointersA);
  }

  public int getFreeThrows() {
    return stats.count("FTM");
  }

  void addFreeThrows(int freeThrows) {
    stats.add("FTM", freeThrows);
  }

  public int getFreeThrowsA() {
    return stats.count("FTA");
  }

  void addFreeThrowsA(int freeThrowsA) {
    stats.add("FTA", freeThrowsA);
  }

  public int getOffensiveRebounds() {
    return stats.count("ORB");
  }

  void addOffensiveRebounds(int orb) {
    stats.add("ORB", orb);
  }

  public int getDefensiveRebounds() {
    return stats.count("DRB");
  }

  void addDefensiveRebounds(int drb) {
    stats.add("DRB", drb);
  }

  public int getAssists() {
    return stats.count("AST");
  }

  void addAssists (int ast) {
    stats.add("AST", ast);
  }

  public int getSteals() {
    return stats.count("STL");
  }

  void addSteals(int stl) {
    stats.add("STL", stl);
  }

  public int getBlocks() {
    return stats.count("BLK");
  }

  void addBlocks(int blk) {
    stats.add("BLK", blk);
  }

  public int getTurnovers() {
    return stats.count("TOV");
  }

  void addTurnovers(int tov) {
    stats.add("TOV", tov);
  }

  public int getOffensiveFouls() {
    return stats.count("OffensiveFouls");
  }


  void addOffensiveFouls(int offensiveFoul) {
    stats.add("OF", offensiveFoul);
  }

  public int getDefensiveFouls() {
    return stats.count("DF");
  }

  void addDefensiveFouls(int defensiveFoul) {
    stats.add("DF", defensiveFoul);
  }

  public int getTechnicalFouls() {
    return stats.count("TF");
  }

  void addTechnicalFouls(int technicalFouls) {
    stats.add("TF", technicalFouls);
  }

  public int getFieldGoals() {
    return stats.count("3PM") + stats.count("2PM");
  }

  public int getFieldGoalsAttempted() {
    return stats.count("3PA") + stats.count("2PA");
  }

  public double getTwoPointPercentage() {
    return  stats.count("2PM") / (double) stats.count("2PA");
  }

  public double getThreePointPercentage() {
    return  stats.count("3PM") / (double) stats.count("3PA");
  }

  public double getFreeThrowPercentage() {
    return stats.count("FTM") / (double) stats.count("FTA");
  }

  public int getPoints() {
    return stats.count("FTM")
        + TWO_POINTS * stats.count("2PM")
        + THREE_POINTS * stats.count("3PM");
  }

  public int getRebounds() {
    return stats.count("ORB") + stats.count("DRB");
  }

  public int getPersonalFouls() {
    return stats.count("OF") + stats.count("DF");
  }

}
