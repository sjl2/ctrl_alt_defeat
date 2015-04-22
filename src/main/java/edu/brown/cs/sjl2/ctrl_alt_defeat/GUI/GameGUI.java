package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.Map;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;

public class GameGUI {
  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public GameGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class StatPageHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      Game game = dash.getGame();
      if (game != null) {
        ArrayList<Object> rosterInfo = new ArrayList<Object>();
            rosterInfo.add(game.getHome().getPrimary());
            rosterInfo.add(game.getHome().getSecondary());
            rosterInfo.add(game.getAway().getPrimary());
            rosterInfo.add(game.getHome().getSecondary());
            rosterInfo.add(game.getLineup());
            rosterInfo.add(game.getBench(true));
            rosterInfo.add(game.getBench(false));

        Map<String, Object> toReturn =
            ImmutableMap.of("roster", rosterInfo, "stats", game.getAllStats());

        return GSON.toJson(toReturn);
      } else {
        return null;
      }
    }

  }

  public class StartHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int teamID = Integer.parseInt(qm.value("opponent"));
      boolean isHome = GSON.fromJson(qm.value("is_home"), Boolean.class);

      try {
        dash.startGame(isHome, teamID);
      } catch (DashboardException e) {
        return e.getMessage();
      }

      return "";

    }
  }

}
