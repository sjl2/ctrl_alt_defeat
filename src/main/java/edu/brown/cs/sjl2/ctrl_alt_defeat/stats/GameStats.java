package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

public interface GameStats {

  /**
   * A getter for the minutes played.
   *
   * @return minutes played
   */
  int getMinutes();

  /**
   * Adds minutes.
   *
   * @param minutes
   *          to be added
   */
  void addMinutes(int minutes);

  /**
   * A getter for the two pointers made.
   *
   * @return two pointers made
   */
  int getTwoPointers();

  /**
   * Adds two pointers.
   *
   * @param twoPointers
   *          to be added
   */
  void addTwoPointers(int twoPointers);

  /**
   * A getter for the two pointers attempted.
   *
   * @return two pointers attempted
   */
  int getTwoPointersA();

  /**
   * Adds two pointers attempted.
   *
   * @param twoPointersA
   *          to be added
   */
  void addTwoPointersA(int twoPointersA);

  /**
   * A getter for the three pointers.
   *
   * @return three pointers
   */
  int getThreePointers();

  /**
   * Adds three pointers.
   *
   * @param threePointers
   *          to be added
   */
  void addThreePointers(int threePointers);

  /**
   * A getter for the three pointers attempted.
   *
   * @return three pointers attempted
   */
  int getThreePointersA();

  /**
   * Adds three pointers attempted.
   *
   * @param threePointersA
   *          to be added
   */
  void addThreePointersA(int threePointersA);

  /**
   * A getter for the free pointers made.
   *
   * @return free throws made
   */
  int getFreeThrows();

  /**
   * Adds free throws.
   *
   * @param freeThrows
   *          to be added
   */
  void addFreeThrows(int freeThrows);

  /**
   * A getter for the free pointers attempted.
   *
   * @return free pointers attempted
   */
  int getFreeThrowsA();

  /**
   * Adds free throws attempted.
   *
   * @param freeThrowsA
   *          to be added
   */
  void addFreeThrowsA(int freeThrowsA);

  /**
   * A getter for the offensive rebound.
   *
   * @return offensive rebound
   */
  int getOffensiveRebounds();

  /**
   * Adds offensive rebound.
   *
   * @param orb
   *          to be added
   */
  void addOffensiveRebounds(int orb);

  /**
   * A getter for the defensive rebound.
   *
   * @return defensive rebound
   */
  int getDefensiveRebounds();

  /**
   * Adds defensive rebound.
   *
   * @param drb
   *          to be added
   */
  void addDefensiveRebounds(int drb);

  /**
   * A getter for the assist.
   *
   * @return assist
   */
  int getAssists();

  /**
   * Adds assists.
   *
   * @param ast
   *          to be added
   */
  void addAssists(int ast);

  /**
   * A getter for the steals.
   *
   * @return steals
   */
  int getSteals();

  /**
   * Adds steals.
   *
   * @param stl
   *          to be added
   */
  void addSteals(int stl);

  /**
   * A getter for the blocks.
   *
   * @return blocks
   */
  int getBlocks();

  /**
   * Adds blocks.
   *
   * @param blk
   *          to be added
   */
  void addBlocks(int blk);

  /**
   * A getter for the turnovers.
   *
   * @return turnovers
   */
  int getTurnovers();

  /**
   * Adds turnovers.
   *
   * @param tov
   *          to be added
   */
  void addTurnovers(int tov);

  /**
   * A getter for the offensive fouls.
   *
   * @return offensive fouls
   */
  int getOffensiveFouls();

  /**
   * Adds offensive fouls.
   *
   * @param offensiveFoul
   *          to be added
   */
  void addOffensiveFouls(int offensiveFoul);

  /**
   * A getter for the defensive fouls.
   *
   * @return defensive fouls
   */
  int getDefensiveFouls();

  /**
   * Adds defensive fouls.
   *
   * @param defensiveFoul
   *          to be added
   */
  void addDefensiveFouls(int defensiveFoul);

  /**
   * A getter for the technical fouls.
   *
   * @return technical fouls
   */
  int getTechnicalFouls();

  /**
   * Add technical fouls.
   *
   * @param technicalFouls
   *          to be added.
   */
  void addTechnicalFouls(int technicalFouls);
}
