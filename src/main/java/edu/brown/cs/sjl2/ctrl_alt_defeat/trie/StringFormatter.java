package edu.brown.cs.sjl2.ctrl_alt_defeat.trie;

import java.util.ArrayList;
import java.util.List;

/**
 * A class containing useful utilities for trie construction.
 *
 * @author ngoelz
 *
 */
public final class StringFormatter {

  private StringFormatter() {
  }

  /**
   * A method that takes a string and returns a list of characters ready for
   * insertion into the trie.
   *
   * @param str
   *          the string
   * @return the string with '@' prepended and '$' appended
   */
  public static List<Character> treat(String str) {
    ArrayList<Character> toReturn = new ArrayList<Character>();
    toReturn.add('@');
    for (int i = 0; i < str.length(); i++) {
      toReturn.add(str.toCharArray()[i]);
    }
    toReturn.add('$');
    return toReturn;
  }

  /**
   * Simple list representation of a string.
   *
   * @param str
   *          the string.
   * @return a list containing each character in the string.
   */
  public static List<Character> listify(String str) {
    ArrayList<Character> toReturn = new ArrayList<Character>();
    for (int i = 0; i < str.length(); i++) {
      toReturn.add(str.toCharArray()[i]);
    }
    return toReturn;
  }

  /**
   * Inverse of treat.
   *
   * @param c
   *          the character list
   * @return the string hidden inside
   */
  public static String unlist(List<Character> c) {
    String s = "";
    for (int i = 1; i < c.size() - 1; i++) {
      if (c.get(i) == '$') {
        s = s + ' ';
        i++;
      } else {
        s = s + c.get(i);
      }
    }
    return s;
  }

}
