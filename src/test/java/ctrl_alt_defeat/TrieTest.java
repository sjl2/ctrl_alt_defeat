package ctrl_alt_defeat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Node;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.StringFormatter;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;

public class TrieTest {

  @Test
  public void constructionTests() {
    ArrayList<Character> init = new ArrayList<Character>();
    init.add('@');
    init.add('$');
    Trie trie = new Trie(init);
    assertTrue(trie.getRoot().getSequence().get(0) == '@');
    assertTrue(trie.getRoot().getSequence().get(1) == '$');
    ArrayList<List<Character>> strings = new ArrayList<List<Character>>();
    strings.add(StringFormatter.listify("@test$"));
    trie.addWords(strings);

    assertTrue(trie.getRoot().getChildren().size() == 2);
    assertTrue(trie.getRoot().getChildren().get('$').terminal());
    assertTrue(trie.getRoot().getChildren().get('t').terminal());
    assertTrue(trie.getRoot().getChildren().get('t').getSequence().get(1) == 'e');
  }
  
  @Test
  public void splittingTest() {
    ArrayList<Character> init = new ArrayList<Character>();
    init.add('@');
    init.add('$');
    Trie trie = new Trie(init);
    ArrayList<List<Character>> strings = new ArrayList<List<Character>>();
    strings.add(StringFormatter.listify("@a$"));
    strings.add(StringFormatter.listify("@ab$"));
    strings.add(StringFormatter.listify("@abc$"));
    strings.add(StringFormatter.listify("@abd$"));
    strings.add(StringFormatter.listify("@abcd$"));
    strings.add(StringFormatter.listify("@abcde$"));
    strings.add(StringFormatter.listify("@abdce$"));
    
    trie.addWords(strings);
    assertTrue(trie.getRoot().getChildren().keySet().contains('a'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().containsKey('$'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().containsKey('b'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('$').terminal());
    assertTrue(!trie.getRoot().getChildren().get('a').getChildren().get('b').terminal());
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('$'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('c'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('d'));

    trie = new Trie(init);
    ArrayList<List<Character>>  rev = new ArrayList<List<Character>>();
    for (int i=strings.size(); i > 0; i--) {
      rev.add(strings.get(i-1));
    }
    trie.addWords(rev);
    assertTrue(trie.getRoot().getChildren().keySet().contains('a'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().containsKey('$'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().containsKey('b'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('$').terminal());
    assertTrue(!trie.getRoot().getChildren().get('a').getChildren().get('b').terminal());
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('$'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('c'));
    assertTrue(trie.getRoot().getChildren().get('a').getChildren().get('b').getChildren().containsKey('d'));
    
  }
  
  @Test
  public void trieFunctions() {
    ArrayList<Character> init = new ArrayList<Character>();
    init.add('@');
    init.add('$');
    Trie trie = new Trie(init);

    ArrayList<List<Character>> strings = new ArrayList<List<Character>>();
    strings.add(StringFormatter.listify("@test$"));
    strings.add(StringFormatter.listify("@tester$"));
    strings.add(StringFormatter.listify("@testing$"));
    strings.add(StringFormatter.listify("@tested$"));
    strings.add(StringFormatter.listify("@fest$"));
    strings.add(StringFormatter.listify("@fiesta$"));
    strings.add(StringFormatter.listify("@festive$"));
    strings.add(StringFormatter.listify("@first$"));
    strings.add(StringFormatter.listify("@flat$"));
    strings.add(StringFormatter.listify("@bet$"));
    strings.add(StringFormatter.listify("@best$"));
    strings.add(StringFormatter.listify("@bets$"));
    strings.add(StringFormatter.listify("@brat$"));
    strings.add(StringFormatter.listify("@stest$"));
    
    trie.addWords(strings);

    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 0).size() == 1);
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 0).contains(StringFormatter.listify("@test$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 1).size() == 4);
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 1).contains(StringFormatter.listify("@test$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 1).contains(StringFormatter.listify("@best$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 1).contains(StringFormatter.listify("@fest$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).size() == 7);
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@test$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@best$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@fest$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@bet$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@tester$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 2).contains(StringFormatter.listify("@tested$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).size() == 13);
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@test$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@best$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@fest$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@bet$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@tester$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@tested$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@brat$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@bets$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@testing$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@first$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@fiesta$")));
    assertTrue(trie.editDistance(StringFormatter.listify("@test$"), 3).contains(StringFormatter.listify("@flat$"))); 
    
    assertTrue(trie.isPrefix(StringFormatter.listify("@")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@t")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@te")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@tes")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@test")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@test$")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@b")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@f")));
    assertTrue(trie.isPrefix(StringFormatter.listify("@fie")));
    assertTrue(!trie.isPrefix(StringFormatter.listify("$")));
    assertTrue(!trie.isPrefix(StringFormatter.listify("@a")));
    assertTrue(!trie.isPrefix(StringFormatter.listify("@testocalypse$")));
    assertTrue(!trie.isPrefix(StringFormatter.listify("@mashedpotatos$")));

    assertTrue(trie.prefixed(StringFormatter.listify("@t")).size() == 4);
    assertTrue(trie.prefixed(StringFormatter.listify("@te")).size() == 4);
    assertTrue(trie.prefixed(StringFormatter.listify("@test$")).size() == 1);
    assertTrue(trie.prefixed(StringFormatter.listify("@b")).size() == 4);
    assertTrue(trie.prefixed(StringFormatter.listify("@f")).size() == 5);
    assertTrue(trie.prefixed(StringFormatter.listify("@fi")).size() == 2);

    assertTrue(trie.whitespace(StringFormatter.listify("@betstest$")).size() == 2);
    assertTrue(trie.whitespace(StringFormatter.listify("@bettest$")).size() == 1);
    assertTrue(trie.whitespace(StringFormatter.listify("@testbet$")).size() == 1);
    assertTrue(trie.whitespace(StringFormatter.listify("@firstbet$")).size() == 1);
    assertTrue(trie.whitespace(StringFormatter.listify("@betsfirst$")).size() == 1);
    assertTrue(trie.whitespace(StringFormatter.listify("@testingtesting$")).size() == 1);

    assertTrue(trie.evaluateWord(StringFormatter.listify("@test$"), null).size() == 1);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@tes$"), null).size() == 0);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@oogabooga$"), null).size() == 0);
    
    trie.prefixOn();
    assertTrue(trie.evaluateWord(StringFormatter.listify("@testing$"), null).size() == 1);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@test$"), null).size() == 4);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@tes$"), null).size() == 4);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@oogabooga$"), null).size() == 0);
    
    trie.editDistanceOn().setK(2);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@testing$"), null).size() == 1);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@test$"), null).size() == 5);
    
