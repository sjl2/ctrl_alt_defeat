package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class Play {

  private String name;
  private int numFrames;
  private Location[][] paths;

  public Play(String name, int numFrames, Location[][] paths) {
    this.name = name;
    this.numFrames = numFrames;
    this.paths = paths;
  }

  public Play(String name, int numFrames) {
    this.name = name;
    this.numFrames = numFrames;
  }

  public String getName() {
    return this.name;
  }
  public int getNumFrames() {
    return this.numFrames;
  }
  public Location[][] getPaths() {
    return this.paths;
  }
  public void setPaths(Location[][] paths) {
    this.paths = paths;
  }
}
