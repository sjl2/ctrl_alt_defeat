package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Position;

public class Lineup {
  private Map<Position, Player> players;
  private Team team;
  
  
  public Map<Position, Player> getPlayers() {
    return players;
  }
  public void setPlayers(Map<Position, Player> players) {
    this.players = players;
  }
  public Team getTeam() {
    return team;
  }
  public void setTeam(Team team) {
    this.team = team;
  }
  
  
}
