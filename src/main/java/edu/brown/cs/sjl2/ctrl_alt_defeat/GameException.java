package edu.brown.cs.sjl2.ctrl_alt_defeat;

/**
 * An Exception for all errors that are inherent to Live Games.
 *
 * @author sjl2
 *
 */
public class GameException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs A Game Exception for the Game Object.
   * @param message The error message to display with the exception.
   */
  public GameException(String message) {
    super(message);
  }

}
