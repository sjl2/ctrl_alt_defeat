package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;


public class Player {
  private int id;
  private String name;
  private int teamID;
  private String teamName;
  private int number;
  private boolean current;

  public Player(
      int id,
      String name,
      int number,
      int teamID,
      String teamName,
      boolean current) {

    this.id = id;
    this.name = name;
    this.number = number;
    this.teamID = teamID;
    this.teamName = teamName;
    this.current = current;
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getNumber() {
    return number;
  }

  public int getTeamID() {
    return teamID;
  }

  public String getTeamName() {
    return teamName;
  }

  public boolean getCurrent() {
    return current;
  }

  @Override
  public String toString() {
    return name + " (#" + number + ") " + getID();
  }

  public Link getLink() {
    return new Link(id, "/player/view/", name + " #" + number);
  }
}
