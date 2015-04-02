package edu.brown.cs.sjl2.ctrl_alt_defeat.stats.basketballstats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerGameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class ThreePointer implements Stat {
  private Location pos;
  private Player player;

  @Override
  public void execute(PlayerGameStats ps) {
    ps.setThreePointers(ps.getThreePointers() + 1);
    ps.setThreePointersA(ps.getThreePointersA() + 1);
  }

  @Override
  public void undo(PlayerGameStats ps) {
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

}
