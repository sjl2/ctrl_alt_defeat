package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Position;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public interface Stat {

  void execute(PlayerStats ps);

  void undo(PlayerStats ps);

  Position getPosition();

  Player getPlayer();

}