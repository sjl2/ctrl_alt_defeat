package edu.brown.cs.sjl2.ctrl_alt_defeat.trie;

import java.util.Comparator;
import java.util.List;

/**
 * A comparator for the score type. The scores are sorted such that the heap
 * exposes the largest score.
 *
 * @author nickgoelz
 *
 */

public class ScoringComparator implements Comparator<Pair<?, ?>> {

  /**
   * Compares two Pairs of elements. Casts arguments as appropriate for use and
   * compares them first by the first integer, then by the second integer, then
   * by the alphabetic order of the lists of Characters.
   *
   *
   * @param pair The first pair to be compared
   * @param p The second pair to be compared
   * @return -1 if the first element is greater, 0 if they are equal, 1 if the
   *         second is geater.
   *
   */
  @Override
  public int compare(Pair<?, ?> pair,
      Pair<?, ?> p) {
    Pair<List<Character>, Pair<Integer, Integer>> pairCast =
        (Pair<List<Character>, Pair<Integer, Integer>>) pair;
    Pair<List<Character>, Pair<Integer, Integer>> pCast =
        (Pair<List<Character>, Pair<Integer, Integer>>) p;
    int bigram = Integer.compare(pairCast.getSecond().getFirst(),
        pCast.getSecond().getFirst());
    if (bigram != 0) {
      return bigram;
    }
    int unigram = Integer.compare(pairCast.getSecond().getSecond(),
        pCast.getSecond().getSecond());
    if (unigram != 0) {
      return unigram;
    }
    String s1 = "";
    for (Character c : pairCast.getFirst()) {
      s1 = s1 + c;
    }
    String s2 = "";
    for (Character c : pCast.getFirst()) {
      s2 = s2 + c;
    }
    int stringComp = -s1.compareToIgnoreCase(s2);
    if (stringComp > 0) {
      return 1;
    } else {
      return -1;
    }

  }
}
