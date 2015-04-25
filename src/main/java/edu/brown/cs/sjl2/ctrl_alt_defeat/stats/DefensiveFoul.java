package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class DefensiveFoul implements Stat {
  private static final String TYPE = "DefensiveFoul";
  private Location pos;
  private Player player;
  private int id;
  private int period;

  public DefensiveFoul(int id, Player player, Location pos, int period) {
    this.id = id;
    this.period = period;
    this.pos = pos;
    this.player = player;
  }

  @Override
  public void execute(GameStats ps) {
    ps.addDefensiveFouls(1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.addDefensiveFouls(-1);
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
