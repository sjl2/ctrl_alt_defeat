package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class PlayerFactory {

  private Map<Integer, Player> players;

  /**
   * Factory object for players to ensure that there is only one object for a
   * player.
   * @param db The database manager needed to query for a player
   */
  public PlayerFactory(DBManager db) {
    this.players = new HashMap<>();
  }

  /**
   * Getter for a player from its id.
   * @param id The id of a player
   * @return Returns the player object corresponding to the id.
   */
  public Player getPlayer(int id) {
    return players.get(id);
  }

  public Player getPlayer(
      int id,
      String name,
      int teamID,
      String teamName,
      int number,
      boolean curr) {

    Player p = new Player(id, name, number, teamID, teamName, curr);
    cachePlayer(p);
    return p;
  }

  private void cachePlayer(Player p) {
    players.put(p.getID(), p);
  }

}
