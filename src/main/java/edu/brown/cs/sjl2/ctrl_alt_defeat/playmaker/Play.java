package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class Play {

  private int id;
  private String name;
  private int numFrames;
  private Location[][] paths;

  public Play(int id, String name, int numFrames, Location[][] paths) {
    this.id = id;
    this.name = name;
    this.numFrames = numFrames;
    this.paths = paths;
  }
  
  public Play(String name, int numFrames, Location[][] paths) {
    this.id = -1;
    this.name = name;
    this.numFrames = numFrames;
    this.paths = paths;
  }
  
  public Play(int id, String name, int numFrames) {
    this.id = id;
    this.name = name;
    this.numFrames = numFrames;
  }

  public int getID() {
    return this.id;
  }
  public String getName() {
    return this.name;
  }
  public int getNumFrames() {
    return this.numFrames;
  }
  public boolean isNewPlay() {
    return this.id == -1;
  }
  public Location[][] getPaths() {
    return this.paths;
  }
  public void setPaths(Location[][] paths) {
    this.paths = paths;
  }

//  public Path getPathForPosition(BasketballPosition pos) {
//    return paths.get(pos);
//  }
//
//  public void setLocationForPosition(BasketballPosition pos, Location loc, int index) {
//    Path p = paths.get(pos);
//    p.setLocationAtIndex(loc, index);
//  }
//
//  public void moveLocationForPosition(BasketballPosition pos, Location loc, int index) {
//		Path p = paths.get(pos);
//    p.moveLocationAtIndex(loc, index);
//  }
}
