package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class OffensiveRebound implements Stat {
  private static final String TYPE = "OffensiveRebound";
  private Location pos;
  private Player p;
  private int id;
  private int period;

  public OffensiveRebound(int id, Player p, Location pos, int period) {
    this.id = id;
    this.pos = pos;
    this.p = p;
    this.period = period;
  }

  @Override
  public void execute(GameStats ps) {
    ps.addOffensiveRebounds(1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.addOffensiveRebounds(-1);
  }

  @Override
  public Location getLocation() {
    return pos;
  }

  @Override
  public Player getPlayer() {
    return p;
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
    return TYPE + " for " + p + "(id: " + id + ")";
  }

  @Override
  public String getStatType() {
    return TYPE;
  }

}
