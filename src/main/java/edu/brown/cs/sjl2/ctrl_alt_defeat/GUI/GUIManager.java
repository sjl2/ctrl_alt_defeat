package edu.brown.cs.sjl2.ctrl_alt_defeat.GUI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;
import edu.brown.cs.sjl2.ctrl_alt_defeat.Dashboard;
import edu.brown.cs.sjl2.ctrl_alt_defeat.DashboardException;
import edu.brown.cs.sjl2.ctrl_alt_defeat.GUI.PlaymakerGUI.PlaymakerHandler;
import edu.brown.cs.sjl2.ctrl_alt_defeat.database.DBManager;
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



  public GUIManager(String db) {
    this.dbManager = new DBManager(db);
    this.dash = new Dashboard(dbManager);
    this.dashboardGUI = new DashboardGUI(dash, dbManager);
    this.gameGUI = new GameGUI(dash);
    this.playmakerGUI = new PlaymakerGUI(dbManager);
    this.statsEntryGUI = new StatsEntryGUI(dash);
    runServer();
  }

  public GUIManager(String db, int port) {
    this.dbManager = new DBManager(db);
    this.port = port;
    this.playmakerGUI = new PlaymakerGUI(dbManager);
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

    Spark.get("/dashboard", dashboardGUI.new DashboardHandler(), freeMarker);
    Spark.post("/dashboard/new", dashboardGUI.new DashboardSetupHandler(), freeMarker);
    Spark.get("/dashboard/new/team",
        dashboardGUI.new NewTeamHandler(), freeMarker);
    Spark.post("/dashboard/new/team/results",
        dashboardGUI.new NewTeamResultsHandler(), freeMarker);
    Spark.get("/dashboard/new/player",
        dashboardGUI.new NewPlayerHandler(), freeMarker);
    Spark.post("/dashboard/new/player/results",
        dashboardGUI.new NewPlayerResultsHandler(), freeMarker);

    Spark.post("/game/start", gameGUI.new StartHandler());
    Spark.get("/game/roster", gameGUI.new RosterHandler());

		Spark.get("/playmaker", playmakerGUI.new PlaymakerHandler(), freeMarker);
    Spark.post("/playmaker/save", playmakerGUI.new SaveHandler());
    Spark.get("/playmaker/load", playmakerGUI.new LoadHandler());
    Spark.post("/playmaker/delete", playmakerGUI.new DeleteHandler());
    Spark.get("/playmaker/playNames", playmakerGUI.new PlayNamesHandler());

		Spark.get("/stats", statsEntryGUI.new StatsEntryHandler(), freeMarker);
		Spark.post("/stats/add", statsEntryGUI.new AddStatHandler());
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
    public ModelAndView handle(Request rsubleq, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("tabTitle", "Ctrl-Alt-Defeat");
      return new ModelAndView(variables, "ctrl_alt_defeat.ftl");
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
