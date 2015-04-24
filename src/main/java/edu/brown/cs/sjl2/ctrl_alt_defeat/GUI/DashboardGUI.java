package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.List;
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
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.OldGame;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class DashboardGUI {

  private final static Gson GSON = new Gson();
  private DBManager dbManager;
  private Dashboard dash;

  public DashboardGUI(Dashboard dash, DBManager dbManager) {
    this.dbManager = dbManager;
    this.dash = dash;
  }

  public class DashboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (dash.getMyTeam() == null) {
        Map<String, Object> variables =
            ImmutableMap.of("tabTitle", "Set-Up", "errorMessage", "");
        return new ModelAndView(variables, "dashboard_setup.ftl");
      }

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Dashboard",
                          "teams", dbManager.getTeams(),
                          "myTeam", dash.getMyTeam(),
                          "errorMessage", "");
      return new ModelAndView(variables, "dashboard.ftl");
    }
  }

  public class NewTeamHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team", "errorMessage", "");
      return new ModelAndView(variables, "newTeam.ftl");
    }

  }

  public class NewTeamResultsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      dash.addTeam(
          qm.value("name"),
          qm.value("coach"),
          qm.value("color1"),
          qm.value("color2"));

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team Results", "errorMessage", "");
      return new ModelAndView(variables, "newTeamResults.ftl");
    }
  }

  public class NewGameHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "New Game",
                        "players", dash.getMyTeam().getPlayers(),
                        "teams", dbManager.getTeams(),
                        "errorMessage", "");
      return new ModelAndView(variables, "newGame.ftl");
    }
  }

  public class DashboardSetupHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {

      QueryParamsMap qm = request.queryMap();

      String name = qm.value("name");
      String coach = qm.value("coach");
      String color1 = qm.value("color1");
      String color2 = qm.value("color2");

      dash.setMyTeam(name, coach, color1, color2);

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Dashboard Set-up", "errorMessage", "");
      return new ModelAndView(variables, "setup_complete.ftl");
    }
  }

  public class NewPlayerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Player",
                          "teams", dbManager.getTeams(),
                          "errorMessage", "");
      return new ModelAndView(variables, "newPlayer.ftl");
    }
  }

  public class NewPlayerResultsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      dbManager.savePlayer(
          qm.value("name"),
          Integer.parseInt(qm.value("team")),
          Integer.parseInt(qm.value("number")),
          Integer.parseInt(qm.value("current")));

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Player Results", "errorMessage", "");
      return new ModelAndView(variables, "newPlayerResults.ftl");
    }

  }

  public class GameViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int gameID = Integer.parseInt(request.params("id"));
      String error = "";

      OldGame game = null;
      try {
        game = dash.getOldGame(gameID);
      } catch (DashboardException e) {
        error = e.getMessage();
        System.out.println("there's an error and game isn't getting defined");
      }
      System.out.println(game.getHomeBoxScore().getTeamStats());
      Map<String, Object> variables =
        ImmutableMap.of(
            "tabTitle", game.toString(),
            "game", game,
            "errorMessage", error);
      return new ModelAndView(variables, "game.ftl");
    }

  }
  
  public class GetGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      if (dash.getGame() == null) {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
            .put("tabTitle", "Dashboard")
            .put("teams", dbManager.getTeams())
            .put("myTeam", dash.getMyTeam())
            .put("isGame", false)
            .put("game", "")
            .put("errorMessage", "").build();
        return GSON.toJson(variables);
      } else {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("isGame", true)
          .put("timeouts", dash.getGame().getRules().timeouts())
          .put("errorMessage", "").build();
        return GSON.toJson(variables);
      }
    }
    
  }

}
