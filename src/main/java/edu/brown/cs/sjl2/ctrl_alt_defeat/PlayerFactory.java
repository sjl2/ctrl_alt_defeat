package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class PlayerFactory {

  private DBManager database;
  private Map<String, Player> players;

  /**
   * Factory object for players to ensure that there is only one object for a
   * player.
   * @param db The database manager needed to query for a player
   */
  PlayerFactory(DBManager db) {
    this.database = db;
    this.players = new HashMap<>();
  }

  /**
   * Getter for a player from its id.
   * @param id The id of a player
   * @return Returns the player object corresponding to the id.
   */
  public Player getPlayer(String id) {
    Player p = players.get(id);
    return p != null ? p : database.getPlayer(id);
  }

}
