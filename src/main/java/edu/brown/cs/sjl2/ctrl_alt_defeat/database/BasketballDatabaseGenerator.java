package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.StatFactory;

public class BasketballDatabaseGenerator {

  private static final int NUMBER_OF_PLAYERS = 12;
  private static final int NUMBER_OF_ROUND_ROBINS = 10;
  private static final int NUM_OF_STATS = 25;
  private static final int NUMBER_BASE = 20;
  private static final double HALF = 0.5;
  private static final double DEPLETING_RATIO = 0.9;
  private static final int BATCH_SIZE = 5;

  public static void populateDB(DBManager db) {
    Connection conn = db.getConnection();

    List<String> teamNames =
        Arrays.asList(
            "Cleveland Caveliers",
            "Seattle Sonics",
            "New York Knicks",
            "Golden State Warriors");

    List<String> players =
        Arrays.asList(
            "Lebron James",
            "Ray Allen",
            "Jahlil Okafor",
            "Stephen Curry");

    List<String> primary =
        Arrays.asList(
            "red", "green", "orange", "blue"
            );

    List<String> second =
        Arrays.asList(
            "white", "yellow", "blue", "yellow"
            );

    List<Team> teams = new ArrayList<>();

    try {

      conn.setAutoCommit(false);

      // Create Players and Teams
      for (int i = 0; i < teamNames.size(); i++) {
        Team t = db.createTeam(
            teamNames.get(i),
            "Coach Bob " + i,
            primary.get(i),
            second.get(i),
            false);

        for (int j = 0; j < NUMBER_OF_PLAYERS; j++) {
          String suffix = "";
          if (j != 0) {
            suffix +=  " " + j;
          }

          db.createPlayer(
              players.get(i) + suffix,
              t.getID(),
              j + NUMBER_BASE,
              true);

        }

        teams.add(t);
      }

      int numGames =
          NUMBER_OF_ROUND_ROBINS * teams.size() * (teams.size() - 1);
      int gamesCompleted = 0;

      for (int i = 0; i < NUMBER_OF_ROUND_ROBINS; i++) {
        for (Team home : teams) {
          for (Team away :  teams) {
            if (home.getID() != away.getID()) {
              Game game = new Game(home, away, db);
              randomGameStats(game, home, away, db);
              if (gamesCompleted % BATCH_SIZE == 0) {
                String status = "Progress: " + gamesCompleted + " of "
                    + numGames + " completed.";
                System.out.println(status);
              }
              gamesCompleted++;
            }
          }
        }
      }

      conn.commit();
      conn.setAutoCommit(true);
    } catch (SQLException e) {
      db.close();
      String message = "Failed to populate db with random game date.";
      throw new RuntimeException(message + e.getMessage());
    } catch (GameException e) {
      db.close();
      String message = "Failed to populate db with random game date.";
      throw new RuntimeException(message + e.getMessage());
    }
  }

  private static void randomGameStats(Game game, Team home, Team away,
      DBManager db) throws GameException {

    List<Team> gameTeams = Arrays.asList(home, away);

    for (Team gameTeam : gameTeams) {
      double percent = 1;
      for (Player p : gameTeam.getPlayers()) {
        randomPlayerStats(game, p, db, percent * DEPLETING_RATIO);
      }
    }
  }

  private static void randomPlayerStats(Game game, Player p, DBManager db,
      Double percentOfGame) throws GameException {

    Random r = new Random();
    List<String> types = StatFactory.getTypes();

    for (String type : types) {

      int numStats = new Double(HALF * r.nextDouble() * NUM_OF_STATS)
        .intValue();

      switch (type) {
        case "OffensiveFoul":
          if (numStats > 2) {
            numStats = 2;
          }
        case "DefensiveFoul":
          if (numStats > 2) {
            numStats = new Double(r.nextDouble() * 2)
              .intValue();
          }
          break;
        default:
          if (numStats > 5) {
            numStats = numStats / 2;
          }
      }

      for (int z = 0; z < numStats; z++) {

        int id = db.getNextID("stat");

        int numPeriods = game.getRules().getPeriods();
        int period = r.nextInt(numPeriods - 1) + 1;

        Location loc;
        if (game.isHome(p.getTeamID())) {
          if ((double) period / numPeriods < HALF) {
            loc = new Location(HALF + HALF * r.nextDouble(), r.nextDouble());
          } else {
            loc = new Location(r.nextDouble(), r.nextDouble());
          }
        } else {
          if ((double) period / numPeriods < HALF) {
            loc = new Location(r.nextDouble(), r.nextDouble());
          } else {
            loc = new Location(HALF + HALF * r.nextDouble(), r.nextDouble());
          }
        }

        Stat s = StatFactory.newStat(type, id, p, loc, period);
        game.addStat(s);
      }

    }

  }

}

