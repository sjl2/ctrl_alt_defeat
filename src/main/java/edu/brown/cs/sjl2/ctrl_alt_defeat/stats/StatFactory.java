package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class StatFactory {

  public Stat getStat(String statID, Player p, Location location) {
    Stat s = null;

    switch (statID) {
      case "Block":
        s = new Block(p, location);
        break;
      case "DefensiveFoul":
        s = new DefensiveFoul(p, location);
        break;
      case "FreeThrow":
        s = new FreeThrow(p, location);
        break;
      case "MissedFreeThrow":
        s = new MissedFreeThrow(p, location);
        break;
      case "MissedTwo":
        s = new MissedTwo(p, location);
        break;
      case "MissedThree":
        s = new MissedThree(p, location);
        break;
      case "OffensiveFoul":
        s = new OffensiveFoul(p, location);
        break;
      case "OffensiveRebound":
        s = new OffensiveRebound(p, location);
        break;
      case "Steal":
        s = new Steal(p, location);
        break;
      case "TechnicalFoul":
        s = new TechnicalFoul(p, location);
        break;
      case "ThreePointer":
        s = new ThreePointer(p, location);
        break;
      case "Turnover":
        s = new Turnover(p, location);
        break;
      case "TwoPointer":
        s = new TwoPointer(p, location);
        break;
      default:
        throw new RuntimeException("Unrecognized statID \"" + statID + "\".");
    }

    return s;
  }

}
