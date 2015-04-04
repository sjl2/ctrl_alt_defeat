package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Location {
  private double x;
  private double y;

  Location(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

	public Location withX(double newX) {
		return new Location(newX, y);
	}

	public Location withY(double newY) {
		return new Location(x, newY);
	}

	public Location add(Location loc) {
		return new Location(x + loc.getX(), y + loc.getY());
	}

	public Location multiply(double s) {
		return new Location(x * s, y * s);
	}

	public static Location interpolate(Location loc1, Location loc2, double interp) {
		return loc1.multiply(interp).add(loc2.multiply(1 - interp));
	}
}
