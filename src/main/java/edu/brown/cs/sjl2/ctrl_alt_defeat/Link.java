package edu.brown.cs.sjl2.ctrl_alt_defeat;

/**
 * A Link class is the information needed to represent the bare minimum of an
 * entity as a link. Consists of a link path, text, and id.
 *
 * @author sjl2
 *
 */
public class Link {
  private int id;
  private String path;
  private String text;

  /**
   * Constructs a Link object used to represent an entity or list item on front
   * end.
   *
   * @param id
   *          ID of the object that the link represents.
   * @param path
   *          The url path to the id.
   * @param text
   *          The text to display on the link.
   */
  public Link(int id, String path, String text) {
    this.id = id;
    this.path = path;
    this.text = text;
  }

  /**
   * Getter for the URL of the link. Can be href'ed.
   *
   * @return Returns the full URL of the link.
   */
  public String getURL() {
    return path + id;
  }

  /**
   * Getter for the id of the object represented by the link.
   *
   * @return Returns the int of the id.
   */
  public int getID() {
    return id;
  }

  /**
   * Getter for the text to be displayed on the link.
   *
   * @return the text of the link
   */
  public String getText() {
    return text;
  }
}
