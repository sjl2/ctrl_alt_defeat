package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.ArrayList;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.RuleSet;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class Game {
  private static final String TABLE = "game";

  private int id;
  private Team homeTeam;
  private Team awayTeam;
  private Lineup lineup;
  private Bench homeBench;
  private Bench awayBench;
  private BoxScore homeBoxScore;
  private BoxScore awayBoxScore;

  private int period;
  private int homeScore;
  private int awayScore;
  private int homeTO;
  private int awayTO;
  private boolean possession;
  private int homeFouls;
  private int awayFouls;

  private List<Stat> stats;
  private RuleSet rules;
  private PlayerFactory pf;
  private StatFactory sf;
  private DBManager db;

  public Game(Team home, Team away, PlayerFactory pf, StatFactory sf, DBManager db) {
    this.id = db.getNextID(TABLE);
    this.homeTeam = home;
    this.awayTeam = away;
    this.homeBoxScore = new BoxScore(true, home);
    this.awayBoxScore = new BoxScore(false, away);
    this.lineup = new Lineup();
    this.stats = new ArrayList<>();
    this.pf = pf;
    this.sf = sf;
    this.db = db;
  }

  public int getID() {
    return id;
  }

  public Player getPlayerAtPosition(Location pos) {return null;}

  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }
  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
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

  public void takeTimeout(Boolean home) throws GameException {
    if (home) {
      if (homeTO == 0) {
        throw new GameException("Home team is out of timeouts");
      } else {
        homeTO--;
      }
    } else {
      if (awayTO == 0) {
        throw new GameException("Away team is out of timeouts");
      } else {
        awayTO--;
      }
    }
  }
  
  public void incrementPeriod() throws GameException {
    if (this.period == rules.periods()) {
      throw new GameException("Cannot increment, game is already in period " + this.period + "!");
    } else {
      this.period++;
    }
  }
  
  public void decrementPeriod() throws GameException {
    if (this.period == 1) {
      throw new GameException("Cannot decrement, game is already in the first period!");
    } else {
      this.period--;
    }
  }

  public void flipPossession() {
    this.possession = !possession;
  }

  public void addStat(Stat s) throws GameException {
    stats.add(0, s);
    if (s.getPlayer().getTeamID() == homeTeam.getId()) {
      s.execute(homeBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(homeBoxScore.getTeamStats());
      this.homeScore = homeBoxScore.getScore();
      this.homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getId()) {
      s.execute(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(awayBoxScore.getTeamStats());
      this.awayScore = awayBoxScore.getScore();
      this.awayFouls = awayBoxScore.getFouls();
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
      this.homeScore = homeBoxScore.getScore();
      this.homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getId()) {
      s.undo(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.undo(awayBoxScore.getTeamStats());
      this.awayScore = awayBoxScore.getScore();
      this.awayFouls = awayBoxScore.getFouls();
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
