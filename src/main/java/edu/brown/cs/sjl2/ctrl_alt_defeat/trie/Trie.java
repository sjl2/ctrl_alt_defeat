package edu.brown.cs.sjl2.ctrl_alt_defeat.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**This is my Trie class, holding words stored such that common prefixes
 * are paths on the tree from the root until the path branches.
 *
 * The class holds a root and also holds a dictionary which is not lightweight
 * - it holds the seen words.  This removes some of the
 * space efficiency of the trie but because the dictionary implements HashMaps,
 * all lookup doesn't suffer.  I included a full dictionary
 * instead of simply pointing to the leaf nodes of the trie to make the
 * dictionary more easily updated (updating which nodes are leaves
 * dynamically as the trie grows seemed unnecessarily complicated to me).
 *
 * @author nickgoelz
 *
 */
public class Trie {
  private Node<Character> root;
  private Map<String, String> capKey;
  private Dictionary dictionary;
  private Boolean usePrefix;
  private Boolean useED;
  private int k;
  private Boolean useWhitespace;
  private Boolean smartRank;
  private int smartAdd = 4;
  private int smartMult = 10;

  /**This is the default constructor for a trie.
   *
   * It sets the root to null and all user related fields to false or 0.
   */
  public Trie() {
    this.root = null;
    this.useED = false;
    this.usePrefix = false;
    this.useWhitespace = false;
    this.smartRank = false;
    this.k = 0;
  }
  /**This is a similar operator but it sets the root to a node equivalent to
   * the initiazation string.
   * This string is "@$" where '@' serves as a unique character
   * to start every word to appear in the trie
   * and '$' appears as a unique termination character.  These
   * characters ensure that a) the root can be treated
   * just like every other node and b) all nodes that represent
   * the end of a word are terminal.
   *
   * @param init The intial characters for the trie.  For my tries
   * it's the string "@$"
   */
  public Trie(List<Character> init) {
    root = new Node<Character>(init, null);
    this.capKey = new HashMap<String, String>();
    this.dictionary = new Dictionary();
    this.useED = false;
    this.usePrefix = false;
    this.useWhitespace = false;
    this.smartRank = false;
    this.k = 0;
  }

  @Override
  /**returns a String representation of the trie.
   *
   * @return a String representing the trie.  Only deals with the
   * graphical trie, not the dictionary.
   */
  public String toString() {
    return root.toString();
  }
  /**Turns on the prefix field.
   *
   * @return returns itself
   */
  public Trie prefixOn() {
    this.usePrefix = true;
    return this;
  }
  /**Turns on the edit distance field.
   *
   * @return returns itself
   */
  public Trie editDistanceOn() {
    this.useED = true;
    return this;
  }
  /**Turns on the whitespace field.
   *
   * @return returns itself
   */
  public Trie whiteSpaceOn() {
    this.useWhitespace = true;
    return this;
  }
  /**Turns on the smart ranking feature.
   *
   * @return returns itself
   */
  public Trie smartRank() {
    this.smartRank = true;
    return this;
  }
  /**Sets the k cutoff associated with L.E.D.
   *
   * @param n The integer k is to be set to
   * @return returns itself
   */
  public Trie setK(Integer n) {
    this.k = n;
    return this;
  }
  /**Adds a list of words to the trie.
   *
   * @param l The list of words, represented as Lists of Characters, to the
   * trie.
   */
  public void addWords(List<List<Character>> l) {
    List<Character> prev = null;
    if (root == null) {
      root = new Node<Character>(l.get(0), null);
      dictionary.addFirstWord(l.get(0));
      prev = l.get(0);
    } else {
      root.addSequence(l.get(0));
      dictionary.addFirstWord(l.get(0));
      prev = l.get(0);
    }
    for (int i = 1; i < l.size(); i++) {
      root.addSequence(l.get(i));
      dictionary.addWord(l.get(i), prev);
      prev = l.get(i);
    }
  }

  /**Adds a single word to the trie.
   *
   * @param word The word to be added.
   * @param prev The previous word (needed for dictionary construction).
   */
  public void addWord(List<Character> word, List<Character> prev) {
    if (root == null) {
      root = new Node<Character>(word, null);
      dictionary.addWord(word, prev);
    } else {
      root.addSequence(word);
      dictionary.addWord(word, prev);
    }
  }
  /**Adds the first word to the dictionary, significant because there's no
   * previous word.
   *
   * @param word the word to be added.
   */
  public void addFirstWord(String w, List<Character> word) {
    capKey.put(StringFormatter.unlist(word), w);
    root.addSequence(word);
    dictionary.addFirstWord(word);
  }


