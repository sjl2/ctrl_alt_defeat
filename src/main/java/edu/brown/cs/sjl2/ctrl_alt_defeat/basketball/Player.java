package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Link;

/**
 * The class represeting a player.
 *
 * @author sjl2
 *
 */
public class Player {
  private int id;
  private String name;
  private int teamID;
  private String teamName;
  private int number;
  private boolean current;

  /**
   * Constructs a player with the parameters below.
   * @param id The database id of the player.
   * @param name The player's name.
   * @param number The player's jersey number.
   * @param teamID The id of the team for the player.
   * @param teamName The name of the team.
   * @param current Boolean true if the player is currently playing.
   */
  public Player(
      int id,
      String name,
      int number,
      int teamID,
      String teamName,
      boolean current) {

    this.id = id;
    this.name = name;
    this.number = number;
    this.teamID = teamID;
    this.teamName = teamName;
    this.current = current;
  }

  /**
   * Getter for the player's database id.
   * @return Returns the int of the player id.
   */
  public int getID() {
    return id;
  }

  /**
   * Getter for the player's name.
   * @return Returns the player's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the player's number.
   * @return Returns the player's number.
   */
  public int getNumber() {
    return number;
  }

  /**
   * Getter for the team's id.
   * @return Returns the team id.
   */
  public int getTeamID() {
    return teamID;
  }

  /**
   * Getter for the name of the team.
   * @return Returns the name of the team.
   */
  public String getTeamName() {
    return teamName;
  }

  /**
   * Getter for whether player is current.
   * @return Returns true if the player is currently playing, false if retired.
   */
  public boolean getCurrent() {
    return current;
  }

  @Override
  public String toString() {
    return name + " (#" + number + ") " + getID();
  }

  /**
   * Getter for a link representing the player.
   * @return Returns the link of the player.
   */
  public Link getLink() {
    return new Link(id, "/player/view/", name + " #" + number);
  }
}
