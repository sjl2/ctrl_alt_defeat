package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;

public class GameGUI {
  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public GameGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class RosterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      Game game = dash.getGame();
      if (game != null) {
        Map<String, Object> variables =
            ImmutableMap.of(
                "home", game.getHome(),
                "away", game.getAway());

        return GSON.toJson(variables);
      } else {
        return null;
      }
    }

  }

  public class StartHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      System.out.println("in the starthandler " + qm.value("opponent"));
      
      Game g = new Game();
      System.out.println("here");
      dash.setGame(g);
      return 15;
      
    }
  }

}
