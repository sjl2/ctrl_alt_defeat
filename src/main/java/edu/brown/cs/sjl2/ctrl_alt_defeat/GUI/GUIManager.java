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
import edu.brown.cs.sjl2.ctrl_alt_defeat.Game;
import freemarker.template.Configuration;

public class GUIManager {

  private File db;
  private int port = 8585;
  private final static int STATUS = 500;

  private Dashboard dash;
  private Game game;

  private PlaymakerGUI playmakerGUI;
  private StatGUI statGUI;

  public GUIManager(File db) {
    this.db = db;
    this.playmakerGUI = new PlaymakerGUI(dash);
    this.statGUI = new StatGUI(dash);
    runServer();
  }

  public GUIManager(File db, int port) {
    this.db = db;
    this.port = port;
    this.playmakerGUI = new PlaymakerGUI(dash);
    this.statGUI = new StatGUI(dash);
    runServer();
  }

  private void runServer() {
    Spark.setPort(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/ctrlaltdefeat", new FrontHandler(), freeMarker);
		Spark.get("/playmaker", playmakerGUI.new PlaymakerHandler(), freeMarker);
		Spark.post("/add/stat", statGUI.new AddStat());
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
   * @author awainger
   */
  private class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables =
        ImmutableMap.of("title", "Bacon",
          "movieDB", db);
      return new ModelAndView(variables, "query.ftl");
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