  /**Returns a list of pairs of words representing all pairs of words that
   * could have been intended.
   *
   *
   * @param word The word to be checked to see if two words are contained
   * within it.
   * @return A list of all pairs of words possibly contained in the
   * examined word.
   */
  public List<Pair<List<Character>, List<Character>>> whitespace(
      List<Character> word) {
    ArrayList<Pair<List<Character>, List<Character>>> toReturn =
        new ArrayList<Pair<List<Character>, List<Character>>>();
    Character firstToken = word.get(0);
    Character lastToken = word.get(word.size() - 1);
    List<Character> firstTemp = word.subList(1, word.size() - 1);
    List<Character> first = new ArrayList<Character>();
    for (int i = 0; i < firstTemp.size(); i++) {
      first.add(firstTemp.get(i));
    }
    List<Character> second = new ArrayList<Character>();

    Character changing = first.remove(first.size() - 1);
    second.add(0, changing);

    while (first.size() > 0) {
      List<Character> tempFirst = new ArrayList<Character>();
      tempFirst.add(firstToken);
      tempFirst.addAll(first);
      tempFirst.add(lastToken);

      List<Character> tempLast = new ArrayList<Character>();
      tempLast.add(firstToken);
      tempLast.addAll(second);
      tempLast.add(lastToken);

      if (search(tempFirst) && search(tempLast)) {
        toReturn.add(
            new Pair<List<Character>, List<Character>>(tempFirst, tempLast));
      }

      changing = first.remove(first.size() - 1);
      second.add(0, changing);

    }

    return toReturn;

  }

  /**A search to see if a provided word is in the trie.
   *
   * @param word the query
   * @return true is the word is in the trie, false otherwise.
   */
  public boolean search(List<Character> word) {
    Node<Character> sought = root.searchDown(word);
    return sought != null && sought.terminal();
  }

  /**Slightly relaxed search: returns true if the provided word is a prefix
   * to a word in the trie.
   * It is assumed that the word has been 'treated' already meaning that the
   * trie will not consider
   * its own initiating and terminating characters here.
   *
   * @param word the word being examined.
   * @return true if the word is a prefix to any word in the trie,
   * false otherwise.
   */
  public boolean isPrefix(List<Character> word) {
    return root.searchDown(word) != null;
  }

  /**Returns a list of all words that contain the prefix provided.
   *
   * @param p the prefix
   * @return A list of all words, represented as lists of Characters, which
   * have prefix p
   */
  public List<List<Character>> prefixed(List<Character> p) {
    if (isPrefix(p)) {
      Node<Character> hub = root.searchDown(p);

      List<Character> prefix = hub.getParent().fullList();

      List<List<Character>> suffixes = hub.gatherAllWords();

      List<List<Character>> toReturn = new ArrayList<List<Character>>();
      for (List<Character> l : suffixes) {
        ArrayList<Character> toAdd = new ArrayList<Character>();
        toAdd.addAll(prefix);
        toAdd.addAll(l);
        toReturn.add(toAdd);
      }
      return toReturn;
    } else {
      List<List<Character>> toReturn = new ArrayList<List<Character>>();
      return toReturn;
    }
  }
  /**Getter for the root of the tree.
   *
   * @return the root of the tree.
   */
  public Node<Character> getRoot() {
    return root;
  }
  /**Dynamically computes edit distance for prefixes/words in the tree
   * and returns
   * words which are within k edit distance of the provided word.
   *
   * @param word the query.
   * @param inputK the edit distance limit for returned words.
   * @return A list of words within k of word
   */
  public List<List<Character>> editDistance(List<Character> word, int inputK) {
    int[] a = new int[word.size() + 1];
    for (int i = 0; i < a.length; i++) {
      a[i] = i;
    }
    return getRoot().gatherWithinDistance(word, inputK, a);
  }

  /**A private dictionary class used to store all words for information about.
   * their unigram and bigram counts.
   *
   * @author ngoelz
   *
   */
  public class Dictionary {
    private HashMap<
    List<Character>, Pair<HashMap<List<Character>,
    Pair<List<Character>, Integer>>, Integer>> dictionary;

    /**The constructor for a dictionary.
     *
     */
    public Dictionary() {
      this.dictionary = new HashMap<List<Character>,
          Pair<HashMap<List<Character>,
          Pair<List<Character>, Integer>>, Integer>>();
    }

