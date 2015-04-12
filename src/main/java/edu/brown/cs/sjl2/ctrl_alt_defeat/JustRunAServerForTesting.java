package edu.brown.cs.sjl2.ctrl_alt_defeat;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;


public class JustRunAServerForTesting {
  public static void main(String[] args) {
    runSparkServer();
  }
  
  private static void runSparkServer() {
    Spark.externalStaticFileLocation(
        "src/main/resources/static");
    Spark.get("/", new GetHandler(), new FreeMarkerEngine());
  }

  private static class GetHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request arg0, Response arg1) {
      Map<String, Object> variables = new HashMap<String, Object>();
      variables.put("title", "whatever");
      return new ModelAndView(variables, "statsEntryFirst.ftl");
    }
  }
  
  
}
