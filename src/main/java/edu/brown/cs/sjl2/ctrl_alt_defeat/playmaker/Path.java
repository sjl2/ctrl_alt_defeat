package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import java.util.LinkedList;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class Path {
  
  private List<Location> path;
  
  public Path() {
    this.path = new LinkedList<>();
  }

  public Location getLocationAtTime(double time) throws IndexOutOfBoundsException {
		int index1 = (int) Math.floor(time * Playmaker.FRAME_RATE);
		int index2 = (int) Math.ceil(time * Playmaker.FRAME_RATE);
		double interp = (time * Playmaker.FRAME_RATE) - index1;
		Location loc1 = path.get(index1);
		Location loc2 = path.get(index2);
    return Location.interpolate(loc1, loc2, interp);
  }

	public Location getLocationAtIndex(int index) throws IndexOutOfBoundsException {
		return path.get(index);
	}
  
  public void setLocationAtIndex(Location loc, int index) {
		if(index < path.size()) {
			path.set(index, loc);
		} else {
			for(int i = 0; i < index + 1 - path.size(); i++) {
				path.add(loc);
			}
		}
	}
  
  public void moveLocationAtIndex(Location loc, int index) throws IndexOutOfBoundsException {
		path.set(index, path.get(index).add(loc));
  }
  
  public int size() {
    return path.size();
  }
  
  public double getMaxTime() {
    return path.size() / Playmaker.FRAME_RATE;
  }
  
}
