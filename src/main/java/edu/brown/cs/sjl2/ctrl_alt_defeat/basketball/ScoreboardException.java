package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

public class ScoreboardException extends Exception{

  
  /**The default constructor.
  *
  */
 public ScoreboardException() {

 }

 /**A constructor that takes a message.
  *
  * @param message A message defined as in the super class.
  */
 public ScoreboardException(String message) {
   super(message);
 }
 /**A constructor that takes a throwable.
 *
 * @param cause A throwable defined as in the super class.
 */
 public ScoreboardException(Throwable cause) {
   super(cause);
 }
 /**A constructor that takes a message and a throwable.
 *
 * @param message A message defined as in the super class.
 * @param cause A throwable defined as in the super class.
 */
 public ScoreboardException(String message, Throwable cause) {
   super(message, cause);
 }
}
