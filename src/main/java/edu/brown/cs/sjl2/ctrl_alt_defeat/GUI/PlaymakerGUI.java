package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.playmaker.Play;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public class PlaymakerGUI {

  private DBManager dbManager;

  private static final Gson GSON = new Gson();

  
  /**
   * @param dbManager
   * @author awainger
   */
  public PlaymakerGUI(DBManager dbManager) {
    this.dbManager = dbManager;
  }

  /**
   * Playmaker handler, loads main playmaker class.
   *
   * @author awainger
   */
  public class PlaymakerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("title", "Ctrl Alt Defeat: Playmaker");
      return new ModelAndView(variables, "playmaker.ftl");
    }
  }


  /**
   * Save handler, parses play, saves to database, returns list of
   * play ids and names to front end.
   *
   * @author awainger
   */
  public class SaveHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int id = Integer.parseInt(qm.value("id"));
      String name = qm.value("name");
      int numFrames = Integer.parseInt(qm.value("numFrames"));
      String jsonString = qm.value("paths");

      int[][][] jsonPaths  = GSON.fromJson(jsonString, int[][][].class);
      BasketballPosition[] bballPositions = BasketballPosition.values();
      int numBasketballPlayers = bballPositions.length;
      Location[][] paths = new Location[numBasketballPlayers][];
      for (int position = 0; position < numBasketballPlayers; position++) {
        Location[] path = new Location[numFrames];
        for (int frame = 0; frame < paths[position].length; frame++) {
          int x = jsonPaths[position][frame][0];
          int y = jsonPaths[position][frame][1];
          path[frame] = new Location(x, y);
        }
        paths[position] = path;
      }

      dbManager.savePlay(new Play(id, name, numFrames, paths));
      Map<Integer, String> plays = dbManager.loadPlayIDs();
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("plays", plays)
          .build();

      return GSON.toJson(variables);
    }
  }
  
  public class LoadHandler implements Route {
    
  }
}
