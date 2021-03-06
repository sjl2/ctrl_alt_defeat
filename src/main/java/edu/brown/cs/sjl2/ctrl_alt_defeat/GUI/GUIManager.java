package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;
import freemarker.template.Configuration;

/**
 * A class that contains all Spark routing that calls the appropriate manager.
 * Also contains information for Spark such as port and session management.
 *
 * @author ngoelz
 *
 */
public class GUIManager {

  private DBManager db;
  private int port = 0;
  private static final int DEFAULT_PORT = 8585;
  private static final int STATUS = 500;

  private Dashboard dash;

  private DashboardGUI dashboardGUI;
  private GameGUI gameGUI;
  private StatsEntryGUI statsEntryGUI;
  private PlaymakerGUI playmakerGUI;
  private WikiGUI wikiGUI;
  private Trie trie;

  private static final Gson GSON = new Gson();

  /**
   * Initializes the Class.
   *
   * @param db
   *          The database manager to be assigned.
   */
  public GUIManager(DBManager db) {
    this.port = DEFAULT_PORT;
    initializeGUIManager(db);
  }

  /**
   * Initializes the class.
   *
   * @param db
   *          the database manager.
   * @param port
   *          the port to be assigned.
   */
  public GUIManager(DBManager db, int port) {
    this.port = port;
    initializeGUIManager(db);
  }

  /**
   * Sets the internals of the class. Fills the trie and sets its parameters,
   * initializes the various helper classes containing the handlers, and runs
   * the server.
   *
   * @param database
   *          The DBManager to be assigned
   */
  private void initializeGUIManager(DBManager database) {
    this.db = database;
    this.dash = new Dashboard(database);
    this.trie = database.fillTrie();
    trie.whiteSpaceOn().prefixOn().editDistanceOn().setK(2);

    this.dashboardGUI = new DashboardGUI(dash, database, trie);
    this.gameGUI = new GameGUI(dash);
    this.playmakerGUI =
        new PlaymakerGUI(dash, database, database.getPlaymakerDB());
    this.statsEntryGUI = new StatsEntryGUI(dash);
    this.wikiGUI = new WikiGUI(database, dash);
    runServer();
  }

  /**
   * Runs the server on the selected port. There are a lot of routes here.
   */
  private void runServer() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    /*** Setup Filters ***/
    Spark.before("/dashboard", new CoachFilter());
    Spark.before("/dashboard/*", new CoachFilter());
    Spark.before("/whiteboard", new CoachFilter());
    Spark.before("/whiteboard/*", new CoachFilter());
    Spark.before("/playmaker", new CoachFilter());
    Spark.before("/playmaker/*", new CoachFilter());
    Spark.before("/users/", new CoachFilter());
    Spark.before("/users/*", new CoachFilter());
    Spark.after("/dashboard/*", new GameCheckFilter());
    Spark.before("/stats", new StatsFilter());
    Spark.before("/stats/*", new StatsFilter());

    // Setup Spark Routes
    Spark.get("/login", new LoginViewHandler(), freeMarker);
    Spark.post("/login/login", new LoginHandler());
    Spark.post("/login/logout", new LogoutHandler());

    /*** DashboardGUI routes ***/
    Spark.get("/dashboard", dashboardGUI.new DashboardHandler(), freeMarker);
    Spark.post("/dashboard/new", dashboardGUI.new DashSetupHandler(),
        freeMarker);
    Spark.post("/dashboard/new/team", dashboardGUI.new NewTeamHandler());
    Spark.post("/dashboard/new/player", dashboardGUI.new NewPlayerHandler());
    Spark.post("/user/edit", dashboardGUI.new EditUserHandler());
    Spark.get("/users/get", dashboardGUI.new GetUsersHandler());
    Spark.get("/dashboard/new/game", dashboardGUI.new NewGameHandler(),
        freeMarker);
    Spark.get("/dashboard/getgame", dashboardGUI.new GetGameHandler());
    Spark.get("/dashboard/updategame", dashboardGUI.new UpdateGameHandler());
    Spark.get("/scoreboard", dashboardGUI.new ScoreboardHandler());
    Spark.get("/dashboard/get/opponents",
        dashboardGUI.new GetOpponentsHandler());
    Spark.get("/dashboard/get/teams", dashboardGUI.new GetTeamsHandler());
    Spark.get("/dashboard/opponent/get", dashboardGUI.new GetPlayersHandler(),
        freeMarker);
    Spark.post("/dashboard/autocomplete",
        dashboardGUI.new AutocompleteHandler());
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
    Spark.get("/dashboard/analytics", dashboardGUI.new AnalyticsHandler(),
        freeMarker);

