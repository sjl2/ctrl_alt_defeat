package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.List;

public class Bench {
  private List<Player> players;
  private Team team;
  public List<Player> getPlayers() {
    return players;
  }
  public void setPlayers(List<Player> players) {
    this.players = players;
  }
  public Team getTeam() {
    return team;
  }
  public void setTeam(Team team) {
    this.team = team;
  }
}
