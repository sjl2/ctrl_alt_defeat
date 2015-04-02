package edu.brown.cs.sjl2.ctrl_alt_defeat.stats.basketballstats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerGameStats;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class DefensiveFoul implements Stat {
  private Location pos;
  private Player player;

  @Override
  public void execute(PlayerGameStats ps) {
    ps.setPersonalFouls(ps.getPersonalFouls() + 1);
    ps.setDefensiveFouls(ps.getDefensiveFouls() + 1);
  }

  @Override
  public void undo(PlayerGameStats ps) {
    ps.setPersonalFouls(ps.getPersonalFouls() - 1);
    ps.setDefensiveFouls(ps.getDefensiveFouls() - 1);
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
