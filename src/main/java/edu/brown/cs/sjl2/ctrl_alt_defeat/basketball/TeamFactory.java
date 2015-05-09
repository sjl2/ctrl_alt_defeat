package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

/**
 * Factory class that caches teams for the database manager.
 *
 * @author sjl2
 *
 */
public class TeamFactory {

  private DBManager db;
  private Map<Integer, Team> teams;

  /**
   * Constructor for a team factory.
   * 
   * @param db The database to retrieve players from.
   */
  public TeamFactory(DBManager db) {
    this.db = db;
    this.teams = new HashMap<>();
  }

  /**
   * Getter for Team by id.
   * 
   * @param id The id of the team
   * @return Returns the team object associated with id.
   */
  public Team getTeam(int id) {
    return teams.get(id);
  }

  /**
   * Gets a team object with the preceding information.
   * 
   * @param id Team id
   * @param name Team's name
   * @param coach Team's coach
   * @param primary Team's primary color
   * @param secondary Team's secondary color
   * @return Returns the Team Object and caches it
   */
  public Team getTeam(
      int id,
      String name,
      String coach,
      String primary,
      String secondary) {

    Team t = new Team(
        id,
        name,
        coach,
        primary,
        secondary,
        db);

    cacheTeam(t);

    return t;
  }

  private void cacheTeam(Team t) {
    teams.put(t.getID(), t);
  }

  /**
   * Returns all teams in the database.
   * 
   * @return Returns links of all teams in the database.
   */
  public List<Link> getAllTeams() {
    return db.getAllTeams();
  }

  /**
   * Getter for all opponents of my teams.
   * 
   * @return Returns links of all opposing teams.
   */
  public List<Link> getOpposingTeams() {
    return db.getOpposingTeams();
  }
}
