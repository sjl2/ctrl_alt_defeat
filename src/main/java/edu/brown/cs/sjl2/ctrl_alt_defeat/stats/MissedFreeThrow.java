package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class MissedFreeThrow implements Stat {
  private Location pos;
  private Player player;

  @Override
  public void execute(GameStats ps) {
    ps.setFreeThrowsA(ps.getFreeThrowsA() + 1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.setFreeThrowsA(ps.getFreeThrowsA() - 1);
  }

  @Override
  public Location getPosition() {
    return pos;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

}