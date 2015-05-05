package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class Team {
  private int id;
  private String name;
  private String coach;
  private String primary;
  private String secondary;
  private Map<Integer, Player> playerIds;


  public Team(
      int id,
      String name,
      String coach,
      String primary,
      String secondary,
      DBManager db) {

    this.id = id;
    this.name =  name;
    this.coach = coach;
    this.primary = primary;
    this.secondary = secondary;
    this.playerIds = new HashMap<>();

    Collection<Player> players = db.getTeamPlayers(id);
    for (Player p : players) {
      addPlayer(p);
    }
  }

  public Team(
      int id,
      String name,
      String coach,
      String primary,
      String secondary,
      Collection<Player> players) {

    this.id = id;
    this.name =  name;
    this.coach = coach;
    this.primary = primary;
    this.secondary = secondary;
    this.playerIds = new HashMap<>();

    for (Player p : players) {
      addPlayer(p);
    }
  }

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

  public List<Player> getPlayers() {
    List<Player> toReturn = new ArrayList<>();
    Collection<Player> players = playerIds.values();
    for (Player p : players) {
      toReturn.add(p);
    }

    return toReturn;
  }

  public void addPlayer(Player p) {
    playerIds.put(p.getID(), p);
  }

  public void removePlayer(Player p) {
    playerIds.remove(p.getID());
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

  public Link getLink() {
    return new Link(id, "/team/view/", name);
  }
}
