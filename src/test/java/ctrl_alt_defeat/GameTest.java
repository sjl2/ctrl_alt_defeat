package ctrl_alt_defeat;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Bench;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BoxScore;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Lineup;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class GameTest {
  Team home;
  Team away;
  Game game;
  DBManager db;

  @Before
  public void setUp() throws Exception {
    DBManager db = new DBManager("./data/testing/Test.sqlite3");
    db.clearDatabase();

    home = db.createTeam("Parsnips", "Ankit Shah", "#3333ee", "#33ee33", false);

    for (int i = 0; i < 10; i++) {
      db.createPlayer("Ankit Numbah " + i + 1, home.getID(), i + 20, true);
    }

    away = db.createTeam("Potatoes", "Gabe Lyons", "#33ee33", "#3333ee", false);

    for (int i = 0; i < 10; i++) {
      db.createPlayer("Gabe Numbah " + i + 1, away.getID(), i + 20, true);
    }

    game = new Game(home, away, db);
  }

  @Test
  public void subHomePlayer() {
    Lineup l = game.getLineup();
    Bench homeBench = game.getBench(true);

    Player pOut = l.getPlayers().get(BasketballPosition.HomePG);
    Player pIn = homeBench.getPlayers().get(0);

    try {
      game.subPlayer(pIn.getID(), pOut.getID(), true);
    } catch (ScoreboardException e) {
      fail("Sub player threw and exception");
    }

    assertTrue(l.getPlayers().get(BasketballPosition.HomePG).getID() == pIn
        .getID());
  }

  @Test
  public void subAwayPlayer() {
    Lineup l = game.getLineup();
    Bench bench = game.getBench(false);

    Player pOut = l.getPlayers().get(BasketballPosition.AwayPG);
    Player pIn = bench.getPlayers().get(0);

    try {
      game.subPlayer(pIn.getID(), pOut.getID(), false);
    } catch (ScoreboardException e) {
      fail("Sub player threw and exception: " + e.getMessage());
    }

    l = game.getLineup();
    assertTrue(l.getPlayers().get(BasketballPosition.AwayPG).getID() == pIn
        .getID());
  }

  @Test
  public void addingStats() {
    Lineup l = game.getLineup();
    Player p1 = l.getPlayers().get(BasketballPosition.HomeSF);
    Player p2 = l.getPlayers().get(BasketballPosition.HomeSG);
    Random r = new Random();
    try {
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("Assist", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("Assist", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("ThreePointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("ThreePointer", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("FreeThrow", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("FreeThrow", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      BoxScore home = game.getHomeBoxScore();
      assertTrue(game.getHomeScore() == 6 + 6 + 2);
      assertTrue(home.getTeamStats().getAssists() == 2);
      assertTrue(home.getPlayerStats(p1).getPoints() == 10);

    } catch (GameException e) {
      fail();
    }
  }

  @Test
  public void deletingStats() {
    Lineup l = game.getLineup();
    Player p1 = l.getPlayers().get(BasketballPosition.HomeSF);
    Player p2 = l.getPlayers().get(BasketballPosition.HomeSG);
    Random r = new Random();
    try {
      Stat twoPointer = game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("Assist", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("Assist", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      Stat threePointer = game.addStat("ThreePointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("ThreePointer", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      Stat freeThrow = game.addStat("FreeThrow", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("FreeThrow", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.deleteStat(freeThrow.getID());
      game.deleteStat(twoPointer.getID());
      game.deleteStat(threePointer.getID());

      BoxScore home = game.getHomeBoxScore();
      assertTrue(game.getHomeScore() == 4 + 3 + 1);
      assertTrue(home.getPlayerStats(p1).getPoints() == 4);

    } catch (GameException e) {
      fail();
    }

  }

  @Test
  public void updatingStats() {
    Lineup l = game.getLineup();
    Player p1 = l.getPlayers().get(BasketballPosition.HomeSF);
    Player p2 = l.getPlayers().get(BasketballPosition.HomeSG);
    Random r = new Random();
    try {
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("TwoPointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("Assist", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("Assist", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.addStat("ThreePointer", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("ThreePointer", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      Stat freeThrow = game.addStat("FreeThrow", p1.getID(),
          new Location(r.nextDouble(), r.nextDouble()));
      game.addStat("FreeThrow", p2.getID(),
          new Location(r.nextDouble(), r.nextDouble()));

      game.updateStat(freeThrow.getID(), "ThreePointer", freeThrow.getPlayer()
          .getID(), new Location(r.nextDouble(), r.nextDouble()));

      BoxScore home = game.getHomeBoxScore();
      assertTrue(game.getHomeScore() == 6 + 6 + 4);
      assertTrue(home.getPlayerStats(p1).getPoints() == 12);

    } catch (GameException e) {
      fail();
    }

  }

  @Test
  public void incrementPeriod() {
    int curr = game.getPeriod();
    try {
      game.incrementPeriod();
      assertTrue(game.getPeriod() == curr + 1);
    } catch (GameException e) {
      fail();
    }
  }

}
