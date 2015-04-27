package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.Collection;
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

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameView;
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
            ImmutableMap.of("tabTitle", "Set-Up",
                            "errorMessage", "");
        return new ModelAndView(variables, "dashboard_setup.ftl");
      }

      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Dashboard",
                          "teams", dbManager.getOpposingTeams(),
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
      dash.createTeam(
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
                        "teams", dash.getOpposingTeams(),
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

      dash.addMyTeam(name, coach, color1, color2);

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
                          "teams", dbManager.getAllTeams(),
                          "errorMessage", "");
      return new ModelAndView(variables, "newPlayer.ftl");
    }
  }

  public class NewPlayerResultsHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      dash.createPlayer(
          qm.value("name"),
          Integer.parseInt(qm.value("team")),
          Integer.parseInt(qm.value("number")),
          Boolean.parseBoolean(qm.value("current")));

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

      GameView game = null;
      try {
        game = dash.getOldGame(gameID);
      } catch (DashboardException e) {
        error = "No game exists with that id.";

        System.out.println(error);
      }

      Map<String, Object> variables =
        ImmutableMap.of(
            "tabTitle", game.toString(),
            "game", game,
            "errorMessage", error);
      return new ModelAndView(variables, "game.ftl");
    }

  }

  /* TODO THIS IS STILL COPY PASTED FROM GAMEVIEW HANDLER, ACTUALLY WRITE THIS! */
  public class TeamViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int gameID = Integer.parseInt(request.params("id"));
      String error = "";

      GameView game = null;
      try {
        game = dash.getOldGame(gameID);
      } catch (DashboardException e) {
        error = e.getMessage();
        System.out.println(error);
      }

      Map<String, Object> variables =
        ImmutableMap.of(
            "tabTitle", game.toString(),
            "game", game,
            "errorMessage", error);
      return new ModelAndView(variables, "game.ftl");
    }

  }

  public class PlayerViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int playerID = Integer.parseInt(request.params("id"));
      String error = "";

      Player player = dash.getPlayer(playerID);
      List<Integer> years = null;
      List<GameStats> rows = null;

      if (player == null) {
        error = "Could not find player by that ID!";
      } else {
        years = dbManager.getYearsActive("player_stats", playerID);
        rows = dbManager.getSeparateGameStatsForYear(years.get(0), "player_stats", playerID);

      }

      Map<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
          .put("tabTitle", player.toString())
          .put("db", dbManager)
          .put("player", player)
          .put("years", years)
          .put("rows", rows)
          .put("errorMessage", error).build();
      return new ModelAndView(variables, "player.ftl");
    }
  }

  public class GetGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      if (dash.getGame() == null) {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
            .put("tabTitle", "Dashboard")
            .put("isGame", false).build();
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

  public class UpdateGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      Game g = dash.getGame();
      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
        .put("homeScore", g.getHomeScore())
        .put("awayScore", g.getAwayScore())
        .put("possession", g.getPossession())
        .put("homeFouls", g.getHomeFouls())
        .put("awayFouls", g.getAwayFouls())
        .put("homeTimeouts", g.getTO(true))
        .put("awayTimeouts", g.getTO(false))
        .put("period", g.getPeriod())
        .put("homeBonus", g.getHomeBonus())
        .put("homeDoubleBonus", g.getHomeDoubleBonus())
        .put("awayBonus", g.getAwayBonus())
        .put("awayDoubleBonus", g.getAwayDoubleBonus())
        .put("errorMessage", "").build();

      return GSON.toJson(variables);

    }

  }

  public class GetPlayersHandler implements Route {

    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int teamID = Integer.parseInt(qm.value("teamID"));
      Team team = dash.getTeam(teamID);
      Collection<Player> players = team.getPlayers();
      Map<String, Object> variables =
        ImmutableMap.of("playerList", players);

      return GSON.toJson(variables);
    }

  }

}
