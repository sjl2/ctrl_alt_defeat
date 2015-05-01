package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Location {
  private double x;
  private double y;

  public Location(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
  
  private static Location reflect(double x, double y) {
    if (x > .5) {
      x -= (2 * (x - .5));
      if (y > .5) {
        y -= (2 * (y - .5));
      } else {
        y += (2 * (.5 - y));
      }
    }
    
    return new Location(x, y);
  }
  
  private static Location rotate(Location loc) {
    double _x = loc.getY();
    double _y = 1 - (2 * loc.getX());
    
    return new Location(_x, _y);
  }

  public static Location adjustForVerticalHalfCourt(double x, double y) {
    return rotate(reflect(x, y));
  }

  public static Location adjustForHorizontalHalfCourt(double x, double y) {
    return reflect(x, y);
  }
}
