package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

/**
 * The rule set for NBA Professional games. Current Default.
 *
 * @author sjl2
 *
 */
public class ProRules implements RuleSet {
  private static final int TIME_OUTS = 6;
  private static final int PERIODS = 4;
  private static final int PERIOD_MINUTES = 12;
  private static final int BONUS_FOULS = 5;
  private static final int OT_TIME_OUTS = 3;

  @Override
  public int getTimeOuts() {
    return TIME_OUTS;
  }

  @Override
  public int getPeriods() {
    return PERIODS;
  }

  @Override
  public int getPeriodMinutes() {
    return PERIOD_MINUTES;
  }

  @Override
  public int getBonus() {
    return BONUS_FOULS;
  }

  @Override
  public int getDoubleBonus() {
    return Integer.MAX_VALUE;
  }

  @Override
  public int getOTTimeOuts() {
    return OT_TIME_OUTS;
  }

}
