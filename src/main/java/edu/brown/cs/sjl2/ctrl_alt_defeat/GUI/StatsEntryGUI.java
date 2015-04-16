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

//      if (dash.getGame() != null) {
        return new ModelAndView(variables, "stats_entry.ftl");
//      } else {
//        return new ModelAndView(variables, "no_game.ftl");
//      }

    }

  }

  public class AddStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String statID = qm.value("statID");
      int playerID = 1;
      double x = GSON.fromJson(qm.value("x"), Double.class);
      double y = GSON.fromJson(qm.value("y"), Double.class);
      System.out.println(statID + " " + playerID + " " + x + " " + y);

      try {
        dash.getGame().addStat(statID, playerID, new Location(x, y));
      } catch (GameException ex) {
        return ex.getMessage();
      } catch (Exception e) {
        return "there was an error adding the stat";
      }

      return 28;
    }
  }

}
