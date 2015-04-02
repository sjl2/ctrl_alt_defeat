package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.RuleSet;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Scoreboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class Game {
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
  
  public Game(Team home, Team away) {
    this.homeTeam = home;
    this.awayTeam = away;
    this.lineup = new Lineup();
    this.scoreboard = createScoreboard();
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
  public void addStat(Stat s) {}
  public List<Player> getTopPlayers(int n) {
    //da fuq
    return null;
  }
  public RuleSet getRules() {
    return rules;
  }
  public void setRules(RuleSet rules) {
    this.rules = rules;
  }
  
}
