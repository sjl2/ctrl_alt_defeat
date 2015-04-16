package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

public class StatGUI {

  private final static Gson GSON = new Gson();
  private Dashboard dash;
  
  public StatGUI(Dashboard dash) {
    this.dash = dash;
  }
  
  public class AddStat implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();

      int statID = GSON.fromJson(qm.value("stat"), Integer.class);
      int playerID = GSON.fromJson(qm.value("player"), Integer.class);
      int x = GSON.fromJson(qm.value("x"), Integer.class);
      int y = GSON.fromJson(qm.value("y"), Integer.class);

      int[] location = new int[] { x, y };

      dash.getGame().addStatByID(statID, playerID, location);

      return null;
    }

  }
}
