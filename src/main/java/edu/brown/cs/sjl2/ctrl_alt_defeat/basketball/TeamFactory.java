package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class TeamFactory {

  private DBManager database;
  private Map<Integer, Team> teams;
  private PlayerFactory pf;

  TeamFactory(DBManager db, PlayerFactory pf) {
    this.database = db;
    this.teams = new HashMap<>();
    this.pf = pf;
  }

  public Team getTeam(int id) {
    Team t = teams.get(id);
    return t != null ? t : database.getTeam(id, pf);
  }
}
