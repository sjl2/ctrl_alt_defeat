package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Team {

  private int id;
  private String name;
  private String color1;
  private String color2;

  public Team(int id, String name, String color1, String color2) {
    this.id = id;
    this.name = name;
    this.color1 = color1;
    this.color2 = color2;
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor1() {
    return color1;
  }

  public String getColor2() {
    return color2;
  }



}
