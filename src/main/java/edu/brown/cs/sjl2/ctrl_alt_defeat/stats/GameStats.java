package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

public interface GameStats {

  /**
   * A getter for the minutes played.
   *
   * @return minutes played
   */
  public int getMinutes();

  /**
   * Adds minutes.
   *
   * @param minutes to be added
   */
  public void addMinutes(int minutes);

  /**
   * A getter for the two pointers made.
   *
   * @return two pointers made
   */
  public int getTwoPointers();

  /**
   * Adds two pointers.
   *
   * @param twoPointers to be added
   */
  public void addTwoPointers(int twoPointers);

  /**
   * A getter for the two pointers attempted.
   *
   * @return two pointers attempted
   */
  public int getTwoPointersA();

  /**
   * Adds two pointers attempted.
   *
   * @param twoPointersA to be added
   */
  public void addTwoPointersA(int twoPointersA);

  /**
   * A getter for the three pointers.
   *
   * @return three pointers
   */
  public int getThreePointers();

  /**
   * Adds three pointers.
   *
   * @param threePointers to be added
   */
  public void addThreePointers(int threePointers);

  /**
   * A getter for the three pointers attempted.
   *
   * @return three pointers attempted
   */
  public int getThreePointersA();

  /**
   * Adds three pointers attempted.
   *
   * @param threePointersA to be added
   */
  public void addThreePointersA(int threePointersA);

  /**
   * A getter for the free pointers made.
   *
   * @return free throws made
   */
  public int getFreeThrows();

  /**
   * Adds free throws.
   *
   * @param freeThrows to be added
   */
  public void addFreeThrows(int freeThrows);

  /**
   * A getter for the free pointers attempted.
   *
   * @return free pointers attempted
   */
  public int getFreeThrowsA();

  /**
   * Adds free throws attempted.
   *
   * @param freeThrowsA to be added
   */
  public void addFreeThrowsA(int freeThrowsA);

  /**
   * A getter for the offensive rebound.
   *
   * @return offensive rebound
   */
  public int getOffensiveRebounds();

  /**
   * Adds offensive rebound.
   *
   * @param orb to be added
   */
  public void addOffensiveRebounds(int orb);

  /**
   * A getter for the defensive rebound.
   *
   * @return defensive rebound
   */
  public int getDefensiveRebounds();

  /**
   * Adds defensive rebound.
   *
   * @param drb to be added
   */
  public void addDefensiveRebounds(int drb);

  /**
   * A getter for the assist.
   *
   * @return assist
   */
  public int getAssists();

  /**
   * Adds assists.
   *
   * @param ast to be added
   */
  public void addAssists(int ast);

  /**
   * A getter for the steals.
   *
   * @return steals
   */
  public int getSteals();

  /**
   * Adds steals.
   *
   * @param stl to be added
   */
  public void addSteals(int stl);

  /**
   * A getter for the blocks.
   *
   * @return blocks
   */
  public int getBlocks();

  /**
   * Adds blocks.
   *
   * @param blk to be added
   */
  public void addBlocks(int blk);

  /**
   * A getter for the turnovers.
   *
   * @return turnovers
   */
  public int getTurnovers();

  /**
   * Adds turnovers.
   *
   * @param tov to be added
   */
  public void addTurnovers(int tov);

  /**
   * A getter for the offensive fouls.
   *
   * @return offensive fouls
   */
  public int getOffensiveFouls();

  /**
   * Adds offensive fouls.
   *
   * @param offensiveFoul to be added
   */
  public void addOffensiveFouls(int offensiveFoul);

  /**
   * A getter for the defensive fouls.
   *
   * @return defensive fouls
   */
  public int getDefensiveFouls();

  /**
   * Adds defensive fouls.
   *
   * @param defensiveFoul to be added
   */
  public void addDefensiveFouls(int defensiveFoul);

  /**
   * A getter for the technical fouls.
   *
   * @return technical fouls
   */
  public int getTechnicalFouls();

  /**
   * Add technical fouls
   *
   * @param technicalFouls to be added.
   */
  public void addTechnicalFouls(int technicalFouls);
}
