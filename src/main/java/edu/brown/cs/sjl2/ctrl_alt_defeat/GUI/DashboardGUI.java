package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;

public class DashboardGUI {

  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public DashboardGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class DashboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Dashboard");
      return new ModelAndView(variables, "dashboard.ftl");
    }
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

}
