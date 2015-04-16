package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class StatFactory {

  private DBManager db;
  private Game game;

  public StatFactory(DBManager db, Game game) {
    this.db = db;
    this.game = game;
  }

  public Stat getStat(String statID, Player p, Location location, int period) {
    int id = db.getNextID("stat");

    Stat s = null;

    switch (statID) {
      case "Block":
        s = new Block(id, p, location, period);
        break;
      case "DefensiveFoul":
        s = new DefensiveFoul(id, p, location, period);
        break;
      case "FreeThrow":
        s = new FreeThrow(id, p, location, period);
        break;
      case "MissedFreeThrow":
        s = new MissedFreeThrow(id, p, location, period);
        break;
      case "MissedTwo":
        s = new MissedTwoPointer(id, p, location, period);
        break;
      case "MissedThree":
        s = new MissedThreePointer(id, p, location, period);
        break;
      case "OffensiveFoul":
        s = new OffensiveFoul(id, p, location, period);
        break;
      case "OffensiveRebound":
        s = new OffensiveRebound(id, p, location, period);
        break;
      case "DefensiveRebound":
        s = new DefensiveRebound(id, p, location, period);
        break;
      case "Steal":
        s = new Steal(id, p, location, period);
        break;
      case "TechnicalFoul":
        s = new TechnicalFoul(id, p, location, period);
        break;
      case "ThreePointer":
        s = new ThreePointer(id, p, location, period);
        break;
      case "Turnover":
        s = new Turnover(id, p, location, period);
        break;
      case "TwoPointer":
        s = new TwoPointer(id, p, location, period);
        break;
      default:
        throw new RuntimeException("Unrecognized statID \"" + statID + "\".");
    }

    db.store(s, statID, game);

    return s;
  }

}