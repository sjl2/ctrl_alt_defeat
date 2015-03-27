package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.io.File;

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

    parser.accepts("gui");
    OptionSpec<Integer>  portSpec =
      parser.accepts("port").withRequiredArg().ofType(Integer.class);

    OptionSpec<File> fileSpec = parser.nonOptions().ofType(File.class);



    OptionSet options = parser.parse(args);

    File db = options.valueOf(fileSpec);


    if (db == null) {
      System.out.println("ERROR: Please input a basketball database.");
      return;
    }

    if (options.has("port")) {
      new GUIManager(db, options.valueOf(portSpec));
    } else {
      new GUIManager(db);
    }
  }

}
