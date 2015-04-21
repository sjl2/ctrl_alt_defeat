package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.TeamFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class Dashboard {
  private Game currentGame;
  DBManager db;
  Team myTeam;

  TeamFactory tf;
  PlayerFactory pf;
  StatFactory sf;

  public Dashboard(DBManager db) {

    this.db = db;


    pf = new PlayerFactory(db);
    tf = new TeamFactory(db, pf);

    try {
      this.myTeam = db.getMyTeam(pf);
    } catch (DashboardException e) {
      this.myTeam = null;
      System.out.println(e.getMessage());
    }

  }

  public Game getGame() {
    return this.currentGame;
  }

  private Game newGame(Team home, Team away) {
    return new Game(home, away, pf, db);
  }

//  //simple setter for game
//  //only used for testing
//  public void setGame(Game g) {
//    System.out.println("good god you better be testing");
//    this.currentGame = g;
//  }

  public void startGame(Boolean home, int opponentID)
      throws DashboardException {
    if (getMyTeam() == null) {
      String message = "You cannot start a new game without a team. Please "
          + "create your team at ...";
      throw new DashboardException(message);
    } else if (currentGame == null) {
      if (home) {
        currentGame = newGame(myTeam, tf.getTeam(opponentID));
      } else {
        currentGame = newGame(tf.getTeam(opponentID), myTeam);
      }
    } else {
      String message = "Cannot start new game with a game in progress.";
      throw new DashboardException(message);
    }
  }

  public Team getMyTeam() {
    return myTeam;
  }

  public Team getTeam(int id) {
    return tf.getTeam(id);
  }

  public Game getGameByDate(String date) {
    // TODO
    return null;
  }

  public List<Game> getGamesByOpponent(int opponentID) {
    // TODO
    return null;
  }

  BoxScore getBoxscore(int gameID) {
    // TODO
    return null;
  }

  public Team setMyTeam(String name,
      String coach, String color1, String color2) {
    this.myTeam =
        new Team(db.getNextID("team"), name, coach, color1, color2, pf);

    db.saveTeam(this.myTeam, true);

    return null;
  }
}
