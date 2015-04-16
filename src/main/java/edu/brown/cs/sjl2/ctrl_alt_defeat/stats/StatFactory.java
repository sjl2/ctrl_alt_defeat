package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;

public class StatFactory {

  public Stat getStat(String statID, Player p, Location location) {
    Stat s = null;

    switch (statID) {
    case "Block":
      s = new Block(p, location);
      break;
    case "DefensiveFoul":
      break;
    case "FreeThrow":
      break;
    case "MissedFreeThrow":
      break;
    case "MissedTwo":
      break;
    case "MissedThree":
      break;
    case "OffensiveFoul":
      break;
    case "OffensiveRebound":
      break;
    case "Steal":
      break;
    case "TechnicalFoul":
      break;
    case "ThreePointer":
      break;
    case "Turnover":
      break;
    case "TwoPointer":
      break;
    }
    return null;
  }

}
