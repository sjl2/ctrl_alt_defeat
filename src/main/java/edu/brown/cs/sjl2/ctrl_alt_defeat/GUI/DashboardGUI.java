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
      dash.saveTeam(qm.value("name"), qm.value("color1"), qm.value("color2"));
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team Results");
      return new ModelAndView(variables, "newTeamResults.ftl");
    }
  }

}
