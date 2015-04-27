package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

public class ProRules implements RuleSet {

  @Override
  public int getTimeOuts() {
    return 6;
  }

  @Override
  public int getPeriods() {
    return 4;
  }

  @Override
  public int getPeriodMinutes() {
    return 12;
  }

  @Override
  public int getBonus() {
    return 5;
  }

  @Override
  public int getDoubleBonus() {
    return Integer.MAX_VALUE;
  }

}
