package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class Play {

  private String name;
  private int numFrames;
  private Location[][] playerPaths;
  private Location[] ballPath;

  public Play(String name, int numFrames, Location[][] playerPaths, Location[] ballPath) {
    this.name = name;
    this.numFrames = numFrames;
    this.playerPaths = playerPaths;
    this.ballPath = ballPath;
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
  public Location[][] getPlayerPaths() {
    return this.playerPaths;
  }
  public void setPlayerPaths(Location[][] playerPaths) {
    this.playerPaths = playerPaths;
  }
  public Location[] getBallPath() {
    return this.ballPath;
  }
  public void setBallPath(Location[] ballPath) {
    this.ballPath = ballPath;
  }
}
