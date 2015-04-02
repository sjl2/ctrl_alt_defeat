package edu.brown.cs.sjl2.ctrl_alt_defeat.stats.basketball;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Position;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerGameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class TwoPointer implements Stat {
  private Position pos;
  private Player player;

  @Override
  public void execute(PlayerGameStats ps) {
    ps.setTwoPointers(ps.getTwoPointers() + 1);
    ps.setTwoPointersA(ps.getTwoPointersA() + 1);
  }

  @Override
  public void undo(PlayerGameStats ps) {
    ps.setTwoPointers(ps.getTwoPointers() - 1);
    ps.setTwoPointersA(ps.getTwoPointersA() - 1);
  }

  @Override
  public Position getPosition() {
    return pos;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

}
