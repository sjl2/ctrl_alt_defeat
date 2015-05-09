package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

/**
 * Interface for a RuleSet which determine the certain game restrictions
 * depending on the level of play.
 *
 * @author sjl2
 *
 */
public interface RuleSet {

  /**
   * Getter for the number of time outs per half.
   * 
   * @return Returns the number of time outs per team per half.
   */
  int getTimeOuts();

  /**
   * Getter for the number of time outs in over time.
   * 
   * @return Returns the number of times out per team in over time.
   */
  int getOTTimeOuts();

  /**
   * Number of periods in the game.
   * 
   * @return Returns int number of periods in the game.
   */
  int getPeriods();

  /**
   * Getter for the number of minutes in a period.
   * 
   * @return Returns the number of minutes in a period.
   */
  int getPeriodMinutes();

  /**
   * Getter for the number of fouls until the bonus.
   * 
   * @return Returns the number of fouls until the bonus.
   */
  int getBonus();

  /**
   * Getter for the number of fouls until the double bonus.
   * 
   * @return Returns the number of fouls until the double bonus.
   */
  int getDoubleBonus();
}