    /**How a word is added to the dictionary.
     *
     * @param word the word to be added
     * @param prev the previous word (used for bigram counts)
     */
    public void addWord(List<Character> word, List<Character> prev) {
      if (dictionary.get(word) == null) {
        HashMap<List<Character>, Pair<List<Character>, Integer>>
        temp = new HashMap<List<Character>, Pair<List<Character>, Integer>>();
        temp.put(prev, new Pair<List<Character>, Integer>(prev, 1));
        dictionary.put(word, new Pair<HashMap<List<Character>,
            Pair<List<Character>, Integer>>, Integer>(temp, 1));
      } else if (dictionary.get(word).getFirst().get(prev) == null) {
        HashMap<List<Character>, Pair<List<Character>, Integer>>
        temp = dictionary.get(word).getFirst();
        temp.put(prev, new Pair<List<Character>, Integer>(prev, 1));
        dictionary.get(word).setSecond(dictionary.get(word).getSecond() + 1);
      } else {
        dictionary.get(word).getFirst().get(prev).setSecond(
            dictionary.get(word).getFirst().get(prev).getSecond() + 1);
        dictionary.get(word).setSecond(dictionary.get(word).getSecond() + 1);
      }

    }
    /**How the first word of a phrase is added.
     *
     * @param word only takes the word as input
     */
    public void addFirstWord(List<Character> word) {
      if (dictionary.get(word) == null) {
        HashMap<List<Character>, Pair<List<Character>, Integer>> temp =
            new HashMap<List<Character>, Pair<List<Character>, Integer>>();
        temp.put(new ArrayList<Character>(),
            new Pair<List<Character>, Integer>(new ArrayList<Character>(), 1));
        dictionary.put(word, new Pair<HashMap<List<Character>,
            Pair<List<Character>, Integer>>, Integer>(temp, 1));
      } else if (
          dictionary.get(word).getFirst().get(new ArrayList<Character>())
          == null) {
        HashMap<List<Character>, Pair<List<Character>, Integer>>
        temp = dictionary.get(word).getFirst();
        temp.put(new ArrayList<Character>(), new Pair<List<Character>,
            Integer>(new ArrayList<Character>(), 1));
        dictionary.get(word).setSecond(dictionary.get(word).getSecond() + 1);
      } else {
        dictionary.get(word).getFirst()
        .get(new ArrayList<Character>()).setSecond(dictionary.get(word)
            .getFirst().get(new ArrayList<Character>()).getSecond() + 1);
        dictionary.get(word).setSecond(dictionary.get(word).getSecond() + 1);
      }
    }
    /**A getter for the dictionary.
     *
     * @return the mapping of words
     */
    public HashMap<List<Character>, Pair<HashMap<List<Character>,
      Pair<List<Character>, Integer>>, Integer>> getMap() {
      return dictionary;
    }
  }

