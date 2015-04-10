package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Location {
  private int x;
  private int y;

  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

	public Location withX(int newX) {
		return new Location(newX, y);
	}

	public Location withY(int newY) {
		return new Location(x, newY);
	}
//
//	public Location add(Location loc) {
//		return new Location(x + loc.getX(), y + loc.getY());
//	}
//
//	public Location multiply(double s) {
//		return new Location(x * s, y * s);
//	}
//
//	public static Location interpolate(Location loc1, Location loc2, double interp) {
//		return loc1.multiply(interp).add(loc2.multiply(1 - interp));
//	}
}
