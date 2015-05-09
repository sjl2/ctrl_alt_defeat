package edu.brown.cs.sjl2.ctrl_alt_defeat.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

/**Cache that constructs and stores stats.
 *
 * @author ngoelz
 *
 */
public class StatFactory {

  private static final List<String> types =
      Arrays.asList("Assist", "Block", "DefensiveFoul", "FreeThrow",
          "MissedFreeThrow", "MissedTwoPointer", "MissedThreePointer",
          "OffensiveFoul", "OffensiveRebound", "DefensiveRebound", "Steal",
          "TechnicalFoul", "ThreePointer", "Turnover", "TwoPointer"
          );

  private DBManager db;
  private Game game;
  private Map<Integer, Stat> stats;

  /**Constructor for a given game.
   *
   * @param db the database
   * @param game the current game
   */
  public StatFactory(DBManager db, Game game) {
    this.db = db;
    this.game = game;
    this.stats = new LinkedHashMap<>();
  }

  /**Getter for types of stats.
   *
   * @return the types of stats.
   */
  public static List<String> getTypes() {
    return types;
  }

  /**Getter for a stat by id.
   *
   * @param id the id of the stat
   * @return the stat
   */
  public Stat getStat(int id) {
    return stats.get(id);
  }

  /**Updater for a stat.
   *
   * @param id id of stat to be updated
   * @param statType the new type
   * @param p the new player
   * @param location the new location
   * @return the new stat
   */
  public Stat updateStat(int id, String statType, Player p, Location location) {
    Stat s = stats.get(id);
    int period = s.getPeriod();

    s = newStat(statType, id, p, location, period);

    stats.put(id, s);

    db.updateStat(s);

    return s;
  }

  /**Adds a stat
   *
   * @param statType the type
   * @param p the player
   * @param location the location
   * @param period the period
   * @return the stat
   */
  public Stat addStat(String statType, Player p, Location location, int period) {
    int id = db.getNextID("stat");

    Stat s = newStat(statType, id, p, location, period);

    db.createStat(s, game.getID());
    stats.put(s.getID(), s);

    return s;
  }

  /**Construtor of a stat (subtly different than addStat).
   *
   * @param statType the type
   * @param id the id of the stat
   * @param p the player
   * @param location the location
   * @param period the period
   * @return the stat
   */
  public static Stat newStat(String statType, int id, Player p,
      Location location, int period) {

    Stat s = null;

    switch (statType) {
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
      case "MissedTwoPointer":
        s = new MissedTwoPointer(id, p, location, period);
        break;
      case "MissedThreePointer":
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
      case "Assist":
        s = new Assist(id, p, location, period);
        break;
      default:
        throw new RuntimeException("Unrecognized statID \"" + statType + "\".");
    }

    return s;
  }

  /**Remover of a stat.
   *
   * @param id stat to be removed
   * @return this stat
   * @throws GameException if the stat does not exist
   */
  public Stat removeStat(int id) throws GameException {
    Stat s = getStat(id);
    stats.remove(id);
    db.deleteStat(s);
    return s;
  }

  /**Getter for all of the stats in the cache.
   *
   * @return all present stats.
   */
  public List<Stat> getAllStats() {
    return new ArrayList<>(stats.values());
  }

}
