package edu.brown.cs.sjl2.ctrl_alt_defeat.basketball;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;

import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;

/**
 * The current lineup on a court for both teams.
 *
 * @author sjl2
 *
 */
public class Lineup {
  private BiMap<BasketballPosition, Player> players;

  /**
   * The constructor for a Lineup. Creates an empty line up to add players to.
   */
  public Lineup() {
    this.players = EnumHashBiMap.create(BasketballPosition.class);
  }

  /**
   * Getter for the players of the lineup.
   *
   * @return Returns a bimap of the players to positions.
   */
  public BiMap<BasketballPosition, Player> getPlayers() {
    return players;
  }

  /**
   * Substitutes a pIn into the lineup, removing pOut.
   *
   * @param pIn The player going into the lineup
   * @param pOut The player going to the bench.
   * @throws ScoreboardException Throws a Scoreboard Exception on an illegal
   *           substitution.
   */
  public void sub(Player pIn, Player pOut) throws ScoreboardException {
    BasketballPosition b = players.inverse().get(pOut);
    if (b != null) {
      players.put(b, pIn);
    } else {
      throw new ScoreboardException("Illegal substitution: player not on court");
    }
  }

  /**
   * Adds a starter to the lineup after initialization.
   *
   * @param position The Basketball position of the player.
   * @param player The starting player.
   * @return Returns the lineup for convenient chaining of additions.
   * @throws GameException Throws a game exception if the same player is added
   *           multiple times.
   */
  public Lineup addStarter(BasketballPosition position, Player player)
      throws GameException {
    try {
      players.put(position, player);
    } catch (IllegalArgumentException e) {
      String message = "Cannot have the same player start in multiple "
          + "positions.";
      throw new GameException(message);
    }

    return this;
  }

}
