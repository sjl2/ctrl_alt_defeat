package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class TeamFactory {

  private DBManager db;
  private Map<Integer, Team> teams;

  public TeamFactory(DBManager db) {
    this.db = db;
    this.teams = new HashMap<>();
  }

  public Team getTeam(int id) {
    return teams.get(id);
  }

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

  public List<Link> getAllTeams() {
    return db.getAllTeams();
  }

  public List<Link> getOpposingTeams() {
    return db.getOpposingTeams();
  }
}
