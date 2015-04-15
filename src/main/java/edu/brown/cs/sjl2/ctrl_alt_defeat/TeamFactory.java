package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class TeamFactory {

  private DBManager database;
  private Map<Integer, Team> teams;

  TeamFactory(DBManager db) {
    this.database = db;
    this.teams = new HashMap<>();
  }

  public Team getTeam(int id) {
    Team t = teams.get(id);
    return t != null ? t : database.getTeam(id);
  }
}
