package edu.brown.cs.sjl2.ctrl_alt_defeat.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  private static final int AVE_POSSESSIONS = 194;

  private static Map<String, Map<String, String>> teamToPlayerToType;


  public static void populateDB(DBManager db, int roundRobins, boolean intelligent) {
    Connection conn = db.getConnection();

    List<String> teamNames = null;
    List<List<String>> players = new ArrayList<>();
    List<String> primary = null;
    List<String> secondary = null;
    teamToPlayerToType = new HashMap<>();

    List<Team> teams = new ArrayList<>();

    if (intelligent) {
      teamNames = Arrays.asList("Parsnips", "Potatoes");
      players.add(
          Arrays.asList(
              "PAR Good PG-1-Good PG Shooter Left",
              "PAR Good SG-2-Good SG Shooter Middle",
              "PAR Good SF-3-Good SF Paint Everywhere",
              "PAR Good PF-4-Good PF Shooter Right",
              "PAR Good C-5-Good C Paint Everywhere",
              "PAR Bad PG-6-Bad PG Shooter Middle",
              "PAR Bad SG-7-Bad SG Paint Left",
              "PAR Bad SF-8-Bad SF Shooter Right",
              "PAR Bad PF-9-Bad PF Paint Everywhere",
              "PAR Bad C-10-Bad C Paint Left"));
      players.add(
          Arrays.asList(
              "POT Good PG-1-Good PG Paint Everywhere",
              "POT Good SG-2-Good SG Shooter Middle",
              "POT Good SF-3-Good SF Shooter Left",
              "POT Good PF-4-Good PF Shooter Right",
              "POT Good C-5-Good C Paint Middle",
              "POT Bad PG-6-Bad PG Shooter Everywhere",
              "POT Bad SG-7-Bad SG Shooter Right",
              "POT Bad SF-8-Bad SF Paint Middle",
              "POT Bad PF-9-Bad PF Shooter Middle",
              "POT Bad C-10-Bad C Paint Everywhere"));

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

        Map<String, String> playerTypes = new HashMap<>();
        for (String p : players.get(i)) {
          String[] p_split = p.split("-");
          String name = p_split[0];
          int number = Integer.parseInt(p_split[1]);
          db.createPlayer(
              name,
              t.getID(),
              number,
              true);

          if (intelligent) {
            playerTypes.put(p_split[0], p_split[2]);
          }
        }

        teams.add(t);
        if (intelligent) {
          teamToPlayerToType.put(teamNames.get(i), playerTypes);
        }
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
  
  private static void intelligentGameStats(Game game, Team home, Team away, DBManager db) throws GameException {
    Random r = new Random();
    int possessions = (int) (AVE_POSSESSIONS + (2.5 * r.nextGaussian()));
    Team currentTeam = home;
    Team otherTeam = away;

    for (int i = 0; i < possessions; i++) {
      List<Object> statPlayer = generateIntelligentStat(currentTeam.getName(), currentTeam.getPlayers(), otherTeam.getPlayers());
      for (int j = 0; j < statPlayer.size(); j += 2) {
        String stat = (String) statPlayer.get(j);

        if (!(stat.contains("Missed") || stat.equals("Block")
            || stat.equals("Assist") || stat.equals("Steal")
            || stat.equals("OffensiveRebound") || stat.equals("DefensiveFoul"))) {
          Team temp = currentTeam;
          currentTeam = otherTeam;
          otherTeam = temp;
        }

        Player p = (Player) statPlayer.get(j + 1);

        int id = db.getNextID("stat");

        int numPeriods = game.getRules().getPeriods();
        int period = r.nextInt(numPeriods) + 1;

        Location loc = null;
        if (stat.contains("TwoPointer") || stat.contains("ThreePointer")) {
          loc = generateShotLocation(stat, p);
        } else {
          loc =  generateLocation(stat);
        }

        Stat s = StatFactory.newStat(stat, id, p, loc, period);
        db.createStat(s, game.getID());
        game.addStat(s);
      }

    }
  }
  
  private static List<Object> generateIntelligentStat(String currTeamName, List<Player> currentPlayers, List<Player> otherPlayers) {
    List<Object> statPlayer = new ArrayList<Object>();
    Random r = new Random();
    double statTypeDet = r.nextDouble();

    /***** SHOT *****/
    if (statTypeDet < .75 + (.02 * r.nextGaussian())) {
      Player p = generatePlayer(currentPlayers);
      String shotType = generateShot(teamToPlayerToType.get(currTeamName).get(p.getName()));
      if (shotType.contains("Missed")) {

        /***** BLOCK *****/
        double block = r.nextDouble();
        if (block < .035 + (.01 * r.nextGaussian())) {
          statPlayer.add("Block");
          statPlayer.add(generatePlayer(otherPlayers));
        }

        /***** REBOUND *****/
        double rebound = r.nextDouble();
        if (rebound < .75 * (.03 * r.nextGaussian())) {
          statPlayer.add("DefensiveRebound");
          statPlayer.add(generatePlayer(otherPlayers));
        } else {
          statPlayer.add("OffensiveRebound");
          statPlayer.add(generatePlayer(currentPlayers));
        }

      } else {
        /***** ASSIST *****/
        double assist = r.nextDouble();
        if (assist < .58 + (.03 * r.nextGaussian())) {
          statPlayer.add("Assist");
          statPlayer.add(generatePlayer(currentPlayers));
        }
      }
      statPlayer.add(shotType);
      statPlayer.add(p);

      /***** TURNOVER *****/
    } else if (statTypeDet < .85 + (.02 * r.nextGaussian())) {
      statPlayer.add("Turnover");
      statPlayer.add(generatePlayer(currentPlayers));
      double turnover = r.nextDouble();
      
      /***** STEAL *****/
      if (turnover < .60 + (.02 * r.nextGaussian())) {
        statPlayer.add("Steal");
        statPlayer.add(generatePlayer(otherPlayers));
        
        /***** OFFENSIVE FOUL *****/
      } else if (turnover < .68 + (.02 * r.nextGaussian())) {
        statPlayer.add("OffensiveFoul");
        statPlayer.add(generatePlayer(currentPlayers));
      }

      /***** DEFENSIVE FOUL *****/
    } else if (statTypeDet < .995) {
      statPlayer.add("DefensiveFoul");
      statPlayer.add(generatePlayer(otherPlayers));
      
      /***** SHOOTING FOUL *****/
      if (r.nextDouble() < .5) {
        if (r.nextDouble() < .7 + (.5 * r.nextGaussian())) {
          statPlayer.add("FreeThrow");
          statPlayer.add(generatePlayer(currentPlayers));
        } else {
          statPlayer.add("MissedFreeThrow");
          statPlayer.add(generatePlayer(currentPlayers));
        }
      }

      /***** TECHNICAL FOUL *****/
    } else {
      statPlayer.add("TechnicalFoul");
      if (r.nextDouble() < .5) {
        /***** FREE THROWS *****/
        statPlayer.add(generatePlayer(currentPlayers));
        if (r.nextDouble() < .7 + (.5 * r.nextGaussian())) {
          statPlayer.add("FreeThrow");
          statPlayer.add(generatePlayer(otherPlayers));
        } else {
          statPlayer.add("MissedFreeThrow");
          statPlayer.add(generatePlayer(otherPlayers));
        }
      } else {
        statPlayer.add(generatePlayer(otherPlayers));
        if (r.nextDouble() < .7 + (.5 * r.nextGaussian())) {
          statPlayer.add("FreeThrow");
          statPlayer.add(generatePlayer(currentPlayers));
        } else {
          statPlayer.add("MissedFreeThrow");
          statPlayer.add(generatePlayer(currentPlayers));
        }
      }
    }

    return statPlayer;
  }

  // TODO OVER HERE
  private static String generateShot(String playerType) {
    if (playerType.contains("Paint")) {
      
    } else if (playerType.contains("Shooter")) {
      
    }
    
    return null;
  }

  private static Player generatePlayer(List<Player> players) {
    Random r = new Random();
    double starterOrReserve = r.nextDouble();
    if (starterOrReserve < .75  + (.02 * r.nextGaussian())) {
      return players.get(r.nextInt(5));
    } else {
      return players.get(r.nextInt(players.size() - 5) + 5);
    }
  }
  
  // TODO OVER HERE
  private static Location generateShotLocation(String shotType, Player p) {
    Random r = new Random();
    switch (shotType) {
    case "TwoPointer":
      if (r.nextDouble() < .33) {
        return genLocInPaint();
      } else {
        return genLocMidRange();
      }
    case "MissedTwoPointer":
      if (r.nextDouble() < .33) {
        return genLocInPaint();
      } else {
        return genLocMidRange();
      }
    case "ThreePointer":
      return genLocThree();
    case "MissedThreePointer":
      return genLocThree();
    default:
      return new Location(0,0);
    }
  }

  private static Location generateLocation(String stat) {
    Random r = new Random();
    switch (stat) {
      case "FreeThrow":
        return new Location(.21, .5);
      case "MissedFreeThrow":
        return new Location(.21, .5);
      case "OffensiveFoul":
        return genLocAnywhere();
      case "OffensiveRebound":
        if (r.nextDouble() < .75) {
          return genLocInPaint();
        } else {
          return genLocMidRange();
        }
      case "Turnover":
        return genLocAnywhere();
      case "Assist":
        if (r.nextDouble() < .5) {
          return genLocThree();
        } else {
          return genLocMidRange();
        }
      case "DefensiveRebound":
        if (r.nextDouble() < .75) {
          return genLocInPaint();
        } else {
          return genLocMidRange();
        }
      case "Block":
        double det = r.nextDouble();
        if (det < .6) {
          return genLocInPaint();
        } else if (det < .95){
          return genLocMidRange();
        } else {
          return genLocThree();
        }
      case "DefensiveFoul":
        return genLocAnywhere();
      case "TechnicalFoul":
        return genLocAnywhere();
      case "Steal":
        return genLocAnywhere();
      default:
        return null;
    }
  }

  private static Location genLocInPaint() {
    Location topLeft = new Location(.01, .38);
    Location bottomRight = new Location(.21, .615);
    Random r = new Random();
    double x = topLeft.getX() + (bottomRight.getX() - topLeft.getX()) * r.nextDouble();
    double y = topLeft.getY() + (bottomRight.getY() - topLeft.getY()) * r.nextDouble();
    return new Location(x, y);
  }

  private static Location genLocMidRange() {
    Location topLeft = new Location(.01, .175);
    Location bottomRight = new Location(.21, .82);
    Random r = new Random();
    double x = topLeft.getX() + (bottomRight.getX() - topLeft.getX()) * r.nextDouble();
    double y = topLeft.getY() + (bottomRight.getY() - topLeft.getY()) * r.nextDouble();
    return new Location(x, y);
  }
  
  private static Location genLocThree() {
    Random r = new Random();
    double angle;
    do {
      angle = 90 + (90 * r.nextGaussian());
    } while (angle < 0 || angle > 180);

    double randomRange = 1.0 * r.nextDouble();
    double radius = randomRange + 24.5;
    double x = 5.0 + Math.sin(Math.toRadians(angle)) * radius;
    double y = 25.0 - Math.cos(Math.toRadians(angle)) * radius;

    if (y < 2) {
      y = 2.0 + (3.0 * r.nextDouble());
    } else if (y > 48) {
      y = 48 - (3.0 * r.nextDouble());
    }

    return convertToDBRepresentation(new Location(x, y));
  }
  
  private static Location genLocAnywhere() {
    Random r = new Random();
    double x = .4 * r.nextDouble();
    double y = r.nextDouble();
    
    return new Location(x, y);
  }
  
  private static Location convertToDBRepresentation(Location loc) {
    double x_ = loc.getX() / 94.0;
    double y_ = loc.getY() / 50.0;
    return new Location(x_, y_);
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
        int period = r.nextInt(numPeriods) + 1;
        
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