  /**Getter for the dictionary.
   *
   * @return the dictionary of the trie.
   */
  public Dictionary getDictionary() {
    return dictionary;
  }

/**This is the overall method used by the Main class to analyze words.
 * This method takes in two words as argument and returns the five most likely
 * words that it could be autocorrected to.
 *
 * @param word The input word
 * @param prev The previous word, also provided for bigram scoring
 * @return A list of Pairs of characters and scoring criteria
 */
  public ArrayList<String>
  evaluateWord(List<Character> word, List<Character> prev) {
    PriorityQueue<Pair<List<Character>, Pair<Integer, Integer>>> heap =
        new PriorityQueue<Pair<List<Character>, Pair<Integer, Integer>>>(
            5, new ScoringComparator());
    if (search(word)) {
      conditionallyAdd(heap, new Pair<List<Character>, Pair<Integer, Integer>>(
          word, new Pair<Integer, Integer>(
              Integer.MAX_VALUE, Integer.MAX_VALUE)));
    }

    if (usePrefix) {
      List<Character> prefixOfWord = new ArrayList<Character>();
      for (int i = 0; i < word.size() - 1; i++) {
        prefixOfWord.add(word.get(i));
      }
      List<List<Character>> pfWords = prefixed(prefixOfWord);
      for (List<Character> pfW : pfWords) {
        Pair<Integer, Integer> scores;
        if (!word.equals(pfW)) {
          if (prev == null || getDictionary().dictionary.get(pfW).getFirst().
              get(prev) == null) {
            scores = new Pair<Integer, Integer>(0, getDictionary().dictionary.
                get(pfW).getSecond());
          } else {
            scores = new Pair<Integer, Integer>(getDictionary().dictionary.
                get(pfW).getFirst().get(prev).getSecond(), getDictionary().
                  dictionary.get(pfW).getSecond());
          }
          if (smartRank && pfW.size() < word.size() + smartAdd) {
            scores.setFirst(scores.getFirst() * 2);
            scores.setSecond(scores.getSecond() * 2);
            if (pfW.size() < word.size() + 2) {
              scores.setFirst(scores.getFirst() * smartMult);
              scores.setSecond(scores.getSecond() * smartMult);
            }
          }
          conditionallyAdd(heap, new Pair<List<Character>,
              Pair<Integer, Integer>>(pfW, scores));

        }
      }
    }
    if (useED && (word.size() >= k || !smartRank)) {
      List<List<Character>> edWords = editDistance(word, k);
      for (List<Character> edW : edWords) {
        if (edW.size() > 2 && !prefix(edW, word)) {
          Pair<Integer, Integer> scores;
          if (word.equals(edW)) {
            scores = new Pair<Integer, Integer>(
                Integer.MAX_VALUE, Integer.MAX_VALUE);
          } else if (prev == null || getDictionary().dictionary.
              get(edW).getFirst().get(prev) == null) {
            scores = new Pair<Integer, Integer>(0, getDictionary().
                dictionary.get(edW).getSecond());
          } else {
            scores = new Pair<Integer, Integer>(getDictionary().
                dictionary.get(edW).getFirst().get(prev).getSecond(),
                getDictionary().dictionary.get(edW).getSecond());
          }
          if (word.size() < k + 2 && smartRank) {
            scores.setFirst(scores.getFirst() / smartMult);
            scores.setSecond(scores.getSecond() / smartMult);
          }
          conditionallyAdd(heap, new Pair<List<Character>,
              Pair<Integer, Integer>>(edW, scores));
        }
      }
    }

    if (useWhitespace) {
      List<Pair<List<Character>, List<Character>>> wsWords = whitespace(word);

      for (Pair<List<Character>, List<Character>> p : wsWords) {

        Pair<Integer, Integer> scores;
        if (prev == null || getDictionary().dictionary.get(p.getFirst()).
            getFirst().get(prev) == null) {
          scores = new Pair<Integer, Integer>(0, getDictionary().
              dictionary.get(p.getFirst()).getSecond());
        } else {
          scores = new Pair<Integer, Integer>(getDictionary().
              dictionary.get(p.getFirst()).getFirst().get(prev).getSecond(),
              getDictionary().dictionary.get(p.getFirst()).getSecond());
        }

        ArrayList<Character> forPair = new ArrayList<Character>();
        forPair.addAll(p.getFirst());
        forPair.addAll(p.getSecond());
        conditionallyAdd(heap, new Pair<List<Character>,
            Pair<Integer, Integer>>(forPair, scores));

      }

    }
    ArrayList<String>
    toReturn = new ArrayList<String>();

    while (heap.size() != 0) {
      Pair<List<Character>, Pair<Integer, Integer>> l = heap.poll();
      toReturn.add(0, capKey.get(StringFormatter.unlist(l.getFirst())));
    }

    return toReturn;
  }

  /**A helper method to improve the scoring.
   *
   * @param word a word
   * @param pre a prefix of a word
   * @return A boolean stating if pre is a prefix of word
   */
  private boolean prefix(List<Character> word, List<Character> pre) {
    if (pre.size() > word.size()) {
      return false;
    } else {
      for (int i = 1; i < pre.size() - 1; i++) {
        if (!word.get(i).equals(pre.get(i))) {
          return false;
        }
      }
      return true;
    }
  }

  /**A private helper method updating the heap.
   * Considers the current size of the heap and the proposed addition
   * to the heap to decide whether to update the heap or not.
   *
   * @param heap The heap
   * @param p The proposed addition to the heap
   */
  private void conditionallyAdd(
      PriorityQueue<Pair<List<Character>, Pair<Integer, Integer>>> heap,
      Pair<List<Character>, Pair<Integer, Integer>> p) {
    if (heap.contains(p) || p.getFirst().size() == 2) {
      return;
    }
    if (heap.size() < 5) {
      heap.add(p);
    } else {

      ScoringComparator s = new ScoringComparator();
      if (s.compare(heap.peek(), p) < 0) {
        heap.poll();
        heap.add(p);
      }
    }
  }

}
