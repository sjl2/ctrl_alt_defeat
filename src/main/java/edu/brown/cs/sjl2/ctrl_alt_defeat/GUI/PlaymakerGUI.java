package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.PlaymakerDB;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

/**
 * PlaymakerGUI class, houses all gui handlers related to the playmaker
 * @author awainger
 */
public class PlaymakerGUI {

  private DBManager db;
  private PlaymakerDB playmakerDB;
  private Dashboard dash;
  private static final Gson GSON = new Gson();

  /**
   * Constructor for playmaker gui class.
   * @param dashboard - Dasboard, allows handlers to get game state and teams/players
   * @param db - DBManager, allows handlers to get data
   * @param playmakerDB - PlaymakerDB, allows handlers to get plays from database
   * @author sjl2
   */
  public PlaymakerGUI(Dashboard dashboard, DBManager db,
      PlaymakerDB playmakerDB) {

    this.db = db;
    this.playmakerDB = playmakerDB;
    this.dash = dashboard;
  }

  /**
   * Playmaker handler, loads main playmaker class.
   * @author sjl2
   */
  public class PlaymakerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Playmaker",
          "allTeams", db.getAllTeams(),
          "errorMessage", "");
      return new ModelAndView(variables, "playmaker.ftl");
    }
  }

  /**
   * Loads whiteboard feature of playmaker.
   * @author sjl2
   */
  public class WhiteboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of(
          "tabTitle", "Whiteboard",
          "allTeams", db.getAllTeams(),
          "errorMessage", "");
      return new ModelAndView(variables, "whiteboard.ftl");
    }
  }

  /**
   * Save handler, parses play, saves to database, returns list of play ids and
   * names to front end.
   * @author sjl2
   */
  public class SaveHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      int numFrames = Integer.parseInt(qm.value("numFrames"));
      String jsonPlayerString = qm.value("paths");
      String jsonBallString = qm.value("ballPath");

      double[][][] playerPaths = GSON.fromJson(jsonPlayerString, double[][][].class);
      BasketballPosition[] bballPositions = BasketballPosition.values();
      int numBasketballPlayers = bballPositions.length;
      Location[][] parsedPlayerPaths = new Location[numBasketballPlayers][];
      for (int position = 0; position < numBasketballPlayers; position++) {
        Location[] path = new Location[numFrames];
        for (int frame = 0; frame < numFrames; frame++) {
          double x = playerPaths[position][frame][0];
          double y = playerPaths[position][frame][1];
          path[frame] = new Location(x, y);
        }
        parsedPlayerPaths[position] = path;
      }

      int[] ballPath = GSON.fromJson(jsonBallString, int[].class);

      playmakerDB.savePlay(new Play(name, numFrames, parsedPlayerPaths, ballPath));
      return getPlayNamesFromDB();
    }
  }

  /**
   * Loads a play for the front end.
   * @author sjl2
   */
  public class LoadHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      Play play = playmakerDB.loadPlay(name);
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("play", play).put("errorMessage", "").build();

      return GSON.toJson(variables);
    }
  }


  /**
   * Handles deletion of a play.
   * @author sjl2
   */
  public class DeleteHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      playmakerDB.deletePlay(name);

      return getPlayNamesFromDB();
    }

  }

  /**
   * Loads list of play names for playmaker sidebar.
   * @author sjl2
   */
  public class PlayNamesHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      return getPlayNamesFromDB();
    }
  }

  /**
   * Get numbers for players on court for playmaker.
   * @author sjl2
   */
  public class PlayerNumberHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      Game game = dash.getGame();
      Map<String, Object> variables;
      if(game == null) {
        variables = new ImmutableMap.Builder<String, Object>()
          .put("errorMessage", "").build();
      } else {
        BiMap<BasketballPosition, Player> players = game.getLineup().getPlayers();
        List<Integer> numbers = new ArrayList<>(10);
        for(BasketballPosition bp : BasketballPosition.values()) {
          numbers.add(players.get(bp).getNumber());
        }
        variables = new ImmutableMap.Builder<String, Object>()
            .put("playerNumbers", numbers)
            .put("errorMessage", "")
            .build();
      }
      return GSON.toJson(variables);
    }
  }


  /**
   * Gets play names from DB, used to updated list of plays on front
   * end after each update.
   * @return - String, GSON'ed map from "plays" to the list of play names
   */
  private String getPlayNamesFromDB() {
    List<String> plays = playmakerDB.loadPlayNames();
    Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
        .put("plays", plays).put("errorMessage", "").build();

    return GSON.toJson(variables);
  }
}
