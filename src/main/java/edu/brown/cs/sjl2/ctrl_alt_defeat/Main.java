package edu.brown.cs.sjl2.ctrl_alt_defeat;

import edu.brown.cs.sjl2.ctrl_alt_defeat.GUI.GUIManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.BasketballDatabaseGenerator;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Main serves as the entry point for using Ctrl-Alt-Defeat
 *
 * @author awainger, ngoelz, sjl2, tschicke
 *
 */
public final class Main {

  /**
   * The entry static function for Ctrl-Alt-Defeat.
   * @param args The command line arguments used to execute.
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    OptionParser parser = new OptionParser();

    OptionSpec<Integer> randomRoundRobins =
        parser.accepts("random").withRequiredArg().ofType(Integer.class);
    OptionSpec<Integer> intelligentRoundRobins =
        parser.accepts("intelligent").withRequiredArg().ofType(Integer.class);
    OptionSpec<Integer>  portSpec =
      parser.accepts("port").withRequiredArg().ofType(Integer.class);
    OptionSpec<String> fileSpec = parser.nonOptions().ofType(String.class);



    OptionSet options = parser.parse(args);

    String db = options.valueOf(fileSpec);


    if (db == null) {
      System.out.println("ERROR: Please input a basketball database.");
      return;
    }

    DBManager dbManager = new DBManager(db);

    if (options.has("random")) {
     BasketballDatabaseGenerator.populateDB(dbManager,
         options.valueOf(randomRoundRobins), false);
    } else if (options.has("intelligent")) {
      BasketballDatabaseGenerator.populateDB(dbManager,
          options.valueOf(intelligentRoundRobins), true);
    }

    if (options.has("port")) {
      new GUIManager(dbManager, options.valueOf(portSpec));
    } else {
      new GUIManager(dbManager);
    }
  }

}
