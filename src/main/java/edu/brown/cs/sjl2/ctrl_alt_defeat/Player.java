package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Player {
  private int id;
  private String name;
  private int number;
  private Team team;
  
  
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getNumber() {
    return number;
  }
  public void setNumber(int number) {
    this.number = number;
  }
  public Team getTeam() {
    return team;
  }
  public void setTeam(Team team) {
    this.team = team;
  }
}
