package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import java.util.HashMap;

public class Playmaker {
  
  private HashMap<String, Play> plays;
  
  public Playmaker() {
    this.plays = new HashMap<>();
  }

  public void createPlay(String name) {
    Play p = new Play(name);
    plays.put(name, p);
  }

  public Play getPlay(String name) {
    return plays.get(name);
  }
  
  public void deletePlay(String name) {
    plays.remove(name);
  }
  
  public boolean savePlay(Play play) {
    return false;
    // AS IN SAVE TO DATABASE?
  }
}
