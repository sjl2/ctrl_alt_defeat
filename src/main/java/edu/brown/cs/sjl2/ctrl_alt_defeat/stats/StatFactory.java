package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class StatFactory {

  private DBManager db;
  private Game game;
  private Map<Integer, Stat> stats;

  public StatFactory(DBManager db, Game game) {
    this.db = db;
    this.game = game;
    this.stats = new LinkedHashMap<>();
  }

  public Stat getStat(int id) {
    return stats.get(id);
  }

  public Stat updateStat(int id, String statID, Player p, Location location) {
    Stat s = stats.get(id);
    int period = s.getPeriod();

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

    stats.put(id, s);

    db.updateStat(s);

    return s;
  }

  public Stat addStat(String statID, Player p, Location location, int period) {
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

    db.storeStat(s, statID, game);
    stats.put(s.getID(), s);

    return s;
  }

  public Stat removeStat(int id) throws GameException {
    Stat s = getStat(id);
    stats.remove(id);
    db.removeStat(s);
    return s;
  }

  public List<Stat> getAllStats() {
    return new ArrayList<>(stats.values());
  }

}
