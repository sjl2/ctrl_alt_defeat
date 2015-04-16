package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;


public class Player {
  private int id;
  private String name;
  private int number;
  private int teamID;

  public Player(int id, String name, int number, int teamID) {
    this.id = id;
    this.name = name;
    this.number = number;
    this.teamID = teamID;
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

  @Override
  public String toString() {
    return name + " (#" + number + ")";
  }
}
