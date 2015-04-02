package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import com.google.common.collect.BiMap;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Position;

public class Lineup {
  private BiMap<Position, Player> players;
  
  
  public BiMap<Position, Player> getPlayers() {
    return players;
  }
  public void setPlayers(BiMap<Position, Player> players) {
    this.players = players;
  }
  
  public void sub(Player idIn, Player idOut) {
    players.put(players.inverse().get(idOut), idIn);
  }
  
  
}
