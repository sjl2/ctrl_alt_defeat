package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;


public class Player {
  private int id;
  private String name;
  private int number;
  private String teamName;
  private int teamID;
  private boolean current;

  public Player(int id, String name, int number, int teamID, String teamName, boolean current) {
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
}
