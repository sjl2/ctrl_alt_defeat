package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class PlayerFactory {

  private DBManager db;
  private Map<Integer, Player> players;

  /**
   * Factory object for players to ensure that there is only one object for a
   * player.
   * @param db The database manager needed to query for a player
   */
  public PlayerFactory(DBManager db) {
    this.db = db;
    this.players = new HashMap<>();
  }

  /**
   * Getter for a player from its id.
   * @param id The id of a player
   * @return Returns the player object corresponding to the id.
   */
  public Player getPlayer(int id) {
    Player p = players.get(id);
    if(p == null) {
      p = db.getPlayer(id);
      players.put(p.getID(), p);
    }
    return p;
  }

  public Collection<Player> getTeamPlayers(Team team) {
    Collection<Player> players = new ArrayList<>();
    Collection<Integer> ids = db.getTeamPlayers(team);

    for (int id : ids)  {
      players.add(getPlayer(id));
    }

    return players;
  }

  public Player addPlayer(String name, int teamID, int number, boolean current) {
    Player p = new Player(db.getNextID("player"), name, number, teamID, db.getTeamNameByID(teamID), current);
    db.savePlayer(p);

    players.put(p.getID(), p);

    return p;
  }

}
