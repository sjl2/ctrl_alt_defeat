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
/**
 * @author awainger
 *
 */
public class PlaymakerGUI {

  private DBManager dbManager;
  private Dashboard dash;
  private static final Gson GSON = new Gson();

  /**
   * Constructor for playmaker gui class.
   * @param dbManager - DBManager, allows handlers to get data
   * @author awainger
   */
  public PlaymakerGUI(Dashboard dash, DBManager dbManager) {
    this.dbManager = dbManager;
    this.dash = dash;
  }

  /**
   * Playmaker handler, loads main playmaker class.
   * @author awainger
   */
  public class PlaymakerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Playmaker", "errorMessage", "");
      return new ModelAndView(variables, "playmaker.ftl");
    }
  }

  public class WhiteboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Whiteboard", "errorMessage", "");
      return new ModelAndView(variables, "whiteboard.ftl");
    }
  }

  /**
   * Save handler, parses play, saves to database, returns list of play ids and
   * names to front end.
   * @author awainger
   */
  public class SaveHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      int numFrames = Integer.parseInt(qm.value("numFrames"));
      String jsonPlayerString = qm.value("paths");
      String jsonBallString= qm.value("ballPath");

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

      double[][] ballPath = GSON.fromJson(jsonBallString, double[][].class);
      Location[] parsedBallPath = new Location[numFrames];
      for (int frame = 0; frame < numFrames; frame++) {
        double x = ballPath[frame][0];
        double y = ballPath[frame][1];
        parsedBallPath[frame] = new Location(x, y);
      }

      dbManager.savePlay(new Play(name, numFrames, parsedPlayerPaths, parsedBallPath));
      return getPlayNamesFromDB();
    }
  }

  /**
   * Loads a play for the front end.
   * @author awainger
   */
  public class LoadHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      Play play = dbManager.loadPlay(name);
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("play", play).put("errorMessage", "").build();

      return GSON.toJson(variables);
    }
  }

  public class DeleteHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String name = qm.value("name");
      dbManager.deletePlay(name);

      return getPlayNamesFromDB();
    }

  }

  /**
   * Loads list of play names for playmaker sidebar.
   * @author awainger
   */
  public class PlayNamesHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      return getPlayNamesFromDB();
    }
  }

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

        variables = new ImmutableMap.Builder<String, Object>().put("playerNumbers", numbers).put("errorMessage", "").build();
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
    List<String> plays = dbManager.loadPlayNames();
    Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
        .put("plays", plays).put("errorMessage", "").build();

    return GSON.toJson(variables);
  }
}
