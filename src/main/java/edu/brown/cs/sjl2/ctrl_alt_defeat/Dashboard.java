package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;
import java.util.Map;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

/**
 * Central Class for Coach Operations. The Dashboard is an interface to the
 * database for coaching applications.
 *
 * @author sjl2
 *
 */
public class Dashboard {
  private Game currentGame;
  private DBManager db;
  private Team myTeam;

  /**
   * Constructor for the dashboard using a basketball database.
   *
   * @param db
   *          The DBManager for the basketball database
   */
  public Dashboard(DBManager db) {

    this.db = db;

    try {
      this.myTeam = db.getMyTeam();
    } catch (DashboardException e) {
      this.myTeam = null;
      System.out.println(e.getMessage());
    }

  }

  /**
   * Retrieves the current game.
   *
   * @return Returns the current game. Returns null if there is no current game.
   */
  public Game getGame() {
    return this.currentGame;
  }

  private Game newGame(Team home,
      Team away,
      Map<BasketballPosition, Integer> starterIDs,
      boolean b)
    throws GameException {

    return new Game(home, away, db, starterIDs, b);
  }

  /**
   * Starts a game between my team and an opponent with a specified lineup.
   *
   * @param home
   *          A boolean true if it is a home game for myteam, false for away.
   * @param opponentID
   *          The team id for the opponent
   * @param starterIDs
   *          A Map between the positions and the player ids.
   * @throws DashboardException
   *           Throws an exception if a game cannot be started. This is usually
   *           due to a game in play or a database failure during
   *           initialization.
   */
  public void startGame(Boolean home, int opponentID,
      Map<BasketballPosition, Integer> starterIDs) throws DashboardException {
    if (getMyTeam() == null) {
      String message = "Cannot start a new game without a team. Please "
          + "create your team at ...";
      throw new DashboardException(message);
    } else if (currentGame == null) {
      try {
        if (home) {
          currentGame = newGame(myTeam, db.getTeam(opponentID), starterIDs,
              true);
        } else {
          currentGame = newGame(db.getTeam(opponentID), myTeam, starterIDs,
              false);
        }
      } catch (GameException e) {
        throw new DashboardException("Cannot start game. " + e.getMessage());
      }
    } else {
      String message = "Cannot start new game with a game in progress.";
      throw new DashboardException(message);
    }
  }

  /**
   * Getter for my team.
   *
   * @return Returns the Team object of the coach's team.
   */
  public Team getMyTeam() {
    return myTeam;
  }

  /**
   * Retrieves team from database.
   *
   * @param id
   *          The team id to be searched for.
   * @return Returns the Team stored with the id.
   */
  public Team getTeam(int id) {
    return db.getTeam(id);
  }

  /**
   * Retrieves a player form the database.
   *
   * @param id
   *          The player id to be searched for.
   * @return Returns the Player stored with the id.
   */
  public Player getPlayer(int id) {
    return db.getPlayer(id);
  }

  /**
   * Retrieves a list of Links representing all teams in the database (my team
   * inclusive).
   *
   * @return Returns a list of links representing all teams in the database.
   */
  public List<Link> getAllTeams() {
    return db.getAllTeams();
  }

  /**
   * Retrieves a list of links representing all teams in the database EXCLUDING
   * my team.
   *
   * @return Returns a list of links representing all teams in the database
   *         excluding my team.
   */
  public List<Link> getOpposingTeams() {
    return db.getOpposingTeams();
  }

  /**
   * Getter for a boxscore for a team in a game.
   *
   * @param gameID
   *          The id of the game for the boxscore.
   * @param team
   *          The team of interest
   * @return Returns a boxscore object of the game for the team.
   * @throws DashboardException
   *           Throws Dashboard Exception if the Boxscore is not retrieved
   *           successfully from the database.
   */
  BoxScore getBoxscore(int gameID, Team team) throws DashboardException {
    try {
      return BoxScore.getOldBoxScore(db, gameID, team);
    } catch (GameException e) {
      String message = "Could not get old boxscore for " + team.getName()
          + " in game " + gameID + ". " + e.getMessage();
      throw new DashboardException(message);
    }
  }

  /**
   * Sets the coach's team for the dashboard. It is assumed the team does not
   * previously exist in the database.
   *
   * @param name
   *          The Team Name
   * @param coach
   *          Coach's name
   * @param primary
   *          The string value of the primary color (preferably hexadecimal).
   * @param secondary
   *          The string value of the secondary color(preferably hexadecimal)
   * @return Returns the new Team object now stored as my team in the database
   *         and dashboard.
   */
  public Team addMyTeam(String name, String coach, String primary,
      String secondary) {

    this.myTeam = db.createTeam(name, coach, primary, secondary, true);
    return this.myTeam;
  }

  /**
   * Sets my team to a team of id teamID.
   *
   * @param teamID
   *          The id of myTeam.
   */
  public void setMyTeam(int teamID) {
    setMyTeam(db.getTeam(teamID));
  }

  /**
   * Sets my team to the team object.
   *
   * @param team
   *          The team object that will be my team.
   */
  public void setMyTeam(Team team) {
    this.myTeam = team;
    db.updateTeam(team);
  }

  /**
   * Creates a new team with the specified parameters. Team is then stored in
   * the database.
   *
   * @param name
   *          Team name
   * @param coach
   *          Coach's name
   * @param primary
   *          String of primary color (preferably hexadecimal)
   * @param secondary
   *          String of secondary color (preferably hexadecimal)
   * @return Returns the new team object to of the team.
   */
  public Team createTeam(String name, String coach, String primary,
      String secondary) {

    return db.createTeam(name, coach, primary, secondary, false);
  }

  /**
   * Creates a new player with the specified parameters. The player is then
   * stored in the database.
   *
   * @param name
   *          Player Name
   * @param teamID
   *          ID of team that the player is going on (must exist in db)
   * @param number
   *          The player's number
   * @param current
   *          A Boolean on whether a player is current or not.
   * @return Returns the new player object.
   */
  public Player createPlayer(String name, int teamID, int number,
      boolean current) {

    return db.createPlayer(name, teamID, number, current);
  }

  /**
   * Getter for an old game. Returns a view of the game (not interactive).
   *
   * @param gameID
   *          The id of the old game.
   * @return Returns a GameView of the game.
   * @throws DashboardException
   *           Throws a DashboardException if the game does not exist or if it
   *           was not retieved correctly.
   */
  public GameView getOldGame(int gameID) throws DashboardException {
    try {
      return db.getGameByID(gameID);
    } catch (GameException e) {
      throw new DashboardException(e.getMessage());
    }

  }

  /**
   * Ends the current game.
   */
  public void endGame() {
    this.currentGame = null;
  }
}
