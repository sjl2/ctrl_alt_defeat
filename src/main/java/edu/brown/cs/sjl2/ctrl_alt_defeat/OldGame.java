package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.time.LocalDate;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

public class OldGame {

  private int id;
  private LocalDate date;
  private Team home;
  private Team away;
  private BoxScore homeBoxScore;
  private BoxScore awayBoxScore;

  public OldGame(DBManager db, int id, Team home, Team away, LocalDate date)
      throws GameException {
    this.id = id;
    this.home = home;
    this.away = away;
    this.date = date;

    this.homeBoxScore = BoxScore.getOldBoxScore(db, id, home);
    this.awayBoxScore = BoxScore.getOldBoxScore(db, id, away);
  }

  public int getID() {
    return id;
  }

  /**
   * Returns true if the team input is the home team of the game. Checks via
   * team id.
   * @param team The team to check
   * @return Returns true if team is the home team, false otherwise. Checks for
   * equivalent team ids.
   */
  public boolean isHome(Team team) {
    return this.home.getID() == team.getID();
  }

  public Team getHome() {
    return home;
  }

  public Team getAway() {
    return away;
  }

  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }

  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
  }

  public List<Player> getTopPlayers(int n) {
    // TODO da fuq
    return null;
  }

  public int getHomeScore() {
    return homeBoxScore.getScore();
  }

  public int getAwayScore() {
    return awayBoxScore.getScore();
  }

  public LocalDate getDate() {
    return date;
  }
}
