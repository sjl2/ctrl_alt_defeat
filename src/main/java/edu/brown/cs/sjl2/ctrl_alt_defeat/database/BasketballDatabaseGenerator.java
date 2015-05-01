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

  private static final int NUM_OF_STATS = 15;
  private static final double HALF = 0.5;
  private static final double DEPLETING_RATIO = 0.6;
  private static final double FRONT_COURT = 0.8;
  private static final int BATCH_SIZE = 5;
  private static final int NUM_FOULS = 3;
  private static final int NUM_FG = 4;
  private static final double BUFFER = 0.05;
  private static final int DEPLETE_STALL = 3;

  // For intelligent
  private static final double X_MAX = .5;
  private static final double Y_MAX = 1.0;

  public static void populateDB(DBManager db, int roundRobins, boolean intelligent) {
    Connection conn = db.getConnection();

    List<String> teamNames = null;
    List<List<String>> players = new ArrayList<>();
    List<String> primary = null;
    List<String> secondary = null;
    
    List<Team> teams = new ArrayList<>();

    if (intelligent) {
      teamNames = Arrays.asList("Parsnips", "Potatoes");
      players.add(
          Arrays.asList(
              "PAR Good PG-1",
              "PAR Good SG-2",
              "PAR Good SF-3",
              "PAR Good PF-4",
              "PAR Good C-5",
              "PAR Bad PG-6",
              "PAR Bad SG-7",
              "PAR Bad SF-8",
              "PAR Bad PF-9",
              "PAR Bad C-10"));
      players.add(
          Arrays.asList(
              "POT Good PG-1",
              "POT Good SG-2",
              "POT Good SF-3",
              "POT Good PF-4",
              "POT Good C-5",
              "POT Bad PG-6",
              "POT Bad SG-7",
              "POT Bad SF-8",
              "POT Bad PF-9",
              "POT Bad C-10"));
      
      primary =
          Arrays.asList("#3A61A3", "#860038");
      secondary = 
          Arrays.asList("#F29687", "#FDBB30");

    } else {
      teamNames =
          Arrays.asList(
              "Parsers",
              "Cleveland Caveliers",
              "Seattle Sonics",
              "New York Knicks",
              "Golden State Warriors");

      players.add(
          Arrays.asList(
              "jj-32",
              "Nick Goelz-30",
              "Stewart Lynch-15",
              "Alex Wainger-7",
              "Tyler Schicke-53",
              "Ankit Shah-21",
              "Gabe Lyons-64"));

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
              "Travis Wear-6",
              "Jahlil Okafor-15"));
  
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
  
  
      primary =
          Arrays.asList("#3A61A3", "#860038", "#06730B", "#0953A0", "#04529C");
  
      secondary = 
          Arrays.asList("#F29687", "#FDBB30", "#FFBF0D", "#FF7518", "#FFCC33");
    }

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
            secondary.get(i),
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
          roundRobins * teams.size() * (teams.size() - 1);
      int gamesCompleted = 0;

      // Play multiple round robin tournaments in random years.
      for (int i = 0; i < roundRobins; i++) {
        for (Team home : teams) {
          for (Team away :  teams) {
            if (home.getID() != away.getID()) {
              Game game = new Game(home, away, db);
              if (intelligent) {
                intelligentGameStats(game, home, away, db);
              } else {
                randomGameStats(game, home, away, db);
              }
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
  
  private static void intelligentGameStats(Game game, Team home, Team away, DBManager db) {
    Random r = new Random();
    
    List<Team> gameTeams = Arrays.asList(home, away);
    
    for (Team gameTeam : gameTeams) {
      
    }
  }

  private static Location generateLocation(String stat) {
    switch (stat) {
      case "FreeThrow":
        return new Location(.21, .5);
      case "MissedFreeThrow":
        return new Location(.21, .5);
      case "TwoPointer":

      case "MissedTwoPointer":

      case "ThreePointer":
        
      case "MissedThreePointer":
        
      case "OffensiveFoul":
        
      case "OffensiveRebound":
        
      case "Turnover":
        
      case "Assist":
        
      case "DefensiveRebound":
        
      case "Block":
        
      case "DefensiveFoul":
        
      case "TechnicalFoul":
        
      case "Steal":
        
      default:
        return null;
    }
  }
  
  private static Location genLocInPaint() {
    Location topLeft = new Location(.01, .38);
    Location bottomRight = new Location(.21, .615);
    Random r= new Random();
    double x = topLeft.getX() + (bottomRight.getX() - topLeft.getX()) * r.nextDouble();
    double y = topLeft.getY() + (bottomRight.getY() - topLeft.getY()) * r.nextDouble();
    return new Location(x, y);
  }

  private static Location genLocMidRange() {
    return null;
  }
  
  private static Location getLocDeep() {
    return null;
  }
  
  private static Location getLocAnywhere() {
    return null;
  }

  private static void randomGameStats(Game game, Team home, Team away,
      DBManager db) throws GameException {

    Random r = new Random();

    List<Team> gameTeams = Arrays.asList(home, away);

    for (Team gameTeam : gameTeams) {
      double percent = 1;
      for (Player p : gameTeam.getPlayers()) {
        randomPlayerStats(game, p, db, percent);

        if (r.nextInt(DEPLETE_STALL) == 0) {
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
        case "TwoPointer":
          if (numStats > NUM_FG) {
            numStats = new Double(r.nextDouble() * numStats)
              .intValue();
          }
          break;
        case "ThreePointer":
          if (numStats > NUM_FG) {
            numStats = new Double(r.nextDouble() * numStats)
              .intValue();
          }
          break;
        case "MissedThreePointer":
          if (numStats > NUM_FG) {
            numStats = new Double(r.nextDouble() * NUM_FG)
              .intValue();
          }
          break;
        case "MissedTwoPointer":
          if (numStats > NUM_FG) {
            numStats = new Double(r.nextDouble() * NUM_FG)
              .intValue();
          }
          break;
        case "FreeThrow":
          if (numStats > NUM_FG) {
            numStats = new Double(r.nextDouble() * NUM_FG)
              .intValue();
          }
          break;
        case "MissedFreeThrow":
          if (numStats > 2) {
            numStats = new Double(r.nextDouble() * NUM_FG)
              .intValue();
          }
          break;
        case "OffensiveFoul":
          if (numStats > NUM_FOULS) {
            numStats = NUM_FOULS;
          }
          break;
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

        double x;
        double y;
        if (game.isHome(p.getTeamID())) {
          if ((double) period / numPeriods < HALF) {
            x = 1 - FRONT_COURT * HALF * r.nextDouble();
            y = r.nextDouble();
          } else {
            x = FRONT_COURT * HALF * r.nextDouble();
            y = r.nextDouble();
          }
        } else {
          if ((double) period / numPeriods < HALF) {
            x = FRONT_COURT * HALF * r.nextDouble();
            y = r.nextDouble();
          } else {
            x = 1 - FRONT_COURT * HALF * r.nextDouble();
            y = r.nextDouble();
          }
        }

        if (x < BUFFER) {
          x = BUFFER;
        } else if (x > 1 - BUFFER) {
          x = 1 - BUFFER;
        }

        if (y < BUFFER) {
          y = BUFFER;
        } else if (y > 1 - BUFFER) {
          y = 1 - BUFFER;
        }

        Location loc =  new Location(x,  y);

        Stat s = StatFactory.newStat(type, id, p, loc, period);
        db.createStat(s, game.getID());
        game.addStat(s);
      }

    }

  }

}

