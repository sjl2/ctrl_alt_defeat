package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
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
      Game g = dash.getGame();

      List<Stat> s;
      Boolean isGame = true;
      if (g != null) {
        s = g.getAllStats();
      } else {
        s = new ArrayList<Stat>();
        isGame = false;
      }

      Collections.reverse(s);
      Map<String, Object> variables =
                ImmutableMap.of("tabTitle", "Stats Entry",
                          "stats", s,
                          "isGame", isGame,
                          "errorMessage", "");

        return new ModelAndView(variables, "stats_entry.ftl");

    }

  }

  public class AddStatHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String statID = qm.value("statID");
      int playerID = GSON.fromJson(qm.value("playerID"), Integer.class);
      double x = GSON.fromJson(qm.value("x"), Double.class);
      double y = GSON.fromJson(qm.value("y"), Double.class);
      System.out.println(statID + " " + playerID + " " + x + " " + y);

      Stat s = null;

      try {
        s = dash.getGame().addStat(statID, playerID, new Location(x, y));
      } catch (GameException ex) {
        return null;// TODO ex.getMessage();
      } catch (Exception e) {
        e.printStackTrace();
      }
      HashMap<String, Object> st = new HashMap<String, Object>();
      st.put("stat", s);
      st.put("statType", s.getStatType());
      return new ModelAndView(st, "stat.ftl");
    }
  }

  public class UpdateStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int id = GSON.fromJson(qm.value("databaseID"), Integer.class);
      String statID = qm.value("statID");
      int playerID = GSON.fromJson(qm.value("playerID"), Integer.class);
      double x = GSON.fromJson(qm.value("x"), Double.class);
      double y = GSON.fromJson(qm.value("y"), Double.class);

      System.out.println(
          "Updating: " + statID + " " + playerID + " " + x + " " + y);

      String errorMessage = "";
      try {
        dash.getGame().updateStat(id, statID, playerID, new Location(x, y));
      } catch (GameException ex) {
        errorMessage = ex.getMessage();
      } catch (Exception e) {
        errorMessage = e.getMessage();
      }

      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
    }
  }

  public class DeleteStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int id = GSON.fromJson(qm.value("databaseID"), Integer.class);

      String errorMessage = "";
      try {
        dash.getGame().deleteStat(id);
      } catch (GameException ex) {
        errorMessage = ex.getMessage();
      } catch (Exception e) {
        errorMessage = e.getMessage();
      }
      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
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

      String errorMessage = "";
      try {
        dash.getGame().takeTimeout(
            GSON.fromJson(request.queryMap().value("h"), Boolean.class));
      } catch (JsonSyntaxException | GameException e) {
        errorMessage = e.getMessage();
      };

      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
    }
  }

  public class SubHandler implements Route {
    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int inPlayerID = GSON.fromJson(qm.value("in"), Integer.class);
      int outPlayerID = GSON.fromJson(qm.value("out"), Integer.class);
      boolean home = Boolean.parseBoolean(qm.value("home"));
      String errorMessage = "";
      try {
        dash.getGame().subPlayer(inPlayerID, outPlayerID, home);
      } catch (ScoreboardException e) {
        errorMessage = e.getMessage();
      } catch (Exception e) {
        errorMessage = e.getMessage();
      }
      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
    }

  }

  public class EndGameHandler implements Route {
    @Override
    public Object handle(Request arg0, Response arg1) {
      dash.endGame();
      return true;
    }

  }

  public class AdvancePeriodHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      String errorMessage = "";
      try {
        if (dash.getGame() != null) {
          dash.getGame().incrementPeriod();
        } else {
          errorMessage = "No current game.";
          return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
        }
      } catch (GameException e) {
        return GSON.toJson(ImmutableMap.of("errorMessage", e.getMessage()));
      }

      return GSON.toJson(
          ImmutableMap.of(
              "errorMessage", errorMessage,
              "period", dash.getGame().getPeriod()));
    }

  }


}
