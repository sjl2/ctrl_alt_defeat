package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.RuleSet;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Scoreboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class Game {
  private int id;
  private Team homeTeam;
  private Team awayTeam;
  private Lineup lineup;
  private Bench homeBench;
  private Bench awayBench;
  private Scoreboard scoreboard;
  private BoxScore homeBoxScore;
  private BoxScore awayBoxScore;
  private List<Stat> stats;
  private RuleSet rules;
  private PlayerFactory pf;
  private StatFactory sf;
  private DBManager db;

  public Game(Team home, Team away, PlayerFactory pf, StatFactory sf, DBManager db) {
    this.id = db.getNextGameID();
    this.homeTeam = home;
    this.awayTeam = away;
    this.homeBoxScore = new BoxScore(true, home);
    this.awayBoxScore = new BoxScore(false, away);
    this.lineup = new Lineup();
    this.scoreboard = createScoreboard();
    this.pf = pf;
    this.sf = sf;
    this.db = db;
  }


  public Player getPlayerAtPosition(Location pos) {return null;}

  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }
  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
  }
  private Scoreboard createScoreboard() {
    return new Scoreboard(rules);
  }
  public void subPlayer(int idIn, int idOut, boolean home) throws ScoreboardException {
    Lineup l = lineup;
    Bench b;
    Team t;
    if (home) {
      b = homeBench;
      t = homeTeam;
    } else {
      b = awayBench;
      t = awayTeam;
    }

    l.sub(t.getPlayerById(idIn), t.getPlayerById(idOut));
    b.sub(t.getPlayerById(idIn), t.getPlayerById(idOut));
  }

  public void takeTimeout(Boolean home) throws ScoreboardException {
    scoreboard.takeTimeout(home);
  }

  public void flipPossession() {
    scoreboard.flipPossession();
  }

  public void addStat(Stat s) throws GameException {
    stats.add(0, s);
    if (s.getPlayer().getTeamID() == homeTeam.getId()) {
      s.execute(homeBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(homeBoxScore.getTeamStats());
    } else if (s.getPlayer().getTeamID() == awayTeam.getId()) {
      s.execute(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(awayBoxScore.getTeamStats());
    } else {
      String message = "Cannot add stat for " + s.getPlayer() + " because they "
          + "are not on either team.";
      throw new GameException(message);
    }

    storeGame();
  }

  public void addStatByID(String statID, int playerID, Location location)
      throws GameException {

    Player p = pf.getPlayer(playerID);
    addStat(sf.getStat(statID, p, location));
  }

  public void undoStat() throws GameException {
    Stat s = stats.remove(0);
    if (s.getPlayer().getTeamID() == homeTeam.getId()) {
      s.undo(homeBoxScore.getPlayerStats(s.getPlayer()));
      s.undo(homeBoxScore.getTeamStats());
    } else if (s.getPlayer().getTeamID() == awayTeam.getId()) {
      s.undo(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.undo(awayBoxScore.getTeamStats());
    } else {
      String message = "Cannot undo stat for " + s.getPlayer() + " because "
          + "they are not on either team.";
      throw new GameException(message);
    }
  }

  public List<Player> getTopPlayers(int n) {
    // TODO da fuq
    return null;
  }

  public RuleSet getRules() {
    return rules;
  }

  public void setRules(RuleSet rules) {
    this.rules = rules;
  }

  /**
   * Store all essential data from the game to the databse in case of failure.
   * TODO Consider threading this to happen every second.
   */
  public void storeGame() {
    db.store(id, homeBoxScore);
    db.store(id, awayBoxScore);
    db.store(id, stats);
  }


}
