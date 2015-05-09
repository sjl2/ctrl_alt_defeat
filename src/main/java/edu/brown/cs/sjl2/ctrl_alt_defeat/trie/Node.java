package edu.brown.cs.sjl2.ctrl_alt_defeat.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The node class used by the Trie.
 *
 * @author nickgoelz
 *
 * @param <T> T was a generic when I first started this project. I ended up
 *          leaving it generic because the Nodes do not need to know that
 *          they're dealing with Strings/Characters.
 */
public class Node<T> {
  private List<T> sequence;
  private HashMap<T, Node<T>> children;
  private boolean isTerminal;
  private Node<T> parent;

  /**
   * The constructor for a new Node.
   *
   * @param list The sequence that this Node represents.
   * @param parent The parent node for this Node.
   */
  public Node(List<T> list, Node<T> parent) {
    this.parent = parent;
    this.children = new HashMap<T, Node<T>>();
    this.sequence = list;
    this.isTerminal = true;
  }

  /**
   * Is this node a terminal node.
   *
   * @return true if this node is terminal, false otherwise
   */
  public boolean terminal() {
    return isTerminal;
  }

  /**
   * Getter for the sequence field of the Node.
   *
   * @return the sequence.
   */
  public List<T> getSequence() {
    return sequence;
  }

  /**
   * Returns all words 'under' this Node in the trie.
   *
   * @return A list of all words which pass through this node in the trie.
   */
  public List<List<T>> gatherAllWords() {
    List<List<T>> toReturn = new ArrayList<List<T>>();
    if (isTerminal) {
      toReturn.add(sequence);
      return toReturn;
    } else {
      for (Node<T> child : children.values()) {
        List<List<T>> childWords = child.gatherAllWords();
        for (List<T> l : childWords) {
          List<T> toAdd = new ArrayList<T>();
          toAdd.addAll(sequence);
          toAdd.addAll(l);
          toReturn.add(toAdd);
        }
      }
    }
    return toReturn;
  }

  /**
   * A void method which passes a new sequence through this node. If the
   * sequence entirely contains the sequence given with this node, then the node
   * is unchanged either a new child is created with the remainded of the
   * sequence or the remainded of the sequence is passed through the appropriate
   * child. If the sequence differs from the Node's sequence then the Node must
   * be split, so the Node's sequence is changed to a new, smaller, sequence
   * which has two children (the remainder of the Node and the Node's old
   * children and a new Node representing the rest of the input sequence).
   *
   * @param newSequence A new sequence of T's (characters for the Trie).
   */
  public void addSequence(List<T> newSequence) {
    Iterator<T> iterateOld = sequence.iterator();
    Iterator<T> iterateNew = newSequence.iterator();
    T currOld = iterateOld.next();
    T currNew = iterateNew.next();
    if (currOld.equals(currNew)) {
      List<T> mutual = new ArrayList<T>();
      mutual.add(currOld);
      boolean shouldSplit = false;
      while (!shouldSplit && iterateOld.hasNext() && iterateNew.hasNext()) {
        currOld = iterateOld.next();
        currNew = iterateNew.next();
        if (currOld.equals(currNew)) {
          mutual.add(currOld);
        } else {
          shouldSplit = true;
        }
      }

      if (shouldSplit) {
        List<T> listForCurrent = mutual;
        List<T> listForRemainder = new ArrayList<T>();
        listForRemainder.add(currOld);
        while (iterateOld.hasNext()) {
          listForRemainder.add(iterateOld.next());
        }
        List<T> listForNewNode = new ArrayList<T>();
        listForNewNode.add(currNew);
        while (iterateNew.hasNext()) {
          listForNewNode.add(iterateNew.next());
        }

        HashMap<T, Node<T>> currChildren = children;
        children = new HashMap<T, Node<T>>();
        children.put(listForNewNode.get(0),
            new Node<T>(listForNewNode, this));
        children.put(listForRemainder.get(0),
            new Node<T>(listForRemainder, this));
        this.sequence = listForCurrent;
        this.isTerminal = false;

        Node<T> tweak = children.get(listForRemainder.get(0));
        tweak.setChildren(currChildren);

      } else if (iterateNew.hasNext()) {
        List<T> listForNewNode = new ArrayList<T>();
        T temp = iterateNew.next();
        listForNewNode.add(temp);
        while (iterateNew.hasNext()) {
          listForNewNode.add(iterateNew.next());
        }
        boolean found = false;
        for (Node<T> n : children.values()) {
          if (n.getSequence().get(0).equals(temp)) {
            n.addSequence(listForNewNode);
            found = true;
          }
        }
        if (!found) {
          children.put(listForNewNode.get(0),
              new Node<T>(listForNewNode, this));
        }
      } else if (iterateOld.hasNext()) {
        System.out.println("ERROR: input error");
      }

    }

  }

