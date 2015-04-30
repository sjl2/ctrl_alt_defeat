package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class Dashboard {
  private Game currentGame;
  DBManager db;
  Team myTeam;

  StatFactory sf;

  public Dashboard(DBManager db) {

    this.db = db;

    try {
      this.myTeam = db.getMyTeam();
    } catch (DashboardException e) {
      this.myTeam = null;
      System.out.println(e.getMessage());
    }

  }

  public Game getGame() {
    return this.currentGame;
  }

  private Game newGame(Team home, Team away,
      Map<BasketballPosition, Integer> starterIDs, boolean b) throws GameException {

    return new Game(home, away, db, starterIDs, b);
  }



  public void startGame(Boolean home, int opponentID, Map<BasketballPosition, Integer> starterIDs)
      throws DashboardException {
    if (getMyTeam() == null) {
      String message = "Cannot start a new game without a team. Please "
          + "create your team at ...";
      throw new DashboardException(message);
    } else if (currentGame == null) {
      try {
        if (home) {
          currentGame = newGame(myTeam, db.getTeam(opponentID), starterIDs, true);
        } else {
          currentGame = newGame(db.getTeam(opponentID), myTeam, starterIDs, false);
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
    return db.getTeam(id);
  }

  public Player getPlayer(int id) {
    return db.getPlayer(id);
  }

  public List<Link> getAllTeams() {
    return db.getAllTeams();
  }

  public List<Link> getOpposingTeams() {
    return db.getOpposingTeams();
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

  public Team addMyTeam(String name,
      String coach, String primary, String secondary) {

    this.myTeam = db.createTeam(name, coach, primary, secondary, true);
    return this.myTeam;
  }

  public void setMyTeam(int teamID) {
    setMyTeam(db.getTeam(teamID));
  }

  public void setMyTeam(Team team) {
    this.myTeam = team;
    db.updateTeam(team);
  }

  public Team createTeam(String name, String coach, String primary,
      String secondary) {

    return db.createTeam(name, coach, primary, secondary, false);
  }

  public Player createPlayer(String name, int teamID, int number, boolean current) {
    return db.createPlayer(name, teamID, number, current);
  }

  public GameView getOldGame(int gameID) throws DashboardException {
    try {
      return db.getGameByID(gameID);
    } catch (GameException e) {
      throw new DashboardException(e.getMessage());
    }

  }
}
