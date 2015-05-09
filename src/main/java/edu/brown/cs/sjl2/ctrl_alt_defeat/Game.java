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

/**
 * A class representing a live basketball game. This is the central hub for all
 * actions related to providing a coach with live-game information.
 *
 * @author sjl2
 *
 */
public class Game {
  private static final String TABLE = "game";
  private static final int NUMBER_OF_SEASONS = 3; // Years
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

  private DBManager db;


  /**
   * Constructor for a live game.
   * @param home The home team object
   * @param away The away team object
   * @param db The database to store game information
   * @param starterIDs The Map of positions to player ids for determining
   * starters
   * @param homeGame Boolean for whether it is a home game for my team
   * @throws GameException Throws a game exception if the game could not be
   * created.
   */
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
      this.rules = new ProRules();

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

  /**
   * Constructor for a game where starting lineup is unnecessary. Used for
   * simulations mostly. Starting line up is the top five players obtained for
   * each team.
   * @param home Home team object
   * @param away Away team object
   * @param db The DBManager of the basketball database to store data in
   * @throws GameException Throws a game exception if the game fails to start
   */
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

  /**
   * Getter for the game id.
   * @return Returns the database id of the game.
   */
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
    return this.homeTeam.getID() == team.getID();
  }

  /**
   * Returns true if the team id input represents the home team of this game.
   * @param teamID The team to be queried
   * @return Returns true if the teamID represents the home team, false
   * otherwise
   */
  public boolean isHome(int teamID) {
    return this.homeTeam.getID() == teamID;
  }

  /**
   * Getter for the home team object.
   * @return Returns the home team object.
   */
  public Team getHome() {
    return homeTeam;
  }

  /**
   * Getter for the away team object.
   * @return Returns away team object.
   */
  public Team getAway() {
    return awayTeam;
  }

  /**
   * Getter for the home score.
   * @return Returns the int number of home team points.
   */
  public int getHomeScore() {
    return homeBoxScore.getScore();
  }

  /**
   * Getter for the away score.
   * @return Returns the int number of away team points.
   */
  public int getAwayScore() {
    return awayBoxScore.getScore();
  }

  /**
   * Getter for the home team boxscore.
   * @return Returns the home team's boxscore object.
   */
  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }

  /**
   * Getter for the away team boxscore.
   * @return Returns the away team's boxscore object.
   */
  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
  }

  /**
   * Substitutes a player from the bench to on the court.
   * @param idIn The player id of the player going in.
   * @param idOut The player id of the playering going to the bench.
   * @param home Boolean for whether this is for the home team or not.
   * @throws ScoreboardException Throws a scoreboard exception if the
   * substitution is illegal.
   */
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

  /**
   * Use a timeout for one of the teams.
   * @param home A Boolean for whether the home team is taking a time out. False
   * if the away team is using a timeout.
   * @throws GameException Returns a gameexception if a time out cannot be
   * taken for the specified team.
   */
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

  /**
   * Increments the period by one. Resets necessary team stats and checks for
   * end gaming conditions.
   * @throws GameException Throws a game exception if the period increments too
   * high without a tied score.
   */
  public void incrementPeriod() throws GameException {

    if (period > rules.getPeriods()) {
      if (getHomeScore() != getAwayScore()) {
        throw new GameException("Cannot go into overtime without tied score.");
      }
      this.homeTO = rules.getOTTimeOuts();
      this.awayTO = rules.getOTTimeOuts();
    } else {
      this.awayTO = rules.getTimeOuts();
      this.homeTO = rules.getTimeOuts();
    }

    this.period++;
    this.awayBonus = false;
    this.awayDoubleBonus = false;
    this.homeBonus = false;
    this.homeDoubleBonus = false;
    this.homeFouls = 0;
    this.awayFouls = 0;

  }

  /**
   * Decrements a period in case of a mistake.
   * @throws GameException Throws a GameException if the period is at one.
   * There is no zero period.
   */
  public void decrementPeriod() throws GameException {
    if (this.period == 1) {
      String message = "Cannot decrement, game is already in the first period!";
      throw new GameException(message);
    } else {
      this.period--;
    }
  }

  /**
   * Changes the direction of the possesion arrow.
   */
  public void flipPossession() {
    this.possession = !possession;
  }

  /**
   * Getter for the possesion arrow. True represents the possesion favors the
   * home team.
   * @return Returns a boolean representing the possession. True states that
   * the home team recieves the next tie up.
   */
  public boolean getPossession() {
    return possession;
  }

  /**
   * Getter for the number of timeouts remaining for a team.
   * @param home Boolean for whether to grab home timeouts if true. Away team is
   * false.
   * @return Returns the int number of timeouts remaining.
   */
  public int getTO(boolean home) {
    if (home) {
      return homeTO;
    } else {
      return awayTO;
    }
  }

  /**
   * Getter for all of the stat objects made for this game.
   * @return Returns a list of all the stats made for this game.
   */
  public List<Stat> getAllStats() {
    return sf.getAllStats();
  }

  /**
   * Adds a stat to the game based on input parameters. Used by handlers.
   * @param statType The type of stat
   * @param playerID The id of the player associated with the stat
   * @param location The on court location of the stat
   * @return Returns the stat object for this pairing.
   * @throws GameException Throws a game exception if the stat could not be
   * added to the game.
   */
  public Stat addStat(String statType, int playerID, Location location)
      throws GameException {

    Player p = db.getPlayer(playerID);
    return addStat(sf.addStat(statType, p, location, period));
  }

  /**
   * Updates a stat with the id with all the new parameters.
   * @param id The id of the stat in the database
   * @param statType The type of the stat
   * @param playerID The id of the player
   * @param location The on-court location of the stat
   * @throws GameException Throws a GameException if the stat could not be
   * updated.
   */
  public void updateStat(int id, String statType,
      int playerID, Location location) throws GameException {

    Stat oldStat = sf.getStat(id);
    undoStat(oldStat);

    Stat s = sf.updateStat(id, statType, db.getPlayer(playerID), location);
    addStat(s);

  }

  /**
   * Deletes a stat of the id id from the game. Undoes all of its effects.
   * @param id The stat's id
   * @throws GameException Throws a game exception if the stat could not be
   * deleted.
   */
  public void deleteStat(int id) throws GameException {
    Stat s = sf.removeStat(id);
    undoStat(s);
  }

  /**
   * Updates the bonuses based on the team fouls.
   */
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

  /**
   * Adds a Stat object to the game.
   * @param s The stat object to be added.
   * @return Returns the stat that was added to the game.
   * @throws GameException Throws a game exception if the stat could not be
   * added.
   */
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

  /**
   * Undoes a stat from the game.
   * @param s The stat to be undone
   * @throws GameException Throws a game exception if the stat cannot be undone.
   */
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

  /**
   * Getter for the ruleset governing this game.
   * @return Returns the ruleset of the game.
   */
  public RuleSet getRules() {
    return rules;
  }

  /**
   * Setter for the ruleset of this game.
   * @param rules The new rules to use.
   */
  public void setRules(RuleSet rules) {
    this.rules = rules;
  }

  /**
   * Getter for the number of fouls commited by the home team.
   * @return Returns the int number of home fouls
   */
  public int getHomeFouls() {
    return homeFouls;
  }

  /**
   * Getter for the number of fouls commited by the away team.
   * @return Returns the int number of away fouls
   */
  public int getAwayFouls() {
    return awayFouls;
  }

  /**
   * Getter for the period of the game.
   * @return Returns the period of the game.
   */
  public int getPeriod() {
    return period;
  }

  /**
   * Returns the current lineup of the game (all players on court).
   * @return Returns the current lineup.
   */
  public Lineup getLineup() {
    return lineup;
  }

  /**
   * Getter for a team's bench.
   * @param home A Boolean that is true if the home's bench is to be returned.
   * False otherwise.
   * @return Returns the home bench if home is true, false otherwise.
   */
  public Bench getBench(boolean home) {
    if (home) {
      return homeBench;
    } else {
      return awayBench;
    }
  }

  private void placePlayers(Team h, Team a,
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

  private void defaultStartingLineup(Team h, Team a) throws GameException {

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

  /**
   * Getter for the date of the game.
   * @return Returns the date that the game was played as a local date object.
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Getter for whether the home team is in the bonus.
   * @return Returns true if home team is in bonus.
   */
  public boolean getHomeBonus() {
    return homeBonus;
  }

  /**
   * Getter home team double bonus.
   * @return Returns true if the home team is in the double bonus.
   */
  public boolean getHomeDoubleBonus() {
    return homeDoubleBonus;
  }

  /**
   * Getter for away team bonus.
   * @return Returns true if Away team is in the bonus.
   */
  public boolean getAwayBonus() {
    return awayBonus;
  }

  /**
   * Getter for away team double bonus.
   * @return Returns true if the away team is in the double bonus.
   */
  public boolean getAwayDoubleBonus() {
    return awayDoubleBonus;
  }

  @Override
  public String toString() {
    return awayTeam + " @ " + homeTeam + " (" + date + ")";
  }

  /**
   * Getter for whether the game is a home game for my team.
   * @return Returns true if the game is a hometeam for my team.
   */
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
