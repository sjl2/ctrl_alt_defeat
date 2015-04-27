package edu.brown.cs.sjl2.ctrl_alt_defeat;

public class Link {
  private int id;
  private String path;
  private String text;

  public Link(int id, String path, String text) {
    this.id = id;
    this.path = path;
    this.text = text;
  }

  public String getURL() {
    return path + id;
  }
  
  public int getID() {
    return id;
  }

  public String getText() {
    return text;
  }
}
