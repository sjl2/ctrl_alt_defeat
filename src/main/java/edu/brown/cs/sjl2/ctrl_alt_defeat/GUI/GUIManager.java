package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import spark.ExceptionHandler;
import spark.Filter;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;
import freemarker.template.Configuration;

public class GUIManager {

  private DBManager dbManager;
  private int port = 8585;
  private final static int STATUS = 500;

  private Dashboard dash;

  private DashboardGUI dashboardGUI;
  private GameGUI gameGUI;
  private StatsEntryGUI statsEntryGUI;
  private PlaymakerGUI playmakerGUI;
  private WikiGUI wikiGUI;
  private Trie trie;

  private static final Gson GSON = new Gson();

  public GUIManager(DBManager db) {
    this.dbManager = db;
    this.dash = new Dashboard(dbManager);
    this.trie = dbManager.getTrie();
    trie.whiteSpaceOn().prefixOn().editDistanceOn().setK(2);

    this.dashboardGUI = new DashboardGUI(dash, dbManager, trie);
    this.gameGUI = new GameGUI(dash);
    this.playmakerGUI = new PlaymakerGUI(dash, dbManager.getPlaymakerDB());
    this.statsEntryGUI = new StatsEntryGUI(dash);
    this.wikiGUI = new WikiGUI(dbManager, dash);
    runServer();
  }

  public GUIManager(DBManager db, int port) {
    this.dbManager = db;
    this.port = port;
    this.playmakerGUI = new PlaymakerGUI(dash, dbManager.getPlaymakerDB());
    this.statsEntryGUI = new StatsEntryGUI(dash);
    runServer();
  }

  private void runServer() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    /*** Setup Filters ***/
    /*Spark.before("/dashboard", new CoachFilter());
    Spark.before("/dashboard/*", new CoachFilter());
    Spark.before("/whiteboard", new CoachFilter());
    Spark.before("/whiteboard/*", new CoachFilter());
    Spark.before("/playmaker", new CoachFilter());
    Spark.before("/playmaker/*", new CoachFilter());

    Spark.before("/stats", new StatsFilter());
    Spark.before("/stats/*", new StatsFilter()); */

    
    // Setup Spark Routes
    Spark.get("/login", new LoginViewHandler(), freeMarker);
    Spark.post("/login/login", new LoginHandler());
    Spark.post("/login/logout", new LogoutHandler());

    /*** DashboardGUI routes ***/
    Spark.get("/dashboard", dashboardGUI.new DashboardHandler(), freeMarker);
    Spark.post("/dashboard/new", dashboardGUI.new DashSetupHandler(), freeMarker);
    Spark.get("/dashboard/new/team", dashboardGUI.new NewTeamHandler(), freeMarker);
    Spark.post("/dashboard/new/team/results", dashboardGUI.new NewTeamResultsHandler(), freeMarker);
    Spark.get("/dashboard/new/player", dashboardGUI.new NewPlayerHandler(), freeMarker);
    Spark.post("/dashboard/new/player/results", dashboardGUI.new NewPlayerResultsHandler(), freeMarker);
    Spark.post("/dashboard/edit/user", dashboardGUI.new EditUserHandler());
    Spark.get("/dashboard/get/users", dashboardGUI.new GetUsersHandler());
    Spark.get("/dashboard/new/game", dashboardGUI.new NewGameHandler(), freeMarker);
    Spark.get("/dashboard/getgame", dashboardGUI.new GetGameHandler());
    Spark.get("/dashboard/updategame", dashboardGUI.new UpdateGameHandler());
    Spark.get("/dashboard/opponent/get", dashboardGUI.new GetPlayersHandler(), freeMarker);
    Spark.post("/dashboard/autocomplete", dashboardGUI.new AutocompleteHandler());
    Spark.post("/dashboard/search", dashboardGUI.new SearchBarResultsHandler());

    /*** WikiGUI routes ***/
    Spark.get("/game/view/:id", wikiGUI.new GameViewHandler(), freeMarker);
    Spark.get("/player/view/:id", wikiGUI.new PlayerViewHandler(), freeMarker);
    Spark.post("/player/delete", wikiGUI.new DeletePlayer());
    Spark.get("/team/view/:id", wikiGUI.new TeamViewHandler(), freeMarker);
    Spark.post("/player/edit", wikiGUI.new EditPlayer());
    Spark.post("/team/edit", wikiGUI.new EditTeam());
    Spark.post("/season/get", wikiGUI.new GetGameStats(), freeMarker);