  /**
   * A method recursively returns all words that are within k edit distance
   * units of a query word beneath this node in the tree.
   *
   * @param word the query
   * @param k the distance
   * @param a an array of ints representing the current state of the 2D dynamic
   *          programming table that is implicitely constructed here.
   * @return the list of all words that are beneath this node within k units of
   *         word
   */
  public List<List<T>> gatherWithinDistance(List<T> word, int k, int[] a) {
    int[] b = new int[a.length];
    for (int i = 0; i < getSequence().size(); i++) {
      b[0] = a[0] + 1;
      for (int j = 1; j <= word.size(); j++) {
        if (word.get(j - 1).equals(getSequence().get(i))) {
          b[j] = a[j - 1];
        } else {
          b[j] = Math.min(Math.min(a[j - 1], a[j]), b[j - 1]) + 1;
        }
      }
      a = b;
      b = new int[a.length];
    }
    ArrayList<List<T>> toReturn = new ArrayList<List<T>>();
    if (terminal()) {
      if (a[a.length - 1] <= k) {
        ArrayList<T> l = new ArrayList<T>();
        for (int i = 0; i < getSequence().size(); i++) {
          l.add(getSequence().get(i));
        }
        toReturn.add(l);
      }
    } else {
      boolean proceed = false;
      for (int i = 0; i < a.length; i++) {
        if (a[i] <= k) {
          proceed = true;
        }
      }
      if (proceed) {
        for (Node<T> n : getChildren().values()) {
          List<List<T>> childWords = n.gatherWithinDistance(word, k, a);
          for (List<T> l : childWords) {
            for (int i = getSequence().size() - 1; i >= 0; i--) {
              l.add(0, getSequence().get(i));
            }
            toReturn.add(l);
          }
        }
      }
    }
    return toReturn;
  }

  /**
   * A setter for the HashMap of children stored at each node. Also udatees the
   * isTerminal field if the new set of children is of size 0.
   *
   * @param c A new set of children for the node.
   */
  public void setChildren(HashMap<T, Node<T>> c) {
    this.children = c;
    for (Node<T> t : c.values()) {
      t.parent = this;
    }
    if (c.size() != 0) {
      this.isTerminal = false;
    }
  }

  /**
   * Returns the full list represented by this node in the tree.
   *
   * @return a List of T (character) representing what would be searched in the
   *         trie to arrive at this Node.
   */
  public List<T> fullList() {
    List<T> toReturn = new ArrayList<T>();
    for (int i = 0; i < getSequence().size(); i++) {
      toReturn.add(getSequence().get(i));
    }
    Node<T> temp = parent;
    while (temp != null) {
      for (int i = temp.getSequence().size() - 1; i >= 0; i--) {
        toReturn.add(0, temp.getSequence().get(i));
      }
      temp = temp.getParent();
    }
    return toReturn;
  }

  /**
   * A recursive search that seeks a Node below (or at) this Node represented by
   * a given sequence.
   *
   * @param word the sought word
   * @return the Node represented by word or null if the word is not beneath
   *         this Node.
   */
  public Node<T> searchDown(List<T> word) {
    Iterator<T> nodeIt = sequence.iterator();
    Iterator<T> wordIt = word.iterator();
    while (wordIt.hasNext() && nodeIt.hasNext()) {
      if (!nodeIt.next().equals(wordIt.next())) {
        return null;
      }
    }
    if (wordIt.hasNext()) {
      T temp = wordIt.next();
      if (children.get(temp) != null) {
        ArrayList<T> n = new ArrayList<T>();
        n.add(temp);
        while (wordIt.hasNext()) {
          n.add(wordIt.next());
        }
        return children.get(temp).searchDown(n);
      } else {
        return null;
      }
    } else {
      return this;
    }
  }

  /**
   * A getter for the parent field.
   *
   * @return the Node which is the parent of this node.
   */
  public Node<T> getParent() {
    return parent;
  }

  /**
   * A getter for the children field.
   *
   * @return the HashMap representing the children of this Node.
   */
  public HashMap<T, Node<T>> getChildren() {
    return children;
  }

  /**
   * A method returning the string representation of this Node.
   *
   * @return A string representing the sequence, whether the node is terminal,
   *         and all of its children
   *
   */
  @Override
  public String toString() {
    return sequence.toString() + isTerminal + children.toString();
  }

}
