package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

/**
 * A steal stat.
 *
 * @author ngoelz
 *
 */
public class Steal implements Stat {
  private static final String TYPE = "Steal";
  private Location pos;
  private Player player;
  private int id;
  private int period;

  /**
   * Constructor of a steal.
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
  public Steal(int id, Player player, Location pos, int period) {
    this.id = id;
    this.pos = pos;
    this.player = player;
    this.period = period;
  }

  @Override
  public void execute(GameStats ps) {
    ps.addSteals(1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.addSteals(-1);
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
