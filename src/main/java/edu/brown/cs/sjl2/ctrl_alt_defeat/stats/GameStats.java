package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class GameStats {
  private static final int TWO_POINTS = 2;
  private static final int THREE_POINTS = 3;

  private int id;
  private Player player;
  private Game game;
  private int minutes = 0;
  private int twoPointers = 0;
  private int twoPointersA = 0;
  private int threePointers = 0;
  private int threePointersA = 0;
  private int freeThrows = 0;
  private int freeThrowsA = 0;
  private int orb = 0;
  private int drb = 0;
  private int ast = 0;
  private int stl = 0;
  private int blk = 0;
  private int tov = 0;
  private int offensiveFouls = 0;
  private int defensiveFouls = 0;
  private int technicalFouls = 0;

  public GameStats(int id, Player player, Game game) {
    this.id = id;
    this.player = player;
    this.game = game;
  }

  public static GameStats TeamGameStats(Game game) {
    return new GameStats(-1, null, game);
  }

  public GameStats copy() {
    GameStats copy = new GameStats(id, player, game);

    copy.setMinutes(minutes);
    copy.setTwoPointers(twoPointers);
    copy.setTwoPointersA(twoPointersA);
    copy.setThreePointers(threePointers);
    copy.setThreePointersA(threePointersA);
    copy.setFreeThrows(freeThrows);
    copy.setFreeThrowsA(freeThrowsA);
    copy.setOffensiveRebounds(orb);
    copy.setDefensiveRebounds(drb);
    copy.setAssists(ast);
    copy.setSteals(stl);
    copy.setBlocks(blk);
    copy.setTurnovers(tov);
    copy.setOffensiveFouls(offensiveFouls);
    copy.setDefensiveFouls(defensiveFouls);
    copy.setTechnicalFouls(technicalFouls);

    return copy;
  }

  // TODO Finish Storing GameStats
  public List<Integer> getStatValues() {
    List<Integer> stats = new ArrayList<>();

    assert (id != -1 && player != null);

    stats.add(game.getID());
    stats.add(player.getTeamID());
    stats.add(player.getID());
    stats.add(minutes);
    stats.add(getFieldGoals());
    stats.add(getFieldGoalsAttempted());
    stats.add(twoPointers);
    stats.add(twoPointersA);
    stats.add(threePointers);
    stats.add(threePointersA);
    stats.add(freeThrows);
    stats.add(freeThrowsA);
    stats.add(orb);
    stats.add(drb);
    stats.add(ast);
    stats.add(stl);
    stats.add(blk);
    stats.add(tov);
    stats.add(getPersonalFouls());
    stats.add(getPoints());

    return stats;
  }

  public static List<String> getStatTitles() {
    List<String> stats = new ArrayList<>();

    stats.add("game");
    stats.add("team");
    stats.add("player");
    stats.add("minutes");
    stats.add("FGM");
    stats.add("FGA");
    stats.add("twoPointersM");
    stats.add("twoPointersA");
    stats.add("threePointersM");
    stats.add("threePointersA");
    stats.add("FTM");
    stats.add("FTA");
    stats.add("ORB");
    stats.add("DRB");
    stats.add("AST");
    stats.add("STL");
    stats.add("BLK");
    stats.add("TOV");
    stats.add("PF");
    stats.add("points");

    return stats;
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

  /**
   * Getter for the Game of these stats.
   * @return
   */
  public Game getGame() {
    return game;
  }

  public int getMinutes() {
    return minutes;
  }

  public void setMinutes(int minutesPlayed) {
    this.minutes = minutesPlayed;
  }

  public int getTwoPointers() {
    return twoPointers;
  }

  void setTwoPointers(int twoPoints) {
    this.twoPointers = twoPoints;
  }

  public int getTwoPointersA() {
    return twoPointersA;
  }

  void setTwoPointersA(int twoPointsA) {
    this.twoPointersA = twoPointsA;
  }

  public int getThreePointers() {
    return threePointers;
  }

  void setThreePointers(int threePoints) {
    this.threePointers = threePoints;
  }

  public int getThreePointersA() {
    return threePointersA;
  }

  void setThreePointersA(int threePointsA) {
    this.threePointersA = threePointsA;
  }

  public int getFreeThrows() {
    return freeThrows;
  }

  void setFreeThrows(int freeThrows) {
    this.freeThrows = freeThrows;
  }

  public int getFreeThrowsA() {
    return freeThrowsA;
  }

  void setFreeThrowsA(int freeThrowsA) {
    this.freeThrowsA = freeThrowsA;
  }

  public int getOffensiveRebounds() {
    return orb;
  }

  void setOffensiveRebounds(int orb) {
    this.orb = orb;
  }

  public int getDefensiveRebounds() {
    return drb;
  }

  void setDefensiveRebounds(int drb) {
    this.drb = drb;
  }

  public int getAssists() {
    return ast;
  }

  void setAssists (int ast) {
    this.ast = ast;
  }

  public int getSteals() {
    return stl;
  }

  void setSteals(int stl) {
    this.stl = stl;
  }

  public int getBlocks() {
    return blk;
  }

  void setBlocks(int blk) {
    this.blk = blk;
  }

  public int getTurnovers() {
    return tov;
  }

  void setTurnovers(int tov) {
    this.tov = tov;
  }

  public int getOffensiveFouls() {
    return offensiveFouls;
  }


  void setOffensiveFouls(int offensiveFoul) {
    this.offensiveFouls = offensiveFoul;
  }

  public int getDefensiveFouls() {
    return defensiveFouls;
  }

  void setDefensiveFouls(int defensiveFoul) {
    this.defensiveFouls = defensiveFoul;
  }

  public int getTechnicalFouls() {
    return technicalFouls;
  }

  void setTechnicalFouls(int technicalFouls) {
    this.technicalFouls = technicalFouls;
  }

  public int getFieldGoals() {
    return threePointers + twoPointers;
  }

  public int getFieldGoalsAttempted() {
    return threePointersA + twoPointersA;
  }

  public double getTwoPointPercentage() {
    return  twoPointers / (double) twoPointersA;
  }

  public double getThreePointPercentage() {
    return  threePointers / (double) threePointersA;
  }

  public double getFreeThrowPercentage() {
    return freeThrows / (double) freeThrowsA;
  }

  public int getPoints() {
    return TWO_POINTS * twoPointers + THREE_POINTS * threePointers;
  }

  public int getRebounds() {
    return orb + drb;
  }

  public int getPersonalFouls() {
    return offensiveFouls + defensiveFouls;
  }
}
