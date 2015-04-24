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

  private Game newGame(Team home, Team away) throws GameException {
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
      String message = "Cannot start a new game without a team. Please "
          + "create your team at ...";
      throw new DashboardException(message);
    } else if (currentGame == null) {
      try {
        if (home) {
          currentGame = newGame(myTeam, tf.getTeam(opponentID));
        } else {
          currentGame = newGame(tf.getTeam(opponentID), myTeam);
        }
      } catch (GameException e) {
        throw new DashboardException("Cannot start game. " + e.getMessage());
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

  public List<Team> getAllTeams() {
    return tf.getAllTeams();
  }

  public List<Team> getOpposingTeams() {
    return tf.getOpposingTeams();
  }

  public Game getGameByDate(String date) {
    // TODO
    return null;
  }

  public Game getGameByID(int id) {
    return null; // TODO db.getGameByID(id);
  }

  public List<Game> getGamesByOpponent(int opponentID) {
    // TODO
    return null;
  }

  BoxScore getBoxscore(int gameID, Team team) throws DashboardException {
    try {
      return BoxScore.getOldBoxScore(db, gameID, team);
    } catch (GameException e) {
      String message = "Could not get old boxscore for " + team.getName()
          + " in game " + gameID + ". " + e.getMessage();
      throw new DashboardException(message);
    }
  }

  public Team setMyTeam(String name,
      String coach, String primary, String secondary) {

    this.myTeam = tf.addTeam(name, coach, primary, secondary, true);

    return this.myTeam;
  }

  public void addTeam(String name, String coach, String primary,
      String secondary) {

    tf.addTeam(name, coach, primary, secondary, false);
  }

  public OldGame getOldGame(int gameID) throws DashboardException {
    try {
      return db.getGameByID(gameID, tf);
    } catch (GameException e) {
      throw new DashboardException(e.getMessage());
    }

  }
}
