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

/**
 * Container for all handlers directly interacting with the game state.
 *
 * @author ngoelz
 *
 */
public class GameGUI {
  private final static Gson GSON = new Gson();
  private Dashboard dash;

  public GameGUI(Dashboard dash) {
    this.dash = dash;
  }

  /**
   * Handler that provides information for the initial setup of the stats entry
   * interface.
   *
   * @author ngoelz
   *
   */
  public class StatPageHandler implements Route {

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
        rosterInfo.add(game.getHome().getName());
        rosterInfo.add(game.getAway().getName());

        List<Stat> st = game.getAllStats();
        List<String> lab = new ArrayList<String>();
        for (Stat s : st) {
          lab.add(s.getStatType());
        }
        Map<String, Object> toReturn =
            ImmutableMap.of(
                "roster", rosterInfo,
                "types", lab,
                "errorMessage", "");

        return GSON.toJson(toReturn);
      } else {
        Map<String, Object> variables =
            ImmutableMap.of("errorMessage", "No Game");

        return GSON.toJson(variables);
      }
    }
  }

  /**
   * Handler for providing information for starting a game on the front end.
   *
   * @author ngoelz
   *
   */
  public class StartHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();

      try {

        boolean isHome = Boolean.parseBoolean(qm.value("isHome"));
        int teamID = Integer.parseInt(qm.value("opponent"));

        Map<BasketballPosition, Integer> starterIDs =
            new EnumMap<>(BasketballPosition.class);

        starterIDs.put(BasketballPosition.HomePG,
            Integer.parseInt(qm.value("hpg")));
        starterIDs.put(BasketballPosition.HomeSG,
            Integer.parseInt(qm.value("hsg")));
        starterIDs.put(BasketballPosition.HomeSF,
            Integer.parseInt(qm.value("hsf")));
        starterIDs.put(BasketballPosition.HomePF,
            Integer.parseInt(qm.value("hpf")));
        starterIDs.put(BasketballPosition.HomeC,
            Integer.parseInt(qm.value("hc")));
        starterIDs.put(BasketballPosition.AwayPG,
            Integer.parseInt(qm.value("apg")));
        starterIDs.put(BasketballPosition.AwaySG,
            Integer.parseInt(qm.value("asg")));
        starterIDs.put(BasketballPosition.AwaySF,
            Integer.parseInt(qm.value("asf")));
        starterIDs.put(BasketballPosition.AwayPF,
            Integer.parseInt(qm.value("apf")));
        starterIDs.put(BasketballPosition.AwayC,
            Integer.parseInt(qm.value("ac")));

        dash.startGame(isHome, teamID, starterIDs);
      } catch (DashboardException e) {
        return e.getMessage();
      } catch (Exception e) {
        return "ERROR: " + e.getMessage() + e.getStackTrace();
      }

      return "";

    }
  }

}
