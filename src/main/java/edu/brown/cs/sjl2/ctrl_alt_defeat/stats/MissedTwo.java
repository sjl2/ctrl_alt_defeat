package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class MissedTwo implements Stat {
  private Location pos;
  private Player player;

  public MissedTwo(Location pos, Player player) {
    this.pos = pos;
    this.player = player;
  }

  @Override
  public void execute(GameStats ps) {
    ps.setTwoPointersA(ps.getTwoPointersA() + 1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.setTwoPointersA(ps.getTwoPointersA() - 1);
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
