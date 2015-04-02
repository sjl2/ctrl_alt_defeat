package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public interface Stat {

  void execute(PlayerGameStats ps);

  void undo(PlayerGameStats ps);

  Location getPosition();

  Player getPlayer();

}
