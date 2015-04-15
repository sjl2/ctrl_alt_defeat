package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.PlayerFactory;

public class Team {
  private int id;
  private String name;
  private String primary;
  private String secondary;
  private Map<Integer, Player> playerIds;
  private Map<String, Player> playerNames;
  private PlayerFactory pf;

  public Team(
      int id,
      String name,
      String primary,
      String secondary,
      PlayerFactory pf) {

    this.id = id;
    this.name =  name;
    this.primary = primary;
    this.secondary = secondary;
    this.pf = pf;

    playerIds = new HashMap<>();
    playerNames = new HashMap<>();


  }

  public int getId() {
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





}
