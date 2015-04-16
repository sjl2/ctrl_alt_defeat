package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class DefensiveRebound implements Stat {
  private Location pos;
  private Player player;

  public DefensiveRebound(Location pos, Player player) {
    this.pos = pos;
    this.player = player;
  }

  @Override
  public void execute(GameStats ps) {
    ps.setDefensiveRebounds(ps.getDefensiveRebounds() + 1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.setDefensiveRebounds(ps.getDefensiveRebounds() - 1);
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
