package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import java.util.HashMap;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class Path {
  
  private Map<Double, Location> path;
  private double maxTime;
  
  public Path() {
    this.path = new HashMap<Double, Location>();
    this.maxTime = 0;
  }

  public Location getLocationAtTime(double time) {
    return path.get(time);
  }
  
  public void setLocationAtTime(Location loc, double time) {
    path.put(time, loc);
    if (time > maxTime) {
      maxTime = time;
    }
    
  }
  
  public void moveLocationAtTime(Location loc, double time) {
    // WHAT IS THIS FUNCTION DOING?
  }
  
  public int size() {
    return path.size();
  }
  
  public double getMaxTime() {
    return this.maxTime;
  }
  
}
