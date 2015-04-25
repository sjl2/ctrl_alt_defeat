package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public interface Stat {

  int getID();

  int getPeriod();

  void execute(PlayerStats ps);

  void undo(PlayerStats ps);

  Location getLocation();

  Player getPlayer();

  String getStatType();

}
