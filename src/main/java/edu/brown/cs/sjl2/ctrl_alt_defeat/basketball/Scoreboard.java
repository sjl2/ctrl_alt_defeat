package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

public class Scoreboard {
  private int milliseconds;
  private int seconds;
  private int minutes;
  private int quarter;
  private int homeScore;
  private int awayScore;
  private int homeTO;
  private int awayTO;
  private boolean possession;
  private int homeFouls;
  private int awayFouls;
  
  public Scoreboard(RuleSet l) {
    this.milliseconds = 0;
    this.seconds = 0;
    this.minutes = 0;
    this.quarter = 1;
    this.homeScore = 0;
    this.awayScore = 0;
    this.homeTO = l.timeouts();
    this.awayTO = l.timeouts();
    this.possession = true;
    this.awayFouls = 0;
    this.homeFouls = 0;
  }

  public int getMS() {
    return milliseconds;
  }
  
  public int getSeconds() {
    return seconds;
  }
  
  public int getMinutes() {
    return minutes;
  }
  
  public int getQuarter() {
    return quarter;
  }
  
  public int getHomeScore() {
    return homeScore;
  }
  
  public int getAwayScore() {
    return awayScore;
  }
  
  public int getHomeTO() {
    return homeTO;
  }
  
  public int getAwayTO() {
    return awayTO;
  }
  
  public int getHomeFouls() {
    return homeFouls;
  }
  
  public int getAwayFouls() {
    return awayFouls;
  }

  public boolean isPossession() {
    return possession;
  }

  public void flipPossession() {
    this.possession = !possession;
  }
  
  public void takeTimeout(Boolean home) throws ScoreboardException {
    if (home) {
      if (homeTO == 0) {
        throw new ScoreboardException("Home team is out of timeouts");
      } else {
        homeTO--;
      }
    } else {
      if (awayTO == 0) {
        throw new ScoreboardException("Away team is out of timeouts");
      } else {
        awayTO--;
      }
    }
  }

}
