package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.BasketballPosition;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Player;
import edu.brown.cs.sjl2.ctrl_alt_defeat.basketball.Team;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.StringFormatter;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;


/**
 * Houses all handlers that deal with dashboard-related events.
 * @author awainger
 */
public class DashboardGUI {

  private final static Gson GSON = new Gson();
  private DBManager db;
  private Dashboard dash;
  private Trie trie;

  /**
   * Constructor for dashboardgui class.
   * @param dash - Dashboard, a reference.
   * @param dbManager - DBManager, to retrieve data from the database.
   * @param trie - Used for autocorrecting the coach's search bar.
   */
  public DashboardGUI(Dashboard dash, DBManager dbManager, Trie trie) {
    this.db = dbManager;
    this.dash = dash;
    this.trie = trie;
  }

  /**
   * Loads dashboard setup page if myTeam has not been set, otherwise
   * loads regular dashboard page.
   * @author awainger
   */
  public class DashboardHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      if (dash.getMyTeam() == null) {
        Map<String, Object> variables =
            ImmutableMap.of("tabTitle", "Set-Up",
                            "errorMessage", "");
        return new ModelAndView(variables, "dashboard_setup.ftl");
      }

      Team opponent = db.getTeam(db.getOpposingTeams().get(0).getID());
      Map<String, Object> variables =
          new ImmutableMap.Builder<String, Object>()
          .put("tabTitle", "Dashboard")
          .put("myTeam", dash.getMyTeam())
          .put("players", dash.getMyTeam().getPlayers())
          .put("teams", db.getOpposingTeams())
          .put("opposingPlayers", opponent.getPlayers())
          .put("isGame", dash.getGame() != null)
          .put("errorMessage", "").build();

      if (dash.getGame() == null) {
        return new ModelAndView(variables, "dashboard_no_game.ftl");
      }

