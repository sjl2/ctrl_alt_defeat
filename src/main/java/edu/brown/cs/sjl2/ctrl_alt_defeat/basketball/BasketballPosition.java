package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

/**
 * An enum class to represent the basketball positions.
 *
 * @author sjl2
 *
 */
public enum BasketballPosition {
  HomePG("Home Point Guard"),
  HomeSG("Home Shooting Guard"),
  HomeSF("Home Small Forward"),
  HomePF("Home Power Forward"),
  HomeC("Home Center"),
  AwayPG("Away Point Guard"),
  AwaySG("Away Shooting Guard"),
  AwaySF("Away Small Forward"),
  AwayPF("Away Power Forward"),
  AwayC("Away Center");

  private String positionName;

  /**
   * Constructor for a Basketball Position with a name of the position.
   *
   * @param name
   */
  BasketballPosition(String name) {
    positionName = name;
  }

  /**
   * Getter for name.
   *
   * @return String, representing
   */
  public String getName() {
    return positionName;
  }
}
