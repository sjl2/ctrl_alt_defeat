package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public class StatsEntryGUI {

  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public StatsEntryGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class StatsEntryHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Stats Entry");

     // if (dash.getGame() != null) {
        return new ModelAndView(variables, "stats_entry.ftl");
   //   } else {
     //   return new ModelAndView(variables, "no_game.ftl");
     // }

    }

  }

  public class AddStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      System.out.println("here");
      String statID = qm.value("statID");
      int playerID = GSON.fromJson(qm.value("playerID"), Integer.class);
      int x = GSON.fromJson(qm.value("x"), Integer.class);
      int y = GSON.fromJson(qm.value("y"), Integer.class);

      try {
        dash.getGame().addStat(statID, playerID, new Location(x, y));
      } catch (GameException ex) {
        return ex.getMessage();
      }

      return "";
    }
  }

}
