package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.ScoreboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;
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
      int playerID = GSON.fromJson(qm.value("playerID"), Integer.class);
      double x = GSON.fromJson(qm.value("x"), Double.class);
      double y = GSON.fromJson(qm.value("y"), Double.class);
      System.out.println(statID + " " + playerID + " " + x + " " + y);
      Stat toReturn = null;
      try {
        toReturn = dash.getGame().addStat(statID, playerID, new Location(x, y));
      } catch (GameException ex) {
        return ex.getMessage();
      } catch (Exception e) {
        e.printStackTrace();
      }
      HashMap<String, Object> a = new HashMap<String, Object>();
      a.put("stat", toReturn);
      a.put("type", toReturn.getStatType());
      return GSON.toJson(a);
    }
  }

  public class FlipPossessionHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      System.out.println("changed possession");
      dash.getGame().flipPossession();

      return 27;
    }
  }
  
  public class TimeoutHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      System.out.println("timeout " + request.queryMap().value("h"));
      
      try {
        dash.getGame().takeTimeout(GSON.fromJson(request.queryMap().value("h"), Boolean.class));
      } catch (JsonSyntaxException | GameException e) {
        e.printStackTrace();
        System.out.println("mistake handling timeout");
      };

      return 27;
    }
  }

  public class SubHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int inPlayerID = GSON.fromJson(qm.value("in"), Integer.class);
      int outPlayerID = GSON.fromJson(qm.value("out"), Integer.class);
      boolean home = Boolean.parseBoolean(qm.value("home"));
      
      try {
        dash.getGame().subPlayer(inPlayerID, outPlayerID, home);
      } catch (ScoreboardException e) {
        e.printStackTrace();
        System.out.println("there was an error subbing the players");
      } catch (Exception e) {
        System.out.println("the error is not well classified");
      }
      return 25;
    }

  }


}