      return new ModelAndView(variables, "dashboard_game.ftl");
    }
  }

  /**
   * Loads new team creation page.
   * @author awainger
   */
  public class NewTeamHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Team", "errorMessage", "");
      return new ModelAndView(variables, "newTeam.ftl");
    }
  }

  /**
   * Loads results page after creating a new team.
   * @author awainger
   */
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

  /**
   * Handler for starting a new game.
   * @author awainger
   */
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

  /**
   * Set up handler for the dashboard.
   * @author awainger
   */
  public class DashSetupHandler implements TemplateViewRoute {
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

  /**
   * Loads form for creating new player.
   * @author awainger
   */
  public class NewPlayerHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request request, Response response) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "New Player",
                          "teams", db.getAllTeams(),
                          "errorMessage", "");
      return new ModelAndView(variables, "newPlayer.ftl");
    }
  }

  /**
   * Loads results page for creating a new player.
   * @author awainger
   */
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

  /**
   * Handler for populating the scoreboard on the dashboard.
   * @author awainger
   */
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
          .put("timeouts", dash.getGame().getRules().getTimeOuts())
          .put("errorMessage", "").build();
        return GSON.toJson(variables);
      }
    }
  }

  /**
   * Gets latest game information to display on dashboard.
   * @author awainger
   */
  public class UpdateGameHandler implements Route {

    @Override
    public Object handle(Request arg0, Response arg1) {
      if (dash.getGame() == null) {
        return GSON.toJson(ImmutableMap.of("isGame", false));
      }
      Game g = dash.getGame();
      ImmutableMap.Builder<String, Object> builder =
          new ImmutableMap.Builder<String, Object>()
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
        .put("errorMessage", "");
        if (g.getHomeGame()) {
          builder.put("pgStats", g.getHomeBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.HomePG)));
          builder.put("sgStats", g.getHomeBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.HomeSG)));
          builder.put("sfStats", g.getHomeBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.HomeSF)));
          builder.put("pfStats", g.getHomeBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.HomePF)));
          builder.put("cStats", g.getHomeBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.HomeC)));
          builder.put("ourFGMade", g.getHomeBoxScore().getTeamStats().getFieldGoals());
          builder.put("ourFGAttempted", g.getHomeBoxScore().getTeamStats().getFieldGoalsA());
          builder.put("our3ptMade", g.getHomeBoxScore().getTeamStats().getThreePointers());
          builder.put("our3ptAttempted", g.getHomeBoxScore().getTeamStats().getThreePointersA());
          builder.put("ourFTMade", g.getHomeBoxScore().getTeamStats().getFreeThrows());
          builder.put("ourFTAttempted", g.getHomeBoxScore().getTeamStats().getFreeThrowsA());
          builder.put("ourSteals", g.getHomeBoxScore().getTeamStats().getSteals());
          builder.put("ourBlocks", g.getHomeBoxScore().getTeamStats().getBlocks());
          builder.put("ourRebounds", g.getHomeBoxScore().getTeamStats().getRebounds());
          builder.put("ourAssists", g.getHomeBoxScore().getTeamStats().getAssists());
          builder.put("ourTurnovers", g.getHomeBoxScore().getTeamStats().getTurnovers());
          builder.put("theirFGMade", g.getAwayBoxScore().getTeamStats().getFieldGoals());
          builder.put("theirFGAttempted", g.getAwayBoxScore().getTeamStats().getFieldGoalsA());
          builder.put("their3ptMade", g.getAwayBoxScore().getTeamStats().getThreePointers());
          builder.put("their3ptAttempted", g.getAwayBoxScore().getTeamStats().getThreePointersA());
          builder.put("theirFTMade", g.getAwayBoxScore().getTeamStats().getFreeThrows());
          builder.put("theirFTAttempted", g.getAwayBoxScore().getTeamStats().getFreeThrowsA());
          builder.put("theirSteals", g.getAwayBoxScore().getTeamStats().getSteals());
          builder.put("theirBlocks", g.getAwayBoxScore().getTeamStats().getBlocks());
          builder.put("theirRebounds", g.getAwayBoxScore().getTeamStats().getRebounds());
          builder.put("theirAssists", g.getAwayBoxScore().getTeamStats().getAssists());
          builder.put("theirTurnovers", g.getAwayBoxScore().getTeamStats().getTurnovers());
          builder.put("isGame", true);
        } else {
          builder.put("pgStats", g.getAwayBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.AwayPG)));
          builder.put("sgStats", g.getAwayBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.AwaySG)));
          builder.put("sfStats", g.getAwayBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.AwaySF)));
          builder.put("pfStats", g.getAwayBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.AwayPF)));
          builder.put("cStats", g.getAwayBoxScore().getPlayerStats(
              g.getLineup().getPlayers().get(BasketballPosition.AwayC)));
          builder.put("ourFGMade", g.getAwayBoxScore().getTeamStats().getFieldGoals());
          builder.put("ourFGAttempted", g.getAwayBoxScore().getTeamStats().getFieldGoalsA());
          builder.put("our3ptMade", g.getAwayBoxScore().getTeamStats().getThreePointers());
          builder.put("our3ptAttempted", g.getAwayBoxScore().getTeamStats().getThreePointersA());
          builder.put("ourFTMade", g.getAwayBoxScore().getTeamStats().getFreeThrows());
          builder.put("ourFTAttempted", g.getAwayBoxScore().getTeamStats().getFreeThrowsA());
          builder.put("ourSteals", g.getAwayBoxScore().getTeamStats().getSteals());
          builder.put("ourBlocks", g.getAwayBoxScore().getTeamStats().getBlocks());
          builder.put("ourRebounds", g.getAwayBoxScore().getTeamStats().getRebounds());
          builder.put("ourAssists", g.getAwayBoxScore().getTeamStats().getAssists());
          builder.put("ourTurnovers", g.getAwayBoxScore().getTeamStats().getTurnovers());
          builder.put("theirFGMade", g.getHomeBoxScore().getTeamStats().getFieldGoals());
          builder.put("theirFGAttempted", g.getHomeBoxScore().getTeamStats().getFieldGoalsA());
          builder.put("their3ptMade", g.getHomeBoxScore().getTeamStats().getThreePointers());
          builder.put("their3ptAttempted", g.getHomeBoxScore().getTeamStats().getThreePointersA());
          builder.put("theirFTMade", g.getHomeBoxScore().getTeamStats().getFreeThrows());
          builder.put("theirFTAttempted", g.getHomeBoxScore().getTeamStats().getFreeThrowsA());
          builder.put("theirSteals", g.getHomeBoxScore().getTeamStats().getSteals());
          builder.put("theirBlocks", g.getHomeBoxScore().getTeamStats().getBlocks());
          builder.put("theirRebounds", g.getHomeBoxScore().getTeamStats().getRebounds());
          builder.put("theirAssists", g.getHomeBoxScore().getTeamStats().getAssists());
          builder.put("theirTurnovers", g.getHomeBoxScore().getTeamStats().getTurnovers());
          builder.put("isGame", true);
        }
        Map<String, Object> variables = builder.build();
      return GSON.toJson(variables);
    }
  }

  /**
   * Used to populate create game select list with player names.
   * @author awainger
   */
  public class GetPlayersHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      int teamID = Integer.parseInt(qm.value("teamID"));
      Team team = dash.getTeam(teamID);
      Collection<Player> players = team.getPlayers();
      Map<String, Object> variables =
        ImmutableMap.of("players", players);

      return new ModelAndView(variables, "opponent_lineup.ftl");
    }

  }

  public class AnalyticsHandler implements TemplateViewRoute {

    @Override
    public ModelAndView handle(Request arg0, Response arg1) {
      Map<String, Object> variables =
          ImmutableMap.of("tabTitle", "Analytics",
                          "players", dash.getMyTeam().getPlayers(),
                          "errorMessage", "");
      return new ModelAndView(variables, "analytics.ftl");
    }

  }

  /**
   * Generates and ranks autocorrection suggestions for search bar.
   * @author awainger
   */
  public class AutocompleteHandler implements Route {

    @Override
    public Object handle(Request req, Response arg1) {

      QueryParamsMap qm = req.queryMap();
      String[] resOneString = new String[5];

      ArrayList<String> resOne;
      String start = qm.value("spot");
      if (start.length() != 0) {
        resOne = trie.evaluateWord(StringFormatter.treat(start.toLowerCase()), null);
      } else {
        resOne = new ArrayList<String>();
      }
      for (int i = 0; i < resOne.size(); i++) {
        resOneString[i] = resOne.get(i);
      }
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("title", "Updated");
      variables.put("res", resOneString);

      return GSON.toJson(variables);
    }
  }

  /**
   * Takes search bar results, returns list of teams or players
   * that match the search term.
   * @author awainger
   */
  public class SearchBarResultsHandler implements Route {

    @Override
    public Object handle(Request request, Response response) {
      QueryParamsMap qm = request.queryMap();
      String searchString = qm.value("searchString");
      Boolean isPlayer = Boolean.parseBoolean(qm.value("isPlayer"));
      System.out.println(isPlayer);
      List<Integer> ids = db.searchBarResults(searchString, isPlayer);

      if (ids.isEmpty()) {
        return GSON.toJson(new ImmutableMap.Builder<String, Object>()
            .put("errorMessage", "Sorry, no players or teams matched your search.")
            .build());
      } else {
        if (isPlayer) {
          List<Player> players = new ArrayList<>();
          for (int id : ids) {
            players.add(db.getPlayer(id));
          }
          return GSON.toJson(new ImmutableMap.Builder<String, Object>()
              .put("errorMessage", "")
              .put("list", players)
              .build());
        } else {
          List<Team> teams = new ArrayList<>();
          for (int id : ids) {
            teams.add(db.getTeam(id));
          }
          return GSON.toJson(new ImmutableMap.Builder<String, Object>()
              .put("errorMessage", "")
              .put("list", teams)
              .build());
        }
      }
    }
  }
}
