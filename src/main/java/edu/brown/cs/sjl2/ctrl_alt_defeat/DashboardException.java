package edu.brown.cs.sjl2.ctrl_alt_defeat;

/**
 * An Exception representing errors that are inherent to dashboard operations.
 *
 * @author sjl2
 *
 */
public class DashboardException extends Exception {

  /**
   * Constructor for a Dashboard Exception.
   *
   * @param string
   *          The error message associated with this particular exception.
   */
  public DashboardException(String string) {
    super(string);
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

}
