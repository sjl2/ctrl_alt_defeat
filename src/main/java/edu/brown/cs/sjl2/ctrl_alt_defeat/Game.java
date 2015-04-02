package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.List;

public class Game {
  private Team homeTeam;
  private Team awayTeam;
  private Lineup homeLineup;
  private Lineup awayLineup;
  private Bench homeBench;
  private Bench awayBench;
  private Scoreboard scoreboard;
  private BoxScore homeBoxScore;
  private BoxScore awayBoxScore;
  private List<Stat> stats;
  
  public Player getPlayerAtPosition(Position pos) {return null;}
  public BoxScore getHomeBoxScore() {
    return homeBoxScore;
  }
  public BoxScore getAwayBoxScore() {
    return awayBoxScore;
  }
  private Scoreboard createScoreBoard() {
    return null;
  }
  public void subPlayer(int idIn, int idOut) {}
  public void takeTimeout(Boolean home) {}
  public void flipPossession() {
    scoreboard.flipPossession();
  }
  public void addStat(Stat s) {}
  public List<Player> getTopPlayers(int n) {
    return null;
  }
  
}
