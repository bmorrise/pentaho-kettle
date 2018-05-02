package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/20/18.
 */
public class File {

  public static String FILE = "file";
  public static String DIRECTORY = "directory";

  private String id;
  private String path;
  private String name;
  private String type;
  private String extension;

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension( String extension ) {
    this.extension = extension;
  }
}
