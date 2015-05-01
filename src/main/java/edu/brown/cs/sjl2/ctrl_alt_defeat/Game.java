package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ProRules;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.RuleSet;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class Game {
  private static final String TABLE = "game";
  private static final int NUMBER_OF_SEASONS = 5; // Years
  private static final int SEASON_SPAN = 3; // Months

  private boolean homeGame;
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
  private StatFactory sf;

  private int homeWins;

  private DBManager db;

  private int homeLosses;

  private int awayWins;

  private int awayLosses;

  public Game(Team home, Team away, DBManager db,
              Map<BasketballPosition, Integer> starterIDs, boolean homeGame)
      throws GameException {
    this.homeGame = homeGame;
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
    db.createGame(this);

    this.db = db;

    try {
      // Remaining fields
      this.rules = new ProRules(); // TODO Change in settings

      this.homeBoxScore = new BoxScore(db, this, home);
      this.awayBoxScore = new BoxScore(db, this, away);
      this.lineup = new Lineup();
      this.homeBench = new Bench(home);
      this.awayBench = new Bench(away);

      this.sf = new StatFactory(db, this);

      this.homeBonus = false;
      this.homeDoubleBonus = false;
      this.awayBonus = false;
      this.awayDoubleBonus = false;
      this.homeTO = rules.getTimeOuts();
      this.awayTO = rules.getTimeOuts();
      this.period = 1;

      placePlayers(home, away, starterIDs);
    } catch (GameException e) {
      db.deleteGame(id);
      String message = e.getMessage() + " Game information deleted "
          + "from database.";
      throw new GameException(message);
    }

  }

  public Game(Team home, Team away, DBManager db)
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
    this.date = getRandomDateInSeason();
    db.createGame(this);
    this.db = db;

    try {
    // Remaining fields
    this.rules = new ProRules(); // TODO Change in settings

    this.homeBoxScore = new BoxScore(db, this, home);
    this.awayBoxScore = new BoxScore(db, this, away);
    this.lineup = new Lineup();
    this.homeBench = new Bench(home);
    this.awayBench = new Bench(away);

    this.sf = new StatFactory(db, this);

    this.homeBonus = false;
    this.homeDoubleBonus = false;
    this.awayBonus = false;
    this.awayDoubleBonus = false;
    this.homeTO = rules.getTimeOuts();
    this.awayTO = rules.getTimeOuts();
    this.period = 1;

    defaultStartingLineup(home, away);

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

  public boolean isHome(int teamID) {
    return this.homeTeam.getID() == teamID;
  }

  public Team getHome() {
    return homeTeam;
  }

  public Team getAway() {
    return awayTeam;
  }

  public int getHomeWins() {
    if (homeWins == -1) {
      homeWins = db.getTeamWins(getID(), homeTeam.getID(), date, false);
    }
    return homeWins;
  }

  public int getHomeLosses() {
    if (homeLosses == -1) {
      homeLosses = db.getTeamWins(getID(), homeTeam.getID(), date, false);
    }
    return homeLosses;
  }

  public int getAwayWins() {
    if (awayWins == -1) {
      awayWins = db.getTeamWins(getID(), awayTeam.getID(), date, false);
    }
    return awayWins;
  }

  public int getAwayLosses() {
    if (awayLosses == -1) {
      awayLosses = db.getTeamWins(getID(), awayTeam.getID(), date, false);
    }
    return awayLosses;
  }

  public int getHomeScore() {
    return homeBoxScore.getScore();
  }

  public int getAwayScore() {
    return awayBoxScore.getScore();
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
    if (home) {
      b = homeBench;
    } else {
      b = awayBench;
    }
    l.sub(db.getPlayer(idIn), db.getPlayer(idOut));
    b.sub(db.getPlayer(idIn), db.getPlayer(idOut));
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
    this.period++;
    this.awayBonus = false;
    this.awayDoubleBonus = false;
    this.homeBonus = false;
    this.homeDoubleBonus = false;
    this.homeFouls = 0;
    this.awayFouls = 0;
    if (period > rules.getPeriods()) {
      this.homeTO = rules.getOTTimeOuts();
      this.awayTO = rules.getOTTimeOuts();
    } else {
      this.awayTO = rules.getTimeOuts();
      this.homeTO = rules.getTimeOuts();
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

    Player p = db.getPlayer(playerID);
    return addStat(sf.addStat(statID, p, location, period));
  }

  public void updateStat(int id, String statID,
      int playerID, Location location) throws GameException {

    Stat oldStat = sf.getStat(id);
    undoStat(oldStat);

    Stat s = sf.updateStat(id, statID, db.getPlayer(playerID), location);
    addStat(s);

  }

  public void deleteStat(int id) throws GameException {
    Stat s = sf.removeStat(id);
    undoStat(s);
  }

  public void updateBonuses() {
    if (homeFouls >= rules.getBonus()) {
      homeBonus = true;
    }
    if (homeFouls >= rules.getDoubleBonus()) {
      homeDoubleBonus = true;
    }
    if (awayFouls >= rules.getBonus()) {
      awayBonus = true;
    }
    if (awayFouls >= rules.getDoubleBonus()) {
      awayDoubleBonus = true;
    }
  }

  public Stat addStat(Stat s) throws GameException {
    if (s.getPlayer().getTeamID() == homeTeam.getID()) {
      homeBoxScore.addStat(s);
      homeFouls = homeBoxScore.getFouls();
      updateBonuses();

    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.addStat(s);
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
      homeFouls = homeBoxScore.getFouls();
      updateBonuses();
    } else if (s.getPlayer().getTeamID() == awayTeam.getID()) {
      awayBoxScore.undoStat(s);
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
      Integer playerID = starterIDs.get(bp);
      if(playerID == null) {
        continue;
      }
      Player p = db.getPlayer(playerID);
      lineup.addStarter(bp, p);
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

  public void defaultStartingLineup(Team h, Team a) throws GameException {

    Collection<Player> players =  h.getPlayers();
    Iterator<Player> homeIterator = players.iterator();

    if (players.size() < 5) {
      throw new GameException("Not enough players on the home team.");
    }

    lineup.addStarter(BasketballPosition.HomePG, homeIterator.next())
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
      throw new GameException("Not enough players on the away team.");
    }

    lineup.addStarter(BasketballPosition.AwayPG, awayIterator.next())
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

  public boolean getHomeGame() {
    return homeGame;
  }

  private static LocalDate getRandomDateInSeason() {
    Random r = new Random();

    int year = LocalDate.now().getYear() + r.nextInt(NUMBER_OF_SEASONS);

    int direction = r.nextInt(2);
    int disp = r.nextInt(SEASON_SPAN);
    int month;
    if (direction == 1) {
      year = year - 1;
      month = 12 - disp;
    } else {
      month = 1 + disp;
    }

    int day = 1 + r.nextInt(Month.of(month).maxLength() - 1);

    return LocalDate.of(year, month, day);
  }

}
