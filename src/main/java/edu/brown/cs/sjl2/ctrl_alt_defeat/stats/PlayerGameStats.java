package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

public class PlayerGameStats {
  private int minutesPlayed;
  private int twoPoints;
  private int twoPointsA;
  private int threePoints;
  private int threePointsA;
  private int freeThrows;
  private int freeThrowsA;
  private int orb;
  private int drb;
  private int ast;
  private int stl;
  private int blk;
  private int tov;
  private int pf;
  private int offensiveFouls;
  private int defensiveFouls;
  private int technicalFouls;


  public int getMinutesPlayed() {
    return minutesPlayed;
  }
  public void setMinutesPlayed(int minutesPlayed) {
    this.minutesPlayed = minutesPlayed;
  }
  public int getTwoPointers() {
    return twoPoints;
  }
  public void setTwoPointers(int twoPoints) {
    this.twoPoints = twoPoints;
  }
  public int getTwoPointersA() {
    return twoPointsA;
  }
  public void setTwoPointersA(int twoPointsA) {
    this.twoPointsA = twoPointsA;
  }
  public int getThreePointers() {
    return threePoints;
  }
  public void setThreePointers(int threePoints) {
    this.threePoints = threePoints;
  }
  public int getThreePointersA() {
    return threePointsA;
  }
  public void setThreePointersA(int threePointsA) {
    this.threePointsA = threePointsA;
  }
  public int getFreeThrows() {
    return freeThrows;
  }
  public void setFreeThrows(int freeThrows) {
    this.freeThrows = freeThrows;
  }
  public int getFreeThrowsA() {
    return freeThrowsA;
  }
  public void setFreeThrowsA(int freeThrowsA) {
    this.freeThrowsA = freeThrowsA;
  }
  public int getOffensiveRebounds() {
    return orb;
  }
  public void setOffensiveRebounds(int orb) {
    this.orb = orb;
  }
  public int getDefensiveRebound() {
    return drb;
  }
  public void setDefensiveRebound(int drb) {
    this.drb = drb;
  }
  public int getAssists() {
    return ast;
  }
  public void setAssists (int ast) {
    this.ast = ast;
  }
  public int getSteals() {
    return stl;
  }
  public void setSteals(int stl) {
    this.stl = stl;
  }
  public int getBlocks() {
    return blk;
  }
  public void setBlocks(int blk) {
    this.blk = blk;
  }
  public int getTurnovers() {
    return tov;
  }
  public void setTurnovers(int tov) {
    this.tov = tov;
  }
  public int getPersonalFouls() {
    return pf;
  }
  public void setPersonalFouls(int pf) {
    this.pf = pf;
  }
  public int getOffensiveFouls() {
    return offensiveFouls;
  }
  public void setOffensiveFouls(int offensiveFoul) {
    this.offensiveFouls = offensiveFoul;
  }
  public int getDefensiveFouls() {
    return defensiveFouls;
  }
  public void setDefensiveFouls(int defensiveFoul) {
    this.defensiveFouls = defensiveFoul;
  }
  public int getTechnicalFouls() {
    return technicalFouls;
  }
  public void setTechnicalFouls(int technicalFouls) {
    this.technicalFouls = technicalFouls;
  }
}
