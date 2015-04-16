package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;

public class DashboardGUI {

  private Dashboard dash;

  public DashboardGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class DashboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("title", "Dashboard");
      return new ModelAndView(variables, "dashboard.ftl");
    }
  }

}
