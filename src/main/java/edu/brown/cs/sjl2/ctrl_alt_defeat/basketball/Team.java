package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

/**
 * The class representing a basetkball team.
 *
 * @author sjl2
 *
 */
public class Team {
  private int id;
  private String name;
  private String coach;
  private String primary;
  private String secondary;
  private Map<Integer, Player> playerIds;

  /**
   * Constructor for a team object.
   * 
   * @param id The id of the team
   * @param name The name of the team
   * @param coach The name of the team's coach
   * @param primary The primary color of the team (preferably hex)
   * @param secondary The secondary color of the team (preferably hex)
   * @param db The database to grab the team players from.
   */
  public Team(
      int id,
      String name,
      String coach,
      String primary,
      String secondary,
      DBManager db) {

    this.id = id;
    this.name = name;
    this.coach = coach;
    this.primary = primary;
    this.secondary = secondary;
    this.playerIds = new HashMap<>();

    Collection<Player> players = db.getTeamPlayers(id);
    for (Player p : players) {
      addPlayer(p);
    }
  }

  /**
   * Constructor for a team when the players are already known.
   * 
   * @param id The id of the team
   * @param name The name of the team
   * @param coach The name of the coach
   * @param primary The primary color (preferably hex)
   * @param secondary The secondary color (preferably hex)
   * @param players The collection of players.
   */
  public Team(
      int id,
      String name,
      String coach,
      String primary,
      String secondary,
      Collection<Player> players) {

    this.id = id;
    this.name = name;
    this.coach = coach;
    this.primary = primary;
    this.secondary = secondary;
    this.playerIds = new HashMap<>();

    for (Player p : players) {
      addPlayer(p);
    }
  }

  /**
   * Getter for a team's id.
   * 
   * @return Returns the int of a team's id.
   */
  public int getID() {
    return id;
  }

  /**
   * Getter for a team's name.
   * 
   * @return Returns the name of the team.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter of the primary color.
   * 
   * @return Returns the string of the primary color.
   */
  public String getPrimary() {
    return primary;
  }

  /**
   * Getter for the secondary color.
   * 
   * @return Returns the string of the secondary color.
   */
  public String getSecondary() {
    return secondary;
  }

  /**
   * Gets a player on the team by its id.
   * 
   * @param id The id of the player.
   * @return Returns the player object.
   */
  public Player getPlayerById(int id) {
    return playerIds.get(id);
  }

  /**
   * Getter for the players of a team.
   * 
   * @return Returns a list of players on the team.
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(playerIds.values());
  }

  /**
   * Adds a player to the team.
   * 
   * @param p The player to be added.
   */
  public void addPlayer(Player p) {
    playerIds.put(p.getID(), p);
  }

  /**
   * Removes a player from the team.
   * 
   * @param p The player to be removed.
   */
  public void removePlayer(Player p) {
    playerIds.remove(p.getID());
  }

  @Override
  public String toString() {
    String prefix = "";
    if (!coach.isEmpty()) {
      prefix = coach + "\'s ";
    }
    return prefix + name + " (id: " + id + ")";
  }

  /**
   * Getter for the coach of the team.
   * 
   * @return Returns the coach's name.
   */
  public String getCoach() {
    return coach;
  }

  /**
   * Getter for a team's link.
   * 
   * @return Returns the link for this particular team's page.
   */
  public Link getLink() {
    return new Link(id, "/team/view/", name);
  }
}
