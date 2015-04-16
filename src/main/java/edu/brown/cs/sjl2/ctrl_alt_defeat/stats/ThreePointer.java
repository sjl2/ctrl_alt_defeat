package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class ThreePointer implements Stat {
  private Location pos;
  private Player player;
  private int id;
  private int period;

  public ThreePointer(int id, Player player, Location pos, int period) {
    this.id = id;
    this.period = period;
    this.pos = pos;
    this.player = player;
  }

  @Override
  public void execute(GameStats ps) {
    ps.setThreePointers(ps.getThreePointers() + 1);
    ps.setThreePointersA(ps.getThreePointersA() + 1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.setThreePointers(ps.getThreePointers() - 1);
    ps.setThreePointersA(ps.getThreePointersA() - 1);
  }

  @Override
  public Location getPosition() {
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



}
