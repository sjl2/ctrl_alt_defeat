package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;


public class Lineup {
  private BiMap<BasketballPosition, Player> players;
  
  public Lineup() {
    this.players = EnumHashBiMap.create(BasketballPosition.class);
  }
  
  public BiMap<BasketballPosition, Player> getPlayers() {
    return players;
  }

  
  public void sub(Player pIn, Player pOut) throws ScoreboardException {
    BasketballPosition b = players.inverse().get(pOut);
    if (b != null) {
      players.put(b, pIn);
    } else {
      throw new ScoreboardException("Illegal substitution: player not on court");
    }
  }
  
  
}
