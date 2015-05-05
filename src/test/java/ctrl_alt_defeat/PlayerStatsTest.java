package ctrl_alt_defeat;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.PlayerStats;

public class PlayerStatsTest {

  public PlayerStats ps;
  public Random r = new Random();

  @Before
  public void setUp() throws Exception {
    List<Player> players = new ArrayList<>();
    Player p = new Player(1, "jj", 11, 1, "Parsrs", true);
    players.add(new Player(2, "Ankit", 7, 1, "Parsers", true));
    players.add(new Player(3, "Fred", 23, 1, "Parsers", true));

    Team team = new Team(1, "Parsers", "jj", "#ee3333", "#3333ee", players);

    ps = new PlayerStats(1, team, p);

  }

  @Test
  public void minutes() {
    int numStats = r.nextInt(100) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addMinutes(1);
    }

    assertTrue(ps.getMinutes() == numStats);

    ps.addMinutes(-1);

    assertTrue(ps.getMinutes() == numStats - 1);

    ps.addMinutes(-1 * numStats + 1);

    assertTrue(ps.getMinutes() == 0);

  }

  @Test
  public void TwoPointers() {
    int numStats = r.nextInt(1000) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addTwoPointers(1);
    }

    assertTrue(ps.getTwoPointers() == numStats);

    ps.addTwoPointers(-1);

    assertTrue(ps.getTwoPointers() == numStats - 1);

    ps.addTwoPointers(-1 * numStats + 1);

    assertTrue(ps.getTwoPointers() == 0);

  }

  @Test
  public void TwoPointersA() {
    int numStats = r.nextInt(100) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addTwoPointersA(1);
    }

    assertTrue(ps.getTwoPointersA() == numStats);

    ps.addTwoPointersA(-1);

    assertTrue(ps.getTwoPointersA() == numStats - 1);

    ps.addTwoPointersA(-1 * numStats + 1);

    assertTrue(ps.getTwoPointersA() == 0);

  }

  @Test
  public void ThreePointers() {
    int numStats = r.nextInt(100) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addThreePointers(1);
    }

    assertTrue(ps.getThreePointers() == numStats);

    ps.addThreePointers(-1);

    assertTrue(ps.getThreePointers() == numStats - 1);

    ps.addThreePointers(-1 * numStats + 1);

    assertTrue(ps.getThreePointers() == 0);

  }

  @Test
  public void ThreePointersA() {
    int numStats = r.nextInt(100) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addThreePointersA(1);
    }

    assertTrue(ps.getThreePointersA() == numStats);

    ps.addThreePointersA(-1);

    assertTrue(ps.getThreePointersA() == numStats - 1);

    ps.addThreePointersA(-1 * numStats + 1);

    assertTrue(ps.getThreePointersA() == 0);

  }

  @Test
  public void FreeThrows() {
    int numStats = r.nextInt(100) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addFreeThrows(1);
    }

    assertTrue(ps.getFreeThrows() == numStats);

    ps.addFreeThrows(-1);

    assertTrue(ps.getFreeThrows() == numStats - 1);

    ps.addFreeThrows(-1 * numStats + 1);

    assertTrue(ps.getFreeThrows() == 0);

  }

  @Test
  public void FreeThrowsA() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addFreeThrowsA(1);
    }

    assertTrue(ps.getFreeThrowsA() == numStats);

    ps.addFreeThrowsA(-1);

    assertTrue(ps.getFreeThrowsA() == numStats - 1);

    ps.addFreeThrowsA(-1 * numStats + 1);

    assertTrue(ps.getFreeThrowsA() == 0);

  }

  @Test
  public void OffensiveRebounds() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addOffensiveRebounds(1);
    }

    assertTrue(ps.getOffensiveRebounds() == numStats);

    ps.addOffensiveRebounds(-1);

    assertTrue(ps.getOffensiveRebounds() == numStats - 1);

    ps.addOffensiveRebounds(-1 * numStats + 1);

    assertTrue(ps.getOffensiveRebounds() == 0);

  }

  @Test
  public void DefensiveRebounds() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addDefensiveRebounds(1);
    }

    assertTrue(ps.getDefensiveRebounds() == numStats);

    ps.addDefensiveRebounds(-1);

    assertTrue(ps.getDefensiveRebounds() == numStats - 1);

    ps.addDefensiveRebounds(-1 * numStats + 1);

    assertTrue(ps.getDefensiveRebounds() == 0);

  }

  @Test
  public void Assists() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addAssists(1);
    }

    assertTrue(ps.getAssists() == numStats);

    ps.addAssists(-1);

    assertTrue(ps.getAssists() == numStats - 1);

    ps.addAssists(-1 * numStats + 1);

    assertTrue(ps.getAssists() == 0);

  }

  @Test
  public void Steals() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addSteals(1);
    }

    assertTrue(ps.getSteals() == numStats);

    ps.addSteals(-1);

    assertTrue(ps.getSteals() == numStats - 1);

    ps.addSteals(-1 * numStats + 1);

    assertTrue(ps.getSteals() == 0);

  }

  @Test
  public void Blocks() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addBlocks(1);
    }

    assertTrue(ps.getBlocks() == numStats);

    ps.addBlocks(-1);

    assertTrue(ps.getBlocks() == numStats - 1);

    ps.addBlocks(-1 * numStats + 1);

    assertTrue(ps.getBlocks() == 0);

  }

  @Test
  public void Turnovers() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addTurnovers(1);
    }

    assertTrue(ps.getTurnovers() == numStats);

    ps.addTurnovers(-1);

    assertTrue(ps.getTurnovers() == numStats - 1);

    ps.addTurnovers(-1 * numStats + 1);

    assertTrue(ps.getTurnovers() == 0);

  }

  @Test
  public void OffensiveFouls() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addOffensiveFouls(1);
    }

    assertTrue(ps.getOffensiveFouls() == numStats);

    ps.addOffensiveFouls(-1);

    assertTrue(ps.getOffensiveFouls() == numStats - 1);

    ps.addOffensiveFouls(-1 * numStats + 1);

    assertTrue(ps.getOffensiveFouls() == 0);

  }

  @Test
  public void DefensiveFouls() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addDefensiveFouls(1);
    }

    assertTrue(ps.getDefensiveFouls() == numStats);

    ps.addDefensiveFouls(-1);

    assertTrue(ps.getDefensiveFouls() == numStats - 1);

    ps.addDefensiveFouls(-1 * numStats + 1);

    assertTrue(ps.getDefensiveFouls() == 0);

  }

  @Test
  public void TechnicalFouls() {
    int numStats = r.nextInt(10) + 1;
    for (int i = 0; i < numStats; i++) {
      ps.addTechnicalFouls(1);
    }

    assertTrue(ps.getTechnicalFouls() == numStats);

    ps.addTechnicalFouls(-1);

    assertTrue(ps.getTechnicalFouls() == numStats - 1);

    ps.addTechnicalFouls(-1 * numStats + 1);

    assertTrue(ps.getTechnicalFouls() == 0);

  }

  @Test
  public void FieldGoals() {
    int twoPointers = r.nextInt(10) + 1;
    int threePointers = r.nextInt(10) + 1;

    ps.addTwoPointers(twoPointers);
    ps.addTwoPointersA(twoPointers);

    ps.addThreePointers(threePointers);
    ps.addThreePointersA(threePointers);

    assertTrue(ps.getFieldGoals() == twoPointers + threePointers);

    ps.addFreeThrows(r.nextInt(10));
    ps.addFreeThrowsA(r.nextInt(10));

    assertTrue(ps.getFieldGoals() == twoPointers + threePointers);

  }

  @Test
  public void Points() {
    int twoPointers = r.nextInt(10) + 1;
    int threePointers = r.nextInt(10) + 1;
    int freeThrows = r.nextInt(10) + 1;

    ps.addTwoPointers(twoPointers);
    ps.addTwoPointersA(twoPointers + r.nextInt(10));

    ps.addThreePointers(threePointers);
    ps.addThreePointersA(threePointers + r.nextInt(10));

    ps.addFreeThrows(freeThrows);
    ps.addFreeThrowsA(freeThrows + r.nextInt(10));

    assertTrue(
        ps.getPoints() == 2 * twoPointers + 3 * threePointers + freeThrows);
  }

  @Test
  public void Rebounds() {
    int offensive = r.nextInt(10) + 1;
    int defensive = r.nextInt(10) + 1;

    ps.addOffensiveRebounds(offensive);
    ps.addDefensiveRebounds(defensive);

    assertTrue(ps.getRebounds() == offensive + defensive);

  }

  @Test
  public void Fouls() {
    int offensive = r.nextInt(10) + 1;
    int defensive = r.nextInt(10) + 1;

    ps.addOffensiveFouls(offensive);
    ps.addDefensiveFouls(defensive);

    assertTrue(ps.getPersonalFouls() == offensive + defensive);
  }


}
