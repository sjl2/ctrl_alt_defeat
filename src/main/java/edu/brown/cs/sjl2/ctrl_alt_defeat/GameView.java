package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.time.LocalDate;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

/**
 * The object used to view old game information without it being interactive.
 *
 * @author sjl2
 *
 */
public class GameView {

  private int id;
  private LocalDate date;
  private Team home;
  private Team away;
  private BoxScore homeBoxScore;
  private BoxScore awayBoxScore;

  /**
   * Constructs a GameView from necessary parameters.
   *
   * @param db The DBManager needed to build the game view.
   * @param id The database id of the game.
   * @param home The Home Team Object
   * @param away The Away Team Object.
   * @param date The LocalDate of the game's date.
   * @throws GameException Throws GameException if the old boxscore could not be
   *           obtained.
   */
  public GameView(DBManager db, int id, Team home, Team away, LocalDate date)
      throws GameException {

    this.id = id;
    this.home = home;
    this.away = away;
    this.date = date;

    this.homeBoxScore = BoxScore.getOldBoxScore(db, id, home);
    this.awayBoxScore = BoxScore.getOldBoxScore(db, id, away);
  }

  /**
   * Getter for the game's database id.
   *
   * @return Returns an int of the game's id.
   */
  public int getID() {
    return id;
  }

  /**
   * Returns true if the team input is the home team of the game. Checks via
   * team id.
   *
   * @param team The team to check
   * @return Returns true if team is the home team, false otherwise. Checks for
   *         equivalent team ids.
   */
  public boolean isHome(Team team) {
    return this.home.getID() == team.getID();
  }

  /**
   * Getter for the home team.
   *
   * @return Return's the team home object.
   */
  public Team getHome() {
    return home;
  }

  /**
   * Getter for the away team object.
   *
   * @return Returns the away team object.
   */
  public Team getAway() {
    return away;
  }

  /**
   * Getter for the home box score.
   *
   * @return Returns the boxscore object for home.
   */
  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }

  /**
   * Getter for away team boxscore.
   *
   * @return Returns the boxscore object for away.
   */
  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
  }

  /**
   * Getter for the home score.
   *
   * @return Returns int number of points for the home team.
   */
  public int getHomeScore() {
    return homeBoxScore.getScore();
  }

  /**
   * Getter for the away team score.
   *
   * @return Returns int number of points for the away team.
   */
  public int getAwayScore() {
    return awayBoxScore.getScore();
  }

  /**
   * Getter for the date of the game.
   *
   * @return Returns the LocalDate of the game.
   */
  public LocalDate getDate() {
    return date;
  }

  @Override
  public String toString() {
    return away.getName() + " @ " + home.getName() + " (" + date + ")";
  }

}
