package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
  private boolean homeBonus;
  private boolean homeDoubleBonus;
  private boolean awayBonus;
  private boolean awayDoubleBonus;

  private RuleSet rules;
  private PlayerFactory pf;
  private StatFactory sf;

  public Game(Team home, Team away, PlayerFactory pf, DBManager db,
              Map<BasketballPosition, Integer> starterIDs)
      throws GameException {

    if (home.getID() == away.getID()) {
      // Cannot play with yourselves
      String message = "This is no time to play with yourself!";
      throw new GameException(message);
    }

    // Game Fields in DB
    this.id = db.getNextID(TABLE);
    this.homeTeam = home;
    this.awayTeam = away;
    this.date = LocalDate.now();
    db.saveGame(this);

    try {
      // Remaining fields
      this.rules = new ProRules(); // TODO Change in settings

      this.homeBoxScore = new BoxScore(db, this, home);
      this.awayBoxScore = new BoxScore(db, this, away);
      this.lineup = new Lineup();
      this.homeBench = new Bench(home);
      this.awayBench = new Bench(away);

      placePlayers(home, away, starterIDs);

      this.pf = pf;
      this.sf = new StatFactory(db, this);

      this.homeBonus = false;
      this.homeDoubleBonus = false;
      this.awayBonus = false;
      this.awayDoubleBonus = false;
      this.homeTO = rules.timeouts();
      this.awayTO = rules.timeouts();
      this.period = 1;
    } catch (GameException e) {
      db.deleteGame(id);
      String message = e.getMessage() + " Game information deleted "
          + "from database.";
      throw new GameException(message);
    }

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
    this.possession = !possession;
  }

  public boolean getPossession() {
    return possession;
  }

  public int getTO(boolean home) {
    if (home) {
      return homeTO;
    } else {
      return awayTO;
    }
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

  public void updateBonuses() {
    if (homeFouls >= rules.bonus()) {
      homeBonus = true;
    }
    if (homeFouls >= rules.doubleBonus()) {
      homeDoubleBonus = true;
    }
    if (awayFouls >= rules.bonus()) {
      awayBonus = true;
    }
    if (awayFouls >= rules.doubleBonus()) {
      awayDoubleBonus = true;
    }
  }

  public Stat addStat(Stat s) throws GameException {
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      homeBoxScore.addStat(s);
      homeScore = homeBoxScore.getScore();
      homeFouls = homeBoxScore.getFouls();
      updateBonuses();

    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.addStat(s);
      awayScore = awayBoxScore.getScore();
      awayFouls = awayBoxScore.getFouls();
      updateBonuses();
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
      updateBonuses();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.undoStat(s);
      awayScore = awayBoxScore.getScore();
      awayFouls = awayBoxScore.getFouls();
      updateBonuses();
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

  public void placePlayers(Team h, Team a,
                           Map<BasketballPosition, Integer> starterIDs) throws GameException {

    for(BasketballPosition bp : BasketballPosition.values()) {
      lineup.addStarter(bp, pf.getPlayer(starterIDs.get(bp)));
    }
    
    Collection<Player> players =  h.getPlayers();
    Iterator<Player> homeIterator = players.iterator();

    if (players.size() < 5) {
      throw new GameException("Not enough players on the home team.");
    }

    while (homeIterator.hasNext()) {
      Player p = homeIterator.next();
      if(!starterIDs.containsValue(p.getID())) {
        homeBench.getPlayers().add(p);
      }
    }

    players =  a.getPlayers();
    Iterator<Player> awayIterator = players.iterator();

    if (players.size() < 5) {
      throw new GameException("Not enough players on the away team.");
    }

    while (awayIterator.hasNext()) {
      Player p = awayIterator.next();
      if(!starterIDs.containsValue(p.getID())) {
        awayBench.getPlayers().add(p);
      }
    }

  }

  public LocalDate getDate() {
    return date;
  }

  public boolean getHomeBonus() {
    return homeBonus;
  }
  public boolean getHomeDoubleBonus() {
    return homeDoubleBonus;
  }
  public boolean getAwayBonus() {
    return awayBonus;
  }
  public boolean getAwayDoubleBonus() {
    return awayDoubleBonus;
  }

  @Override
  public String toString() {
    return awayTeam + " @ " + homeTeam + " (" + date + ")";
  }


}
