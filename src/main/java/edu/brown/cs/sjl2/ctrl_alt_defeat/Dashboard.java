package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class Dashboard {
  private Game currentGame;
  DBManager db;

  public void startGame(Boolean home, String opponentName) {
    // TODO
    if (currentGame == null) {

    } else {
      // TODO Throw Exception
    }
  }

  public Team getTeamByName(String name) {
    // TODO
    return null;
  }

  public Game getGameByDate(String date) {
    // TODO
    return null;
  }

  public List<Game> getGamesByOpponent(String name) {
    // TODO
    return null;
  }

  BoxScore getBoxscore(String gameID) {
    // TODO
    return null;
  }
}
