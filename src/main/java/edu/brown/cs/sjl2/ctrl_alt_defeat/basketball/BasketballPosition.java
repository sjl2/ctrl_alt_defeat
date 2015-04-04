package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

public enum BasketballPosition {
	HomePG("Home Point Guard"),
	HomeSG("Home Shooting Guard"),
	HomeSF("Home Small Forward"),
	HomePF("Home Power Forward"),
	HomeC("Home Center"),
	AwayPG("Home Point Guard"),
	AwaySG("Home Shooting Guard"),
	AwaySF("Home Small Forward"),
	AwayPF("Home Power Forward"),
	AwayC("Home Center");
	
	private String positionName;

	BasketballPosition(String name) {
		positionName = name;
	}
}
