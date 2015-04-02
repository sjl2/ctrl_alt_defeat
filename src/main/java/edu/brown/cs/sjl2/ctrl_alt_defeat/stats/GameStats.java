package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

public class GameStats {
  private int minutesPlayed = 0;
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

  public GameStats copy() {
    GameStats copy = new GameStats();

    copy.setMinutesPlayed(minutesPlayed);
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

  public int getMinutesPlayed() {
    return minutesPlayed;
  }

  public void setMinutesPlayed(int minutesPlayed) {
    this.minutesPlayed = minutesPlayed;
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

  public int getRebounds() {
    return orb + drb;
  }

  public int getPersonalFouls() {
    return offensiveFouls + defensiveFouls;
  }
}
