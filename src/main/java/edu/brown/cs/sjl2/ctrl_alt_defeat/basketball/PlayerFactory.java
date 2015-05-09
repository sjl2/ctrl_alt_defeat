package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

/**
 * Factory class used to cache player objects in the database manager.
 *
 * @author sjl2
 *
 */
public class PlayerFactory {

  private Map<Integer, Player> players;

  /**
   * Factory object for players to ensure that there is only one object for a
   * player.
   *
   * @param db
   *          The database manager needed to query for a player
   */
  public PlayerFactory(DBManager db) {
    this.players = new HashMap<>();
  }

  /**
   * Getter for a player from its id.
   *
   * @param id
   *          The id of a player
   * @return Returns the player object corresponding to the id.
   */
  public Player getPlayer(int id) {
    return players.get(id);
  }

  /**
   * Get's a player object with the following parameters.
   *
   * @param id
   *          The id of the player in the database (must exist before call)
   * @param name
   *          The name of the player.
   * @param teamID
   *          The player's team id
   * @param teamName
   *          The player's team name
   * @param number
   *          The player's number
   * @param curr
   *          The boolean for whether the player is currently playing
   * @return Returns the player object representing the player info.
   */
  public Player getPlayer(int id, String name, int teamID, String teamName,
      int number, boolean curr) {

    Player p = new Player(id, name, number, teamID, teamName, curr);
    cachePlayer(p);
    return p;
  }

  /**
   * Removes player from the factory cache.
   *
   * @param id
   *          The id of the player to remove.
   */
  public void removePlayer(int id) {
    players.remove(id);
  }

  private void cachePlayer(Player p) {
    players.put(p.getID(), p);
  }

}
