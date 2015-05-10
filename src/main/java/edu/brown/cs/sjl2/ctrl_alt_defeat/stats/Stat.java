package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

/**Interface used for construction of stats.
 *
 * @author ngoelz
 *
 */
public interface Stat {

  /**
   * Getter for the stat id.
   *
   * @return the id of the stat.
   */
  int getID();

  /**
   * Getter for the period the stat occurred in.
   *
   * @return the period the stat occurred in.
   */
  int getPeriod();

  /**
   * The method which executes the stat on the current GameStats object. Will be
   * called once on the player who got the stat and once on the his/her team's
   * stats.
   *
   * @param gs
   *          Who gets the stat (their gamestat)
   */
  void execute(GameStats gs);

  /**
   * Undo the given stat on the game stats provided.
   *
   * @param gs
   *          The game stats to have the stat undone.
   */
  void undo(GameStats gs);

  /**
   * Getter for the location where the stat occurred.
   *
   * @return the location of the stat.
   */
  Location getLocation();

  /**
   * Getter for the player who got the stat.
   *
   * @return the player associated with the stat.
   */
  Player getPlayer();

  /**
   * Getter for the type of the stat.
   *
   * @return the type of the stat.
   */
  String getStatType();

}
