package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class Block implements Stat {

  private int id;
  private Location pos;
  private Player player;
  private int period;

  public Block(int id, Player player, Location pos, int period) {
    this.id = id;
    this.pos = pos;
    this.player = player;
    this.period = period;
  }

  @Override
  public void execute(GameStats ps) {
    int blocks = ps.getBlocks();
    ps.setBlocks(blocks + 1);
  }

  @Override
  public void undo(GameStats ps) {
    int blocks = ps.getBlocks();
    ps.setBlocks(blocks - 1);
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
