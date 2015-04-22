package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
//import java.util.Random;

public class Team {
  private int id;
  private String name;
  private String coach;
  private String primary;
  private String secondary;
  public Map<Integer, Player> playerIds;
  private Map<String, Player> playerNames;

  public Team(
      int id,
      String name,
      String coach,
      String primary,
      String secondary,
      PlayerFactory pf) {

    this.id = id;
    this.name =  name;
    this.coach = coach;
    this.primary = primary;
    this.secondary = secondary;

    playerIds = new HashMap<>();
    playerNames = new HashMap<>();

    Collection<Player> players = pf.getTeamPlayers(this);

    for (Player p : players) {
      playerIds.put(p.getID(), p);
      playerNames.put(p.getName(), p);
    }
  }

  public Team(int id, String name) {
    this.id = id;
    this.name = name;
    this.coach = null;
    this.primary = null;
    this.secondary = null;
    this.playerIds = null;
    this.playerNames = null;
  }

//  public Team(Integer i) {
//    Random rn = new Random();
//    this.id = i;
//    this.name = "Team " + i;
//    this.primary = "rgb(" + (rn.nextDouble() * 255) + "," + (rn.nextDouble() * 255) + "," + (rn.nextDouble() * 255) + ")";
//    this.secondary = "rgb(" + (rn.nextDouble() * 255) + "," + (rn.nextDouble() * 255) + "," + (rn.nextDouble() * 255) + ")";;
//    playerIds = new HashMap<>();
//    playerNames = new HashMap<>();
//    for (int j = 0; j < 12; j++) {
//      Player p = new Player(j, i);
//      playerIds.put(j, p);
//      playerNames.put(p.getName(), p);
//    }
//  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPrimary() {
    return primary;
  }

  public String getSecondary() {
    return secondary;
  }

  public Player getPlayerById(int id) {
    return playerIds.get(id);
  }

  public Player getPlayerByName(String name) {
    return playerNames.get(name);
  }

  public Collection<Player> getPlayers() {
    return playerIds.values();
  }


  @Override
  public String toString() {
    String prefix = "";
    if (!coach.isEmpty()) {
      prefix = coach + "\'s ";
    }
    return prefix + name + " (id: " + id + ")";
  }

  public String getCoach() {
    return coach;
  }


}
