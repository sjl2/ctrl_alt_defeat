package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateViewRoute;

public class PlaymakerGUI {
  
  private Dashboard dash;
  
  public PlaymakerGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class PlaymakerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("title", "Ctrl Alt Defeat: Playmaker");
      return new ModelAndView(variables, "playmaker.ftl");
    }
  }


  public class SaveHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      
      return null;
    }
  }
}
