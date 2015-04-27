package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GUI.PlaymakerGUI.PlaymakerHandler;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
import edu.brown.cs.sjl2.ctrl_alt_defeat.trie.Trie;
import freemarker.template.Configuration;

public class GUIManager {

  private DBManager dbManager;
  private int port = 8585;
  private final static int STATUS = 500;
  private final static int TEN_THOUSAND_AND_FOUR = 10004;

  private Dashboard dash;

  private DashboardGUI dashboardGUI;
  private GameGUI gameGUI;
  private StatsEntryGUI statsEntryGUI;
  private PlaymakerGUI playmakerGUI;
  private Trie trie;

  private static final Gson GSON = new Gson();

  public GUIManager(DBManager db) {
    this.dbManager = db;
    this.trie = dbManager.getTrie();
    trie.whiteSpaceOn().prefixOn().editDistanceOn().setK(2);

    this.dash = new Dashboard(dbManager);
    this.dashboardGUI = new DashboardGUI(dash, dbManager, trie);
    this.gameGUI = new GameGUI(dash);
    this.playmakerGUI = new PlaymakerGUI(dash, dbManager);
    this.statsEntryGUI = new StatsEntryGUI(dash);
    runServer();
  }

  public GUIManager(DBManager db, int port) {
    this.dbManager = db;
    this.port = port;
    this.playmakerGUI = new PlaymakerGUI(dash, dbManager);
    this.statsEntryGUI = new StatsEntryGUI(dash);
    runServer();
  }

  private void runServer() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/ctrlaltdefeat", new FrontHandler(), freeMarker);

    //Spark.get("/login", new LoginViewHandler(), freeMarker);
    Spark.post("/login/login", new LoginHandler());
    Spark.get("/dashboard", dashboardGUI.new DashboardHandler(), freeMarker);
    Spark.post("/dashboard/new",
        dashboardGUI.new DashSetupHandler(), freeMarker);
    Spark.get("/dashboard/new/team",
        dashboardGUI.new NewTeamHandler(), freeMarker);
    Spark.post("/dashboard/new/team/results",
        dashboardGUI.new NewTeamResultsHandler(), freeMarker);
    Spark.get("/dashboard/new/player",
        dashboardGUI.new NewPlayerHandler(), freeMarker);
    Spark.post("/dashboard/new/player/results",
        dashboardGUI.new NewPlayerResultsHandler(), freeMarker);
    Spark.get("/dashboard/new/game",
        dashboardGUI.new NewGameHandler(), freeMarker);
    Spark.get("/dashboard/getgame", dashboardGUI.new GetGameHandler());
    Spark.get("/dashboard/updategame", dashboardGUI.new UpdateGameHandler());
    Spark.get("/dashboard/getPlayers", dashboardGUI.new GetPlayersHandler());
    Spark.post("/dashboard/autocomplete", dashboardGUI.new AutocompleteHandler());

    Spark.get("/game/view/:id", dashboardGUI.new GameViewHandler(), freeMarker);
    Spark.get("/team/view/:id", dashboardGUI.new TeamViewHandler(), freeMarker);
    Spark.get("/player/view/:id",
        dashboardGUI.new PlayerViewHandler(), freeMarker);

    Spark.post("/game/start", gameGUI.new StartHandler());
    Spark.get("/game/roster", gameGUI.new StPgHandler());

		Spark.get("/playmaker", playmakerGUI.new PlaymakerHandler(), freeMarker);
    Spark.post("/playmaker/save", playmakerGUI.new SaveHandler());
    Spark.get("/playmaker/load", playmakerGUI.new LoadHandler());
    Spark.post("/playmaker/delete", playmakerGUI.new DeleteHandler());
    Spark.get("/playmaker/playNames", playmakerGUI.new PlayNamesHandler());
    Spark.get("/playmaker/getPlayerNumbers", playmakerGUI.new PlayerNumberHandler());

		Spark.get("/stats", statsEntryGUI.new StatsEntryHandler(), freeMarker);
		Spark.post("/stats/add", statsEntryGUI.new AddStatHandler());
    Spark.post("/stats/update", statsEntryGUI.new UpdateStatHandler());
    Spark.post("/stats/delete", statsEntryGUI.new DeleteStatHandler());
		Spark.post("/stats/changepossession",
		    statsEntryGUI.new FlipPossessionHandler());
		Spark.post("/stats/sub", statsEntryGUI.new SubHandler());
		Spark.post("/stats/timeout", statsEntryGUI.new TimeoutHandler());

		Spark.get("/whiteboard", playmakerGUI.new WhiteboardHandler(), freeMarker);
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * Default Handler for ctrl-alt-defeat
   *
   * @author sjl2
   */
  private class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Ctrl-Alt-Defeat", "errorMessage", "");
      return new ModelAndView(variables, "ctrl_alt_defeat.ftl");
    }
  }

  public class LoginViewHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      System.out.println("a");
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
      Map<String, Object> variables =
        ImmutableMap.of("clearance", clearance, "errorMessage", "");

      return GSON.toJson(variables);
    }
  }

  /**
   * Handler for printing exceptions. Allows for easier debugging by having any
   * exceptions thrown while using the GUI to go right to browser and not just
   * be displayed on the command line.
   *
   * @author sjl2
   *
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
