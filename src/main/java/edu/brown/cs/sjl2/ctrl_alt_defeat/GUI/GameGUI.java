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

  public class RosterHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      Game game = dash.getGame();
      if (game != null) {
        ArrayList<Object> variables = new ArrayList<Object>();
            variables.add(game.getHome().getPrimary());
            variables.add(game.getHome().getSecondary());
            variables.add(game.getAway().getPrimary());
            variables.add(game.getHome().getSecondary());
            variables.add(game.getLineup());
            variables.add(game.getBench(true));
            variables.add(game.getBench(false));

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
      int teamID = Integer.parseInt(qm.value("opponent"));
      boolean isHome = GSON.fromJson(qm.value("is_home"), Boolean.class);
      
      try {
        dash.startGame(isHome, teamID);
      } catch (DashboardException e) {
        return e.getMessage(); 
      }
      
      // TODO StartHandler should spark a game perspective 
      
      return "";
      
    }
  }

}
