package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.Stat;

public class GameGUI {
  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public GameGUI(Dashboard dash) {
    this.dash = dash;
  }

  public class StPgHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      Game game = dash.getGame();
      if (game != null) {
        ArrayList<Object> rosterInfo = new ArrayList<Object>();
        rosterInfo.add(game.getHome().getPrimary());
        rosterInfo.add(game.getHome().getSecondary());
        rosterInfo.add(game.getAway().getPrimary());
        rosterInfo.add(game.getAway().getSecondary());
        rosterInfo.add(game.getLineup());
        rosterInfo.add(game.getBench(true));
        rosterInfo.add(game.getBench(false));

        List<Stat> st = game.getAllStats();
        List<String> lab = new ArrayList<String>();
        for (Stat s : st) {
          lab.add(s.getStatType());
        }
        Map<String, Object> toReturn =
            ImmutableMap.of(
                "roster", rosterInfo,
                "stats", st,
                "types", lab,
                "errorMessage", "");

        return GSON.toJson(toReturn);
      } else {
        return null;
      }
    }

  }

  public class StartHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int teamID = Integer.parseInt(qm.value("opponent"));
      boolean isHome = GSON.fromJson(qm.value("isHome"), Boolean.class);

      Map<BasketballPosition, Integer> starterIDs = new EnumMap<>(BasketballPosition.class);

      starterIDs.put(BasketballPosition.HomePG, Integer.parseInt(qm.value("hpg")));
      starterIDs.put(BasketballPosition.HomeSG, Integer.parseInt(qm.value("hsg")));
      starterIDs.put(BasketballPosition.HomeSF, Integer.parseInt(qm.value("hsf")));
      starterIDs.put(BasketballPosition.HomePF, Integer.parseInt(qm.value("hpf")));
      starterIDs.put(BasketballPosition.HomeC, Integer.parseInt(qm.value("hc")));
      starterIDs.put(BasketballPosition.AwayPG, Integer.parseInt(qm.value("apg")));
      starterIDs.put(BasketballPosition.AwaySG, Integer.parseInt(qm.value("asg")));
      starterIDs.put(BasketballPosition.AwaySF, Integer.parseInt(qm.value("asf")));
      starterIDs.put(BasketballPosition.AwayPF, Integer.parseInt(qm.value("apf")));
      starterIDs.put(BasketballPosition.AwayC, Integer.parseInt(qm.value("ac")));

      try {
        dash.startGame(isHome, teamID, starterIDs);
      } catch (DashboardException e) {
        return e.getMessage();
      }

      return "";

    }
  }

}
