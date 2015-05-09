package edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

/**
 * The class for a play to be stored in the database. Contains all the
 * information for all the players' movements held as arrays.
 *
 * @author ngoelz
 *
 */
public class Play {

  private String name;
  private int numFrames;
  private Location[][] playerPaths;
  private int[] ballPath;

  /**
   * Constructor for a play.
   *
   * @param name name of the play
   * @param numFrames number of frames in the play
   * @param playerPaths array of location arrays of the paths of the players
   * @param ballPath path of the ball (who has it at each frame)
   */
  public Play(String name, int numFrames, Location[][] playerPaths,
      int[] ballPath) {
    this.name = name;
    this.numFrames = numFrames;
    this.playerPaths = playerPaths;
    this.ballPath = ballPath;
  }

  /**
   * Constructor for play without that fancy stuff.
   *
   * @param name name of the play
   * @param numFrames number of frames
   */
  public Play(String name, int numFrames) {
    this.name = name;
    this.numFrames = numFrames;
  }

  /**
   * Getter for the name.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Getter for number of frames.
   *
   * @return number of frames.
   */
  public int getNumFrames() {
    return this.numFrames;
  }

  /**
   * Getter for the player paths.
   *
   * @return the player paths.
   */
  public Location[][] getPlayerPaths() {
    return this.playerPaths;
  }

  /**
   * Setter for the player paths.
   *
   * @param playerPaths New array to serve as paths.
   */
  public void setPlayerPaths(Location[][] playerPaths) {
    this.playerPaths = playerPaths;
  }

  /**
   * Getter for the ball path.
   *
   * @return the path of the ball between players.
   */
  public int[] getBallPath() {
    return this.ballPath;
  }

  /**
   * Setter for the ball path.
   *
   * @param ballPath The new array of players holding the ball.
   */
  public void setBallPath(int[] ballPath) {
    this.ballPath = ballPath;
  }
}
