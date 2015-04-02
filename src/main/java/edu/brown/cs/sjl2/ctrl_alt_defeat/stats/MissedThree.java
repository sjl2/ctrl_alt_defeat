package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class MissedThree implements Stat {
  private Location pos;
  private Player player;

  @Override
  public void execute(GameStats ps) {
    ps.setThreePointersA(ps.getThreePointersA() + 1);
  }

  @Override
  public void undo(GameStats ps) {
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

}
