package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import java.util.HashMap;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;

public class Play {

  private String name;
  private HashMap<BasketballPosition, Path> paths;

  public Play(String name) {
    this.name = name;
    this.paths = new HashMap<>();
  }

  public String getName() {
    return this.name;
  }

  public Path getPathForPosition(BasketballPosition pos) {
    return paths.get(pos);
  }

  public void setLocationForPosition(BasketballPosition pos, Location loc, int index) {
    Path p = paths.get(pos);
    p.setLocationAtIndex(loc, index);
  }

  public void moveLocationForPosition(BasketballPosition pos, Location loc, double time) {
		Path p = paths.get(pos);
    p.moveLocationAtIndex(loc, index);
  }
}
