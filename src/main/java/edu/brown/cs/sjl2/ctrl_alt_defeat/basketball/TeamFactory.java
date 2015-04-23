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
    return t != null ? t : db.getTeam(id, pf);
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

  public List<Team> getTeams() {
    return db.getTeams();
  }
}
