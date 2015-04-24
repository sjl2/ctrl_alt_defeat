package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

public class ProRules implements RuleSet{

  @Override
  public int timeouts() {
    return 6;
  }

  @Override
  public int periods() {
    return 4;
  }

  @Override
  public int periodMinutes() {
    return 12;
  }

  @Override
  public int bonus() {
    return 5;
  }

  @Override
  public int doubleBonus() {
    return Integer.MAX_VALUE;
  }

}
