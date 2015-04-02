package edu.brown.cs.sjl2.ctrl_alt_defeat;

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

}
