package edu.brown.cs.sjl2.ctrl_alt_defeat.stats.basketballstats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class OffensiveRebound implements Stat {
  private Location pos;
  private Player player;

  @Override
  public void execute(GameStats ps) {
    ps.setOffensiveRebounds(ps.getOffensiveRebounds() + 1);
  }

  @Override
  public void undo(GameStats ps) {
    ps.setOffensiveRebounds(ps.getOffensiveRebounds() - 1);
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
