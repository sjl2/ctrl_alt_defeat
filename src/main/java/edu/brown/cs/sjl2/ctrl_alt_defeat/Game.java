package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonElement;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
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

  public Game(Team home, Team away, PlayerFactory pf, DBManager db) {
    this.id = db.getNextID(TABLE);
    this.homeTeam = home;
    this.awayTeam = away;
    db.saveGame(this);
    
    this.homeBoxScore = new BoxScore(db, this, home);
    this.awayBoxScore = new BoxScore(db, this, away);
    this.lineup = new Lineup();
    this.homeBench = new Bench(home);
    this.awayBench = new Bench(away);
    
    placePlayers(home, away);
    
    this.stats = new ArrayList<>();
    this.pf = pf;
    this.sf = new StatFactory(db, this);
    this.db = db;

  }

  public Game() {
    this.id = -1;
    this.homeTeam = new Team(1);
    this.awayTeam = new Team(2);
    this.pf = null;
    this.db = null;
  }

  public int getID() {
    return id;
  }

  public Player getPlayerAtPosition(Location pos) {return null;}

  public Team getHome() {
    return homeTeam;
  }

  public Team getAway() {
    return awayTeam;
  }

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
    System.out.println(t.playerIds + "  " + t.playerIds + "  " + l.getPlayers().values());
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
    System.out.println(possession);
    this.possession = !possession;
    System.out.println(possession);

  }

  public void addStat(Stat s) throws GameException {
    stats.add(0, s);
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      s.execute(homeBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(homeBoxScore.getTeamStats());
      homeScore = homeBoxScore.getScore();
      homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      s.execute(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.execute(awayBoxScore.getTeamStats());
      awayScore = awayBoxScore.getScore();
      awayFouls = awayBoxScore.getFouls();
    } else {
      String message = "Cannot add stat for " + s.getPlayer() + " because they "
          + "are not on either team.";
      throw new GameException(message);
    }

    storeGame();
  }

  public void addStat(String statID, int playerID, Location location)
      throws GameException {

    Player p = pf.getPlayer(playerID);
    addStat(sf.getStat(statID, p, location, period));
  }

  public void undoStat() throws GameException {
    Stat s = stats.remove(0);
    db.remove(s);
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      s.undo(homeBoxScore.getPlayerStats(s.getPlayer()));
      s.undo(homeBoxScore.getTeamStats());
      homeScore = homeBoxScore.getScore();
      homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      s.undo(awayBoxScore.getPlayerStats(s.getPlayer()));
      s.undo(awayBoxScore.getTeamStats());
      awayScore = awayBoxScore.getScore();
      awayFouls = awayBoxScore.getFouls();
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
   * Store all essential data from the game to the database in case of failure.
   * TODO Consider threading this to happen every second.
   */
  public void storeGame() {
    homeBoxScore.updateDB();
    awayBoxScore.updateDB();
  }

  public int getHomeScore() {
    return homeScore;
  }

  public int getAwayScore() {
    return awayScore;
  }

  public int getHomeFouls() {
    return homeFouls;
  }

  public int getAwayFouls() {
    return awayFouls;
  }

  public int getPeriod() {
    return period;
  }
  
  public Lineup getLineup() {
    return lineup;
  }
  
  public Bench getBench(boolean home) {
    if (home) {
      return homeBench;
    } else {
      return awayBench;
    }
  }

  public void placePlayers(Team h, Team a) {
    Iterator<Player> homeIterator = h.getPlayers().iterator();
    lineup.getPlayers().put(BasketballPosition.HomePG, homeIterator.next());
    lineup.getPlayers().put(BasketballPosition.HomeSG, homeIterator.next());
    lineup.getPlayers().put(BasketballPosition.HomeSF, homeIterator.next());
    lineup.getPlayers().put(BasketballPosition.HomePF, homeIterator.next());
    lineup.getPlayers().put(BasketballPosition.HomeC, homeIterator.next());
    while (homeIterator.hasNext()) {
      homeBench.getPlayers().add(homeIterator.next());
    }
    
    Iterator<Player> awayIterator = a.getPlayers().iterator();
    lineup.getPlayers().put(BasketballPosition.AwayPG, awayIterator.next());
    lineup.getPlayers().put(BasketballPosition.AwaySG, awayIterator.next());
    lineup.getPlayers().put(BasketballPosition.AwaySF, awayIterator.next());
    lineup.getPlayers().put(BasketballPosition.AwayPF, awayIterator.next());
    lineup.getPlayers().put(BasketballPosition.AwayC, awayIterator.next());
    while (awayIterator.hasNext()) {
      awayBench.getPlayers().add(awayIterator.next());
    }

  }

}