    /*** GameGUI routes ***/
    Spark.post("/game/start", gameGUI.new StartHandler());
    Spark.get("/game/roster", gameGUI.new StatPageHandler());

    /*** PlaymakerGUI routes ***/
    Spark.get("/playmaker", playmakerGUI.new PlaymakerHandler(), freeMarker);
    Spark.post("/playmaker/save", playmakerGUI.new SaveHandler());
    Spark.get("/playmaker/load", playmakerGUI.new LoadHandler());
    Spark.post("/playmaker/delete", playmakerGUI.new DeleteHandler());
    Spark.get("/playmaker/playNames", playmakerGUI.new PlayNamesHandler());
    Spark.get("/playmaker/getPlayerNumbers",
        playmakerGUI.new PlayerNumberHandler());
    Spark.get("/whiteboard", playmakerGUI.new WhiteboardHandler(), freeMarker);

    /*** Stats routes ***/
    Spark.get("/stats", statsEntryGUI.new StatsEntryHandler(), freeMarker);
    Spark.post("/stats/add", statsEntryGUI.new AddStatHandler(), freeMarker);
    Spark.post("/stats/update", statsEntryGUI.new UpdateStatHandler());
    Spark.post("/stats/delete", statsEntryGUI.new DeleteStatHandler());
    Spark.post("/stats/changepossession",
        statsEntryGUI.new FlipPossessionHandler());
    Spark.post("/stats/sub", statsEntryGUI.new SubHandler());
    Spark.post("/stats/timeout", statsEntryGUI.new TimeoutHandler());

    Spark.post("/stats/endgame", statsEntryGUI.new EndGameHandler());
    Spark
    .post("/stats/advanceperiod", statsEntryGUI.new AdvancePeriodHandler());

    // Spark.get("*", new PageNotFoundHandler(), freeMarker);
  }

  /**
   * Initializes the FreeMarker for use with the template route.
   *
   * @return
   */
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

  /**
   * Filters webpages to allow for coach-only functionality and pages.
   *
   * @author ngoelz
   *
   */
  private class CoachFilter implements Filter {

    @Override
    public void handle(Request req, Response res) {
      String clearanceString = req.session().attribute("clearance");

      if (clearanceString == null) {
        res.redirect("/login");
        return;
      }

      int clearance = Integer.parseInt(clearanceString);

      if (clearance == 1) {
        res.redirect("/stats");
      } else if (clearance < 1) {
        res.redirect("/login");
      }
    }

  }

  /**
   * A filter to allow access based on game progress.
   *
   * @author ngoelz
   *
   */
  private class GameCheckFilter implements Filter {
    @Override
    public void handle(Request req, Response res) {
      if (dash.getMyTeam() == null) {
        res.redirect("/dashboard");
      }
    }
  }

  /**
   * Filters based on a stats entry login.
   *
   * @author ngoelz
   *
   */
  private class StatsFilter implements Filter {

    @Override
    public void handle(Request req, Response res) {
      String clearanceString = req.session().attribute("clearance");

      if (clearanceString == null) {
        res.redirect("/login");
        return;
      }

      int clearance = Integer.parseInt(clearanceString);

      if (clearance < 1) {
        res.redirect("/login");
      }
    }

  }

  /*
   * private class PageNotFoundFilter implements Filter {
   *
   * @Override public void handle(Request req, Response res) {
   * System.out.println(req.raw().getRequestURL()); } }
   */

  /**
   * Handler for initial login view. Located here because is small and central
   * to the project.
   *
   * @author ngoelz
   *
   */
  public class LoginViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("tabTitle", "Login",
          "errorMessage", "");
      return new ModelAndView(variables, "login.ftl");
    }
  }

  /**
   * Actual handler that handles a login.
   *
   * @author ngoelz
   *
   */
  private class LoginHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("username");
      String password = qm.value("password");
      int clearance = db.checkPassword(username, password);
      req.session().attribute("clearance",
          Integer.valueOf(clearance).toString());
      Map<String, Object> variables = ImmutableMap.of("clearance", clearance,
          "errorMessage", "");

      return GSON.toJson(variables);
    }
  }

  /**
   * Handles logging out, resets clearance to 0.
   *
   * @author ngoelz
   *
   */
  private class LogoutHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
      req.session().attribute("clearance", "0");
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
