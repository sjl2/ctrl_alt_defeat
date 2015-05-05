package edu.brown.cs.sjl2.ctrl_alt_defeat;

/**
 * Location object for a position on the court. x and y are ratios where x
 * is the ratio of the court's sideline and y is the amount of the baseline.
 *
 * @author sjl2
 *
 */
public class Location {
  private double x;
  private double y;

  /**
   * Constructor for a location.
   * @param x Ratio of the sideline.
   * @param y Ratio of the baseline.
   */
  public Location(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Getter for the x value.
   * @return Returns a double between 0 and 1 unless -1 for undefined location.
   */
  public double getX() {
    return x;
  }

  /**
   * Getter for the y value.
   * @return Returns a double between 0 and 1 unless -1 for undefined location.
   */
  public double getY() {
    return y;
  }

  /**
   * Reflects x, y to one side of the court to display information on
   * half a court.
   * @param x The x to reflect
   * @param y The y to reflect
   * @return Returns the new reflected location.
   */
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

  /**
   * Vertically converts x, y to respective halfcourt location.
   * @param x The x to move
   * @param y The y to move
   * @return Returns the adjusted location to a halfcourt view.
   */
  public static Location adjustForVerticalHalfCourt(double x, double y) {
    return rotate(reflect(x, y));
  }

  /**
   * Horizontally converts x, y to respective halfcourt location.
   * @param x The x to move
   * @param y The y to move
   * @return Returns the adjusted location to a halfcourt view.
   */
  public static Location adjustForHorizontalHalfCourt(double x, double y) {
    return reflect(x, y);
  }
}