    assertTrue(trie.evaluateWord(StringFormatter.listify("@tes$"), null).size() == 5);
    assertTrue(trie.evaluateWord(StringFormatter.listify("@oogabooga$"), null).size() == 0);

    //test for whitespace operator, also checks that the weighting is properly done
    trie.whiteSpaceOn();
    assertTrue(trie.evaluateWord(StringFormatter.listify("@betstest$"), null).size() == 2);

    
  }
  @Test
  public void nodeFunctionTest() {
    
    Node<Character> n = new Node<Character>(StringFormatter.listify("abcdefg"), null);
    n.addSequence(StringFormatter.listify("abde"));
    assertTrue(n.getSequence().equals(StringFormatter.listify("ab")));
    assertTrue(n.getChildren().get('c').getSequence().equals(StringFormatter.listify("cdefg")));
    assertTrue(n.getChildren().get('d').getSequence().equals(StringFormatter.listify("de")));
    
    n.addSequence(StringFormatter.listify("abdf"));
    assertTrue(n.getSequence().equals(StringFormatter.listify("ab")));
    assertTrue(n.getChildren().get('c').getSequence().equals(StringFormatter.listify("cdefg")));
    assertTrue(n.getChildren().get('d').getSequence().equals(StringFormatter.listify("d")));
    assertTrue(n.getChildren().get('d').getChildren().get('e').getSequence().equals(StringFormatter.listify("e")));
    assertTrue(n.getChildren().get('d').getChildren().get('f').getSequence().equals(StringFormatter.listify("f")));

    assertTrue(n.gatherAllWords().size() == 3);
    assertTrue(n.gatherAllWords().contains(StringFormatter.listify("abcdefg")));
    assertTrue(n.gatherAllWords().contains(StringFormatter.listify("abde")));
    assertTrue(n.gatherAllWords().contains(StringFormatter.listify("abdf")));

    assertTrue(n.getChildren().get('c').fullList().equals(StringFormatter.listify("abcdefg")));
    assertTrue(n.getChildren().get('d').getChildren().get('e').fullList().equals(StringFormatter.listify("abde")));
    assertTrue(n.getChildren().get('d').getChildren().get('f').fullList().equals(StringFormatter.listify("abdf")));
  }
}
