package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
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
        return new ModelAndView(variables, "stats_entry.ftl");
    }

  }

  public class AddStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();

      String statID = qm.value("stat");
      int playerID = GSON.fromJson(qm.value("player"), Integer.class);
      int x = GSON.fromJson(qm.value("x"), Integer.class);
      int y = GSON.fromJson(qm.value("y"), Integer.class);

      dash.getGame().addStatByID(statID, playerID, new Location(x, y));

      return null;
    }
  }

}
