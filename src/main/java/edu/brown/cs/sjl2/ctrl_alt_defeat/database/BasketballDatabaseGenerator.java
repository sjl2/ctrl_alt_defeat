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

  private static final int NUMBER_OF_ROUND_ROBINS = 3;
  private static final int NUM_OF_STATS = 20;
  private static final double HALF = 0.5;
  private static final double DEPLETING_RATIO = 0.7;
  private static final int BATCH_SIZE = 5;
  private static final int NUM_FOULS = 3;

  public static void populateDB(DBManager db) {
    Connection conn = db.getConnection();

    List<String> teamNames =
        Arrays.asList(
            "Parsers",
            "Cleveland Caveliers",
            "Seattle Sonics",
            "New York Knicks",
            "Golden State Warriors");

    List<List<String>> players = new ArrayList<>();

    players.add(
        Arrays.asList(
            "jj-32",
            "Nick Goelz-30",
            "Stewart Lynch-15",
            "Alex Wainger-7",
            "Tyler Schicke-53",
            "Ankit Shah-99",
            "Jessica Liang-98"));


    players.add(
        Arrays.asList(
            "Lebron James-23",
            "Kyrie Irving-2",
            "Kevin Love-0",
            "J.R. Smith-5",
            "Timofey Mozgov-20",
            "Tristan Thompson-13",
            "Iman Shumpert-4",
            "James Jones-1",
            "jj-32",
            "Matthew Dellavedova-8",
            "Kendrick Perkins-3"));

    players.add(
        Arrays.asList(
            "Gary Payton-20",
            "Fred Brown-32",
            "Jack Sikma-43",
            "Rashard Lewis-7",
            "Shawn Kemp-40",
            "Gus Williams-1",
            "Dale Ellis-3",
            "Spencer Haywood-24",
            "Ray Allen-34"));

    players.add(
        Arrays.asList(
            "Quincy Acy-4",
            "Cole ALdrich-45",
            "Lou Amundson-21",
            "jj-32",
            "Carmelo Anthony-7",
            "Andrea Bargnani-77",
            "Jose Calderon-3",
            "Cleanthony Early-17",
            "Langston Galloway-2",
            "Tim Hardaway Jr.-5",
            "Shane Larkin-0",
            "Ricardo Ledo-11",
            "Alexey Shved-1",
            "Jason Smith-14",
            "Lance Thomas-42",
            "Travis Wear-6"));

    players.add(
        Arrays.asList(
            "Leandro Barbosa-19",
            "Klay Thompson-11",
            "Andrew Bogut-12",
            "jj-32",
            "Stephen Curry-30",
            "Festus Ezeli-31",
            "Draymond Green-23",
            "Justin Holiday-7",
            "Andre Iguodala-9",
            "Ognjen Kuzmic-1",
            "David Lee-10",
            "Shaun Livingston-34",
            "James Michael McAdoo-20",
            "Brandon Rush-4",
            "Marreese Speights-5",
            "Harrison Barnes-40"));


    List<String> primary =
        Arrays.asList(
            "#3A61A3", "#860038", "#06730B", "#0953A0", "#04529C"
            );

    List<String> second =
        Arrays.asList(
            "#F29687", "#FDBB30", "#FFBF0D", "#FF7518", "#FFCC33"
            );

    List<Team> teams = new ArrayList<>();

    try {

      conn.setAutoCommit(false);

      // Create Players and Teams -> Make a list of teams to play games
      for (int i = 0; i < teamNames.size(); i++) {

        boolean myTeam = false;

        if (i == 0) {
          myTeam = true;
        }

        Team t = db.createTeam(
            teamNames.get(i),
            "Coach Ankit",
            primary.get(i),
            second.get(i),
            myTeam);

        for (String p : players.get(i)) {
          String[] p_split = p.split("-");
          String name = p_split[0];
          int number = Integer.parseInt(p_split[1]);
          db.createPlayer(
              name,
              t.getID(),
              number,
              true);

        }

        teams.add(t);

      }

      int numGames =
          NUMBER_OF_ROUND_ROBINS * teams.size() * (teams.size() - 1);
      int gamesCompleted = 0;

      // Play multiple round robin tournaments in random years.
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
        randomPlayerStats(game, p, db, percent);

        if (p.getID() % 2 == 0) {
          percent = percent * DEPLETING_RATIO;
        }

      }
    }
  }

  private static void randomPlayerStats(Game game, Player p, DBManager db,
      Double percentOfGame) throws GameException {

    Random r = new Random();
    List<String> types = StatFactory.getTypes();

    for (String type : types) {

      int numStats =
          new Double(percentOfGame * r.nextDouble() * NUM_OF_STATS).intValue();

      switch (type) {
        case "OffensiveFoul":
          if (numStats > NUM_FOULS) {
            numStats = NUM_FOULS;
          }
        case "DefensiveFoul":
          if (numStats > NUM_FOULS) {
            numStats = new Double(r.nextDouble() * NUM_FOULS)
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

