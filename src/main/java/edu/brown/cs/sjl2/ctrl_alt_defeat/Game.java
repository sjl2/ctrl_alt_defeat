package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.PlayerFactory;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ProRules;
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

  private LocalDate date;
  private int period;
  private int homeScore;
  private int awayScore;
  private int homeTO;
  private int awayTO;
  private boolean possession;
  private int homeFouls;
  private int awayFouls;

  private RuleSet rules;
  private PlayerFactory pf;
  private StatFactory sf;

  public Game(Team home, Team away, PlayerFactory pf, DBManager db)
      throws GameException {
    this.rules = new ProRules();
    this.id = db.getNextID(TABLE);
    this.date = LocalDate.now();

    this.homeTeam = home;
    this.awayTeam = away;
    db.saveGame(this);

    this.homeBoxScore = new BoxScore(db, this, home);
    this.awayBoxScore = new BoxScore(db, this, away);
    this.lineup = new Lineup();
    this.homeBench = new Bench(home);
    this.awayBench = new Bench(away);

    placePlayers(home, away);

    this.pf = pf;
    this.sf = new StatFactory(db, this);

  }

  public int getID() {
    return id;
  }

  public Player getPlayerAtPosition(Location pos) {return null;}

  /**
   * Returns true if the team input is the home team of the game. Checks via
   * team id.
   * @param team The team to check
   * @return Returns true if team is the home team, false otherwise. Checks for
   * equivalent team ids.
   */
  public boolean isHome(Team team) {
    return this.homeTeam.getID() == team.getID();
  }

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

  public void subPlayer(int idIn, int idOut, boolean home)
      throws ScoreboardException {
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
      String message = "Cannot increment, game is already in period "
          + this.period + "!";
      throw new GameException(message);
    } else {
      this.period++;
    }
  }

  public void decrementPeriod() throws GameException {
    if (this.period == 1) {
      String message = "Cannot decrement, game is already in the first period!";
      throw new GameException(message);
    } else {
      this.period--;
    }
  }

  public void flipPossession() {
    System.out.println(possession);
    this.possession = !possession;
    System.out.println(possession);
  }

  public List<Stat> getAllStats() {
    return sf.getAllStats();
  }

  public Stat addStat(String statID, int playerID, Location location)
      throws GameException {

    Player p = pf.getPlayer(playerID);
    return addStat(sf.addStat(statID, p, location, period));
  }

  public void updateStat(int id, String statID,
      int playerID, Location location) throws GameException {

    Stat oldStat = sf.getStat(id);
    undoStat(oldStat);

    Stat s = sf.updateStat(id, statID, pf.getPlayer(playerID), location);
    addStat(s);

  }

  public void deleteStat(int id) throws GameException {
    Stat s = sf.removeStat(id);
    undoStat(s);
  }

  public Stat addStat(Stat s) throws GameException {
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      homeBoxScore.addStat(s);
      homeScore = homeBoxScore.getScore();
      homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.addStat(s);
      awayScore = awayBoxScore.getScore();
      awayFouls = awayBoxScore.getFouls();
    } else {
      String message = "Cannot add stat for " + s.getPlayer() + " because they "
          + "are not on either team.";
      throw new GameException(message);
    }

    return s;

  }

  public void undoStat(Stat s) throws GameException {
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      homeBoxScore.undoStat(s);
      homeScore = homeBoxScore.getScore();
      homeFouls = homeBoxScore.getFouls();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.undoStat(s);
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

  public void placePlayers(Team h, Team a) throws GameException {
    Collection<Player> players =  h.getPlayers();
    Iterator<Player> homeIterator = players.iterator();

    if (players.size() < 5) {
      throw new GameException("Not enough players on the home team.");
    }

    lineup
      .addStarter(BasketballPosition.HomePG, homeIterator.next())
      .addStarter(BasketballPosition.HomeSG, homeIterator.next())
      .addStarter(BasketballPosition.HomeSF, homeIterator.next())
      .addStarter(BasketballPosition.HomePF, homeIterator.next())
      .addStarter(BasketballPosition.HomeC, homeIterator.next());

    while (homeIterator.hasNext()) {
      homeBench.getPlayers().add(homeIterator.next());
    }

    players =  a.getPlayers();
    Iterator<Player> awayIterator = players.iterator();


    if (players.size() < 5) {
      throw new GameException("Not enough players on the home team.");
    }

    lineup
      .addStarter(BasketballPosition.AwayPG, awayIterator.next())
      .addStarter(BasketballPosition.AwaySG, awayIterator.next())
      .addStarter(BasketballPosition.AwaySF, awayIterator.next())
      .addStarter(BasketballPosition.AwayPF, awayIterator.next())
      .addStarter(BasketballPosition.AwayC, awayIterator.next());

    while (awayIterator.hasNext()) {
      awayBench.getPlayers().add(awayIterator.next());
    }

  }

  public LocalDate getDate() {
    return date;
  }

  @Override
  public String toString() {
    return awayTeam + " @ " + homeTeam + " (" + date + ")";
  }


}
