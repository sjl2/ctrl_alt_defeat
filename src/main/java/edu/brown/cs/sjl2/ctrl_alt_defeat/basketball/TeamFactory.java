package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class TeamFactory {

  private DBManager db;
  private Map<Integer, Team> teams;
  private PlayerFactory pf;

  public TeamFactory(DBManager db, PlayerFactory pf) {
    this.db = db;
    this.teams = new HashMap<>();
    this.pf = pf;
  }

  public Team getTeam(int id) {
    Team t = teams.get(id);
    if(t == null) {
      t = db.getTeam(id, pf);
      teams.put(t.getID(), t);
    }
    return t;
  }

  public Team addTeam(
      String name,
      String coach,
      String primary,
      String secondary,
      boolean myTeam) {

    Team t =
        new Team(db.getNextID("team"), name, coach, primary, secondary, pf);
    db.saveTeam(t, myTeam);

    teams.put(t.getID(), t);

    return t;
  }

  public void addPlayer(Player p) {
    Team t = getTeam(p.getTeamID());
    t.addPlayer(p);
  }

  public List<Team> getAllTeams() {
    return db.getAllTeams();
  }

  public List<Team> getOpposingTeams() {
    return db.getOpposingTeams();
  }

}
