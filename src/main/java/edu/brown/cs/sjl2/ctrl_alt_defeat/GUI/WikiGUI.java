package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Arrays;
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
import edu.brown.cs.sjl2.ctrl_alt_defeat.GameView;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Location;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.stats.GameStats;

public class WikiGUI {

  private final static Gson GSON = new Gson();
  private DBManager dbManager;
  private Dashboard dashboard;

  /**
   * Constructor for class.
   *
   * @param dbManager
   *          - DBManager, used to query database
   * @author awainger
   */
  public WikiGUI(DBManager dbManager, Dashboard dashboard) {
    this.dbManager = dbManager;
    this.dashboard = dashboard;
  }

  /**
   * Loads all data needed to view a game page.
   *
   * @author awainger
   */
  public class GameViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int gameID = Integer.parseInt(request.params("id"));
      String error = "";

      GameView game = null;
      try {
        game = dashboard.getOldGame(gameID);
      } catch (DashboardException e) {
        error = "No game exists with that id.";
      }

      if(game != null) {
        Map<String, Object> variables =
          ImmutableMap.of("tabTitle", game.toString(),
                          "allTeams", dbManager.getAllTeams(),
                          "game", game,
                          "errorMessage", error);

        return new ModelAndView(variables, "game.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("tabTitle", "Page Not Found", "errorMessage", "Game doesn't exist");

        return new ModelAndView(variables, "404.ftl");
      }
    }
  }

  /**
   * Loads all data needed to view a team page.
   *
   * @author awainger
   */
  public class TeamViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int teamID = -1;
      Team team = null;
      List<Integer> years = new ArrayList<>();
      List<GameStats> rows = new ArrayList<>();
      List<GameStats> seasonAverages = new ArrayList<>();
      List<GameStats> seasonTotals = new ArrayList<>();
      String error = "";

      try {
        teamID = Integer.parseInt(request.params("id"));
        team = dashboard.getTeam(teamID);
        if (team == null) {
          error = "Could not find team by that ID!";
        } else {
          years = dbManager.getYearsActive("team_stats", teamID);
          if (!years.isEmpty()) {
            rows = dbManager.getSeparateGameStatsForYear(years.get(0), "team_stats", teamID);
            seasonAverages = dbManager.getAggregateGameStats("AVG", "team_stats", teamID);
            seasonTotals = dbManager.getAggregateGameStats("SUM", "team_stats", teamID);
          }
        }
      } catch (NumberFormatException e) {
        error = "That's not a valid team id!";
      }

      int clearance = 0;
      String clearanceString = request.session().attribute("clearance");
      if(clearanceString != null) {
        clearance = Integer.parseInt(clearanceString);
      }

      if(team != null) {
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("tabTitle", team.toString()).put("db", dbManager)
          .put("team", team).put("years", years).put("rows", rows)
          .put("allTeams", dbManager.getAllTeams())
          .put("seasonTotals", seasonTotals)
          .put("seasonAverages", seasonAverages).put("errorMessage", error)
          .put("clearance", clearance)
          .build();
        return new ModelAndView(variables, "team.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("tabTitle", "Page Not Found", "errorMessage", "Game doesn't exist");

        return new ModelAndView(variables, "404.ftl");
      }
    }
  }

  /**
   * Handler for editing information about a player.
   * @author awainger
   */
  public class EditPlayer implements Route {

    @Override
    public Object handle(Request request, Response response) {
      try {
        QueryParamsMap qm = request.queryMap();
        int id = Integer.parseInt(qm.value("id"));
        String name = qm.value("name");
        int number = Integer.parseInt(qm.value("number"));
        int teamID = Integer.parseInt(qm.value("teamID"));
        boolean current = Boolean.parseBoolean(qm.value("current"));
        dbManager.updatePlayer(id, name, teamID, number, current);

        return GSON.toJson(ImmutableMap.of("errorMessage", ""));
      } catch (NumberFormatException e) {
        return GSON.toJson(ImmutableMap.of("errorMessage", "Error parsing changes to player."));
      }
    }
  }

  public class DeletePlayer implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      int id = Integer.parseInt(qm.value("id"));

      return GSON.toJson(ImmutableMap.of("success", dbManager.deletePlayer(id)));
    }
  }

  /**
   * Handler for editing information about a team.
   * @author awainger
   */
  public class EditTeam implements Route {

    @Override
    public Object handle(Request request, Response response) {
      try {
        QueryParamsMap qm = request.queryMap();
        int id = Integer.parseInt(qm.value("id"));
        String name = qm.value("name");
        String coach = qm.value("coach");
        String primary = qm.value("primary");
        String secondary = qm.value("secondary");

        dbManager.updateTeam(id, name, coach, primary, secondary);
        if (id == dashboard.getMyTeam().getID()) {
          dashboard.setMyTeam(dbManager.getMyTeam());
        }
        return ImmutableMap.of("errorMessage", "");
      } catch (NumberFormatException e) {
        return ImmutableMap.of("errorMessage", "Error parsing changes to team.");
      } catch (DashboardException e) {
        return ImmutableMap.of("errorMessage", "Stop messing with your team");
      }
    }
  }

  /**
   * Loads all data needed to view a player page.
   *
   * @author awainger
   */
  public class PlayerViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      int playerID = -1;
      String error = "";
      Player player = null;
      List<Integer> years = null;
      List<GameStats> rows = null;
      List<GameStats> seasonAverages = null;
      List<GameStats> seasonTotals = null;

      try {
        playerID = Integer.parseInt(request.params("id"));
        player = dashboard.getPlayer(playerID);
        if (player == null) {
          error = "Could not find player by that ID!";
        } else {
          years = dbManager.getYearsActive("player_stats", playerID);
          if(!years.isEmpty()) {
            rows = dbManager.getSeparateGameStatsForYear(years.get(0), "player_stats", playerID);
          } else {
            rows = new ArrayList<>();
          }
          seasonAverages = dbManager.getAggregateGameStats("AVG", "player_stats", playerID);
          seasonTotals = dbManager.getAggregateGameStats("SUM", "player_stats", playerID);
        }
      } catch (NumberFormatException e) {
        error = "That's not a valid player id!";
      }

      int clearance = 0;
      String clearanceString = request.session().attribute("clearance");
      if(clearanceString != null) {
        clearance = Integer.parseInt(clearanceString);
      }

      if(player != null) {      
        Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("tabTitle", player.toString()).put("db", dbManager)
          .put("player", player).put("years", years).put("rows", rows)
          .put("seasonTotals", seasonTotals)
          .put("allTeams", dbManager.getAllTeams())
          .put("seasonAverages", seasonAverages).put("errorMessage", error)
          .put("clearance", clearance)
          .build();
        return new ModelAndView(variables, "player.ftl");
      } else {
        Map<String, Object> variables = ImmutableMap.of("tabTitle", "Page Not Found", "errorMessage", "Player doesn't exist");

        return new ModelAndView(variables, "404.ftl");
      }
    }
  }

  /**
   * Handler for updating the season table on either player or team pages.
   *
   * @author awainger
   */
  public class GetGameStats implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String error = "";
      List<GameStats> rows = null;

      boolean isPlayer = false;
      try {
        int year = Integer.parseInt(qm.value("year"));
        int id = Integer.parseInt(qm.value("id"));
        isPlayer = Boolean.parseBoolean(qm.value("isPlayer"));
        String table;
        if (isPlayer) {
          table = "player_stats";
        } else {
          table = "team_stats";
        }
        rows = dbManager.getSeparateGameStatsForYear(year, table, id);
      } catch (NumberFormatException e) {
        error = "That's either an invalid year or ID!";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("db", dbManager).put("rows", rows).put("errorMessage", error)
          .put("isPlayer", isPlayer).build();

      return new ModelAndView(variables, "season.ftl");
    }
  }

  /**
   * Handler for retrieving shot chart data.
   *
   * @author awainger
   */
  public class GetShotChartData implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      boolean player;
      boolean currentGame;
      List<Location> makes = null;
      List<Location> misses = null;
      String errorMessage = "";
      try {
        player = Boolean.parseBoolean(qm.value("player"));
        currentGame = Boolean.parseBoolean(qm.value("currentGame"));
        if (currentGame) {
          if (player) {
            int playerID = Integer.parseInt(qm.value("id"));
            makes = dbManager.getMakesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(playerID), "player");
            misses = dbManager.getMissesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(playerID), "player");
          } else {
            boolean us = Boolean.parseBoolean(qm.value("us"));
            int teamID;
            if ((us && dashboard.getGame().getHomeGame()) || (!us && !dashboard.getGame().getHomeGame())) {
              teamID = dashboard.getGame().getHome().getID();
              makes = dbManager.getMakesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(teamID), "team");
              misses = dbManager.getMissesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(teamID), "team");
            } else {
              teamID = dashboard.getGame().getAway().getID();
              makes = dbManager.getMakesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(teamID), "team");
              misses = dbManager.getMissesForEntityInGames(Arrays.asList(dashboard.getGame().getID()), Arrays.asList(teamID), "team");
            }
          }
        } else {
          if (player) {
            int playerID = Integer.parseInt(qm.value("id"));
            int gameID = Integer.parseInt(qm.value("gameID"));
            makes = dbManager.getMakesForEntityInGames(Arrays.asList(gameID), Arrays.asList(playerID), "player");
            misses = dbManager.getMissesForEntityInGames(Arrays.asList(gameID), Arrays.asList(playerID), "player");
          } else {
            int teamID = Integer.parseInt(qm.value("id"));
            int gameID = Integer.parseInt(qm.value("gameID"));
            makes = dbManager.getMakesForEntityInGames(Arrays.asList(gameID), Arrays.asList(teamID), "team");
            misses = dbManager.getMissesForEntityInGames(Arrays.asList(gameID), Arrays.asList(teamID), "team");
          }
        }
      } catch (NumberFormatException e) {
        errorMessage = "Invalid id!";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("makes", makes).put("misses", misses)
          .put("errorMessage", errorMessage).build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for retrieving heat map data.
   *
   * @author awainger
   */
  public class GetHeatMapData implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      boolean player;
      int entityID;
      String type;
      int championshipYear;
      List<Location> makes = null;
      List<Location> misses = null;
      String errorMessage = "";
      try {
        player = Boolean.parseBoolean(qm.value("player"));
        entityID = Integer.parseInt(qm.value("id"));
        championshipYear = Integer.parseInt(qm.value("championshipYear"));

        if (player) {
          type = "player";
        } else {
          type = "team";
        }

        makes = dbManager.getMakesForYear(championshipYear, Arrays.asList(entityID), type);
        misses = dbManager.getMissesForYear(championshipYear, Arrays.asList(entityID), type);
      } catch (NumberFormatException e) {
        errorMessage = "Invalid id format!";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("makes", makes).put("misses", misses).put("errorMessage", errorMessage)
          .build();
      return GSON.toJson(variables);

    }
  }

  public class GetAnalyticsHeatMap implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      List<Location> makes = null;
      List<Location> misses = null;
      String errorMessage = "";
      try {
        String playerIDsString = qm.value("ids");
        Integer[] playerIDArray = GSON.fromJson(playerIDsString, Integer[].class);
        List<Integer> ids = Arrays.asList(playerIDArray);
        makes = dbManager.getMakesForYear(dbManager.getChampionshipYear(LocalDate.now()), ids, "player");
        misses = dbManager.getMissesForYear(dbManager.getChampionshipYear(LocalDate.now()), ids, "player");
      } catch (NumberFormatException e) {
        errorMessage = "Invalid id format!";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("makes", makes).put("misses", misses).put("errorMessage", errorMessage)
          .build();
      return GSON.toJson(variables);
    }
  }

  public class GetAnalyticsShotChart implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      List<Location> makes = null;
      List<Location> misses = null;
      String errorMessage = "";
      try {
        String playerIDsString = qm.value("ids");
        Integer[] playerIDArray = GSON.fromJson(playerIDsString, Integer[].class);
        List<Integer> ids = Arrays.asList(playerIDArray);
        List<Integer> last5Games = dbManager.getLast5GameIDs();
        makes = dbManager.getMakesForEntityInGames(last5Games, ids, "player");
        misses = dbManager.getMissesForEntityInGames(last5Games, ids, "player");
      } catch (NumberFormatException e) {
        errorMessage = "Invalid id format!";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("makes", makes).put("misses", misses).put("errorMessage", errorMessage)
          .build();
      return GSON.toJson(variables);
    }
  }

  public class GetLineupRanking implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      float ranking = 0;
      String errorMessage = "";
      try {
        String playerIDsString = qm.value("ids");
        Integer[] playerIDArray = GSON.fromJson(playerIDsString, Integer[].class);
        List<Integer> ids = Arrays.asList(playerIDArray);
        ranking = (float) dbManager.lineupRanking(ids);
      } catch (NumberFormatException e) {
        errorMessage = "Error calculating lineup ranking.";
      }

      Map<String, Object> variables = new ImmutableMap.Builder<String, Object>()
          .put("ranking", ranking).put("errorMessage", errorMessage)
          .build();
      return GSON.toJson(variables);
    }

  }
}
