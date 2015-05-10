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

/**
 * Class containing handlers involved with updating stats.
 *
 * @author ngoelz
 *
 */
public class StatsEntryGUI {

  private static final Gson GSON = new Gson();
  private Dashboard dash;

  /**Constructor for the stats entry gui, passes in the dash.
   *
   * @param dash dashboard of the user.
   */
  public StatsEntryGUI(Dashboard dash) {
    this.dash = dash;
  }

  /**
   * Returns all stats for the current game, used to maintain editablity of
   * stats when refreshing the page.
   *
   * @author ngoelz
   *
   */
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
      Map<String, Object> variables = ImmutableMap.of("tabTitle",
          "Stats Entry", "stats", s, "isGame", isGame, "errorMessage", "");

      return new ModelAndView(variables, "stats_entry.ftl");

    }

  }

  /**
   * Handler for adding a stat that has been logged on the front end.
   *
   * @author ngoelz
   *
   */
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
        HashMap<String, Object> t = new HashMap<String, Object>();
        return new ModelAndView(t, "stat.ftl");
      } catch (Exception e) {
        e.printStackTrace();
      }
      HashMap<String, Object> st = new HashMap<String, Object>();
      st.put("stat", s);
      st.put("statType", s.getStatType());
      return new ModelAndView(st, "stat.ftl");
    }
  }

  /**
   * Handler for updating a stat that has previously been logged.
   *
   * @author ngoelz
   *
   */
  public class UpdateStatHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int id = GSON.fromJson(qm.value("databaseID"), Integer.class);
      String statID = qm.value("statID");
      int playerID = GSON.fromJson(qm.value("playerID"), Integer.class);
      double x = GSON.fromJson(qm.value("x"), Double.class);
      double y = GSON.fromJson(qm.value("y"), Double.class);

      System.out.println("Updating: " + statID + " " + playerID + " " + x + " "
          + y);

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

  /**
   * Handler for deleting a stat that has been logged.
   *
   * @author ngoelz
   *
   */
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

  /**
   * Handler for changing the possession arrow of the game.
   *
   * @author ngoelz
   *
   */
  public class FlipPossessionHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      dash.getGame().flipPossession();

      return "";
    }
  }

  /**
   * Handler for calling a timeout.
   *
   * @author ngoelz
   *
   */
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
      }

      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage));
    }
  }

  /**
   * Handler for substituting players.
   *
   * @author ngoelz
   *
   */
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

  /**
   * Handler for ending the game.
   *
   * @author ngoelz
   *
   */
  public class EndGameHandler implements Route {
    @Override
    public Object handle(Request arg0, Response arg1) {
      dash.endGame();
      return true;
    }

  }

  /**
   * Handler for advancing the period.
   *
   * @author ngoelz
   *
   */
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

      return GSON.toJson(ImmutableMap.of("errorMessage", errorMessage,
          "period", dash.getGame().getPeriod()));
    }

  }

}
