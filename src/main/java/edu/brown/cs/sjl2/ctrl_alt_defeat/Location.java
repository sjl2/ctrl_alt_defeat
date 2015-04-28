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

  public static Location adjustForShotChart(double x, double y) {
    // Reflection
    if (x > .5) {
      x -= (2 * (x - .5));
      if (y > .5) {
        y -= (2 * (y - .5));
      } else {
        y += (2 * (.5 - y));
      }
    }

    // Rotation
    double _x = y;
    double _y = 1 - (2 * x);

    return new Location(_x, _y);
  }
}
