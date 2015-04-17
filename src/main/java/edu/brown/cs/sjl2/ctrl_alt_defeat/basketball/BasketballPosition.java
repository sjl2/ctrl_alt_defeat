package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

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
	AwayC("Away Center"),
	Ball("Ball");

	private String positionName;

	BasketballPosition(String name) {
		positionName = name;
	}

	/**
	 * Getter for name.
	 * @return String, representing 
	 */
	public String getName() {
	  return positionName;
	}
}
