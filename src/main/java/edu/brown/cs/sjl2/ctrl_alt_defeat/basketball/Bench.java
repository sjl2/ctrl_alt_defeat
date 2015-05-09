package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for a team's bench.
 *
 * @author sjl2
 *
 */
public class Bench {
  private List<Player> players;
  // private int teamID;
  private Team team;

  /**
   * Constructs a bench for a team.
   * 
   * @param t The bench's team.
   */
  public Bench(Team t) {
    this.team = t;
    this.players = new ArrayList<Player>();
  }

  /**
   * Getter for the players on the bench.
   * 
   * @return Returns a List of players on the bench.
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Get's the team associated with the bench.
   * 
   * @return Returns the team object of the bench's team.
   */
  public Team getTeam() {
    return team;
  }

  /**
   * Subs a player pIn from the bench with a player pOut.
   * 
   * @param pIn The player leaving the bench.
   * @param pOut The player entering the bench.
   * @throws ScoreboardException Throws a ScoreboardException for illegal subs.
   */
  public void sub(Player pIn, Player pOut) throws ScoreboardException {
    if (players.remove(pIn)) {
      players.add(pOut);
    } else {
      throw new ScoreboardException(
          "Illegal substitution: player subbing in not found");
    }
  }
}
