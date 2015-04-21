package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;

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
            ImmutableMap.of("tabTitle", "Set-Up");
        return new ModelAndView(variables, "dashboard_setup.ftl");
      }

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Dashboard",
                          "teams", dbManager.getTeams(),
                          "myTeam", dash.getMyTeam());
      return new ModelAndView(variables, "dashboard.ftl");
    }
  }

  public class NewTeamHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team");
      return new ModelAndView(variables, "newTeam.ftl");
    }

  }

  public class NewTeamResultsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      Team t = new Team(
          dbManager.getNextID("team"),
          qm.value("name"),
          qm.value("coach"),
          qm.value("color1"),
          qm.value("color2"),
          null);
      dbManager.saveTeam(t, false);

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team Results");
      return new ModelAndView(variables, "newTeamResults.ftl");
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
          ImmutableMap.of("tabTitle", "Dashboard Set-up");
      return new ModelAndView(variables, "setup_complete.ftl");
    }
  }

  public class NewPlayerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Player",
                          "teams", dbManager.getTeams());
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
          ImmutableMap.of("tabTitle", "New Player Results");
      return new ModelAndView(variables, "newPlayerResults.ftl");
    }

  }

}
