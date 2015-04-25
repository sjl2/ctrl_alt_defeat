package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

public interface GameStats {

  public int getMinutes();

  public void addMinutes(int minutes);

  public int getTwoPointers();

  public void addTwoPointers(int twoPointers);

  public int getTwoPointersA();

  public void addTwoPointersA(int twoPointersA);

  public int getThreePointers();

  public void addThreePointers(int threePointers);

  public int getThreePointersA();

  public void addThreePointersA(int threePointersA);

  public int getFreeThrows();

  public void addFreeThrows(int freeThrows);

  public int getFreeThrowsA();

  public void addFreeThrowsA(int freeThrowsA);

  public int getOffensiveRebounds();

  public void addOffensiveRebounds(int orb);

  public int getDefensiveRebounds();

  public void addDefensiveRebounds(int drb);

  public int getAssists();

  public void addAssists (int ast);

  public int getSteals();

  public void addSteals(int stl);

  public int getBlocks();

  public void addBlocks(int blk);

  public int getTurnovers();

  public void addTurnovers(int tov);

  public int getOffensiveFouls();

  public void addOffensiveFouls(int offensiveFoul);

  public int getDefensiveFouls();

  public void addDefensiveFouls(int defensiveFoul);

  public int getTechnicalFouls();

  public void addTechnicalFouls(int technicalFouls);
}
