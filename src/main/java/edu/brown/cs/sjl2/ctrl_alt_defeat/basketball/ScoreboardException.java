package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

/**
 * Exception thrown for errors related to the scoreboard/game state usually
 * stored in a scorebaord.
 *
 * @author sjl2
 *
 */
public class ScoreboardException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * A constructor that takes a message.
   * 
   * @param message A message defined as in the super class.
   */
  public ScoreboardException(String message) {
    super(message);
  }

  /**
   * A constructor that takes a throwable.
   * 
   * @param cause A throwable defined as in the super class.
   */
  public ScoreboardException(Throwable cause) {
    super(cause);
  }

  /**
   * A constructor that takes a message and a throwable.
   * 
   * @param message A message defined as in the super class.
   * @param cause A throwable defined as in the super class.
   */
  public ScoreboardException(String message, Throwable cause) {
    super(message, cause);
  }
}