    Spark.post("/analytics/shotchart", wikiGUI.new GetAnalyticsShotChart());
    Spark.post("/analytics/heatmap", wikiGUI.new GetAnalyticsHeatMap());
    Spark.post("/analytics/ranking", wikiGUI.new GetLineupRanking());

    /* Rename these at some point */
    Spark.post("/shotchart", wikiGUI.new GetShotChartData());
    Spark.post("/heatmap", wikiGUI.new GetHeatMapData());
    Spark.get("/dashboard/analytics", dashboardGUI.new AnalyticsHandler(), freeMarker);

    /*** GameGUI routes ***/
    Spark.post("/game/start", gameGUI.new StartHandler());
    Spark.get("/game/roster", gameGUI.new StatPageHandler());

    /*** PlaymakerGUI routes ***/
		Spark.get("/playmaker", playmakerGUI.new PlaymakerHandler(), freeMarker);
    Spark.post("/playmaker/save", playmakerGUI.new SaveHandler());
    Spark.get("/playmaker/load", playmakerGUI.new LoadHandler());
    Spark.post("/playmaker/delete", playmakerGUI.new DeleteHandler());
    Spark.get("/playmaker/playNames", playmakerGUI.new PlayNamesHandler());
    Spark.get("/playmaker/getPlayerNumbers", playmakerGUI.new PlayerNumberHandler());
    Spark.get("/whiteboard", playmakerGUI.new WhiteboardHandler(), freeMarker);

    /*** Stats routes ***/
		Spark.get("/stats", statsEntryGUI.new StatsEntryHandler(), freeMarker);
		Spark.post("/stats/add", statsEntryGUI.new AddStatHandler(), freeMarker);
    Spark.post("/stats/update", statsEntryGUI.new UpdateStatHandler());
    Spark.post("/stats/delete", statsEntryGUI.new DeleteStatHandler());
		Spark.post("/stats/changepossession", statsEntryGUI.new FlipPossessionHandler());
		Spark.post("/stats/sub", statsEntryGUI.new SubHandler());
		Spark.post("/stats/timeout", statsEntryGUI.new TimeoutHandler());

		Spark.post("/stats/endgame", statsEntryGUI.new EndGameHandler());
		Spark.post("/stats/advanceperiod", statsEntryGUI.new AdvancePeriodHandler());

    //Spark.get("*", new PageNotFoundHandler(), freeMarker);
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
      config.setNumberFormat("0.######");
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private class CoachFilter implements Filter {

    @Override
    public void handle(Request req, Response res) {
      String clearanceString = req.session().attribute("clearance");

      if(clearanceString == null) {
        res.redirect("/login");
        return;
      }

      int clearance = Integer.parseInt(clearanceString);

      if(clearance == 1) {
        res.redirect("/stats");
      } else if(clearance < 1) {
        res.redirect("/login");
      }
    }

  }

  private class StatsFilter implements Filter {

    @Override
    public void handle(Request req, Response res) {
      String clearanceString = req.session().attribute("clearance");

      if(clearanceString == null) {
        res.redirect("/login");
        return;
      }

      int clearance = Integer.parseInt(clearanceString);

      if(clearance < 1) {
        res.redirect("/login");
      }
    }

  }

  /*private class PageNotFoundFilter implements Filter {

    @Override
    public void handle(Request req, Response res) {
      System.out.println(req.raw().getRequestURL());
    }
  }*/

  public class LoginViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Login", "errorMessage", "");
      return new ModelAndView(variables, "login.ftl");
    }
  }

  private class LoginHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("username");
      String password = qm.value("password");
      int clearance = dbManager.checkPassword(username, password);
      req.session().attribute("clearance", Integer.valueOf(clearance).toString());
      Map<String, Object> variables =
        ImmutableMap.of("clearance", clearance, "errorMessage", "");

      return GSON.toJson(variables);
    }
  }

  private class LogoutHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      req.session().attribute("clearance", 0);
      res.redirect("/login");
      return GSON.toJson(ImmutableMap.of("errorMessage", ""));
    }
  }

  /**
   * Handler for printing exceptions. Allows for easier debugging by having any
   * exceptions thrown while using the GUI to go right to browser and not just
   * be displayed on the command line.
   *
   * @author sjl2
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(STATUS);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
