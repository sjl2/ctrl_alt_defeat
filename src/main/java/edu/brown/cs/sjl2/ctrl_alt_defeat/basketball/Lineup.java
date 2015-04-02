package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import com.google.common.collect.BiMap;


public class Lineup {
  private BiMap<BasketballPosition, Player> players;
  
  public BiMap<BasketballPosition, Player> getPlayers() {
    return players;
  }

  
  public void sub(Player idIn, Player idOut) {
    players.put(players.inverse().get(idOut), idIn);
  }
  
  
}
