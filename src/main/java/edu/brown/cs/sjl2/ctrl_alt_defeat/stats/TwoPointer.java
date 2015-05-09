package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

/**
 * A two pointer stat.
 *
 * @author ngoelz
 *
 */
public class TwoPointer implements Stat {
  private static final String TYPE = "TwoPointer";
  private Location pos;
  private Player player;
  private int id;
  private int period;

  /**
   * Constructor of a two pointer.
   *
   * @param id
   *          the id of the stat
   * @param player
   *          the player
   * @param pos
   *          the position on the court
   * @param period
   *          the period in the game
   */
  public TwoPointer(int id, Player player, Location pos, int period) {
    this.id = id;
    this.period = period;
    this.pos = pos;
    this.player = player;
  }

  @Override
  public void execute(GameStats ps) {
    ps.addTwoPointers(1);
    ps.addTwoPointersA(1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.addTwoPointers(-1);
    ps.addTwoPointersA(-1);
  }

  @Override
  public Location getLocation() {
    return pos;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  @Override
  public int getID() {
    return id;
  }

  @Override
  public int getPeriod() {
    return period;
  }

  @Override
  public String toString() {
    return TYPE + " for " + player + "(id: " + id + ")";
  }

  @Override
  public String getStatType() {
    return TYPE;
  }

}
