package edu.brown.cs.sjl2.ctrl_alt_defeat.trie;

/**A class that can hold two objects.
 *
 * @author nickgoelz
 *
 * @param <S1> The first object
 * @param <S2> The second object
 */
public class Pair<S1, S2> {
  private S1 first;
  private S2 second;
  /**A constructor requiring both objects.
   *
   * @param a an S1 to be the first object
   * @param b an S2 to be the second
   */
  public Pair(S1 a, S2 b) {
    this.first = a;
    this.second = b;
  }
  /**Getter for the first object.
   *
   * @return the first object.
   */
  public S1 getFirst() {
    return first;
  }
  /**Getter for the second object.
   *
   * @return the second object.
   */
  public S2 getSecond() {
    return second;
  }
  /**Performs equality check.
   * Pairs are equal if both their first and second object are equal
   * @param o an object o.
   * @return true of this is equal to o, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) {
      return false;
    } else {
      return (first.equals(((Pair<?, ?>) o).getFirst())
          && second.equals(((Pair<?, ?>) o).getSecond()));
    }
  }
  /**Returns an int for hashing.
   *
   * @return the sum of the hashcodes of the two objects
   */
  @Override
  public int hashCode() {
    return first.hashCode() + second.hashCode();
  }
  /**A setter for the second object.
   *
   * @param s the new second object
   */
  public void setSecond(S2 s) {
    second = s;
  }
  /**A setter for the first object.
   *
   * @param s the new first object
   */
  public void setFirst(S1 s) {
    first = s;
  }
  /**returns a string representation of the pair.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return "<" + first + ", " + second + ">";
  }

}
