package org.pentaho.repo.provider.local.model;

import org.pentaho.repo.provider.File;

import java.util.Date;

/**
 * Created by bmorrise on 2/16/19.
 */
public class LocalFile implements File {

  public static final String TYPE = "file";
  public static final String ACTION = "localfile";

  private String name;
  private String path;
  private String parent;
  private Date date;

  @Override public String getName() {
    return name;
  }

  @Override public String getPath() {
    return path;
  }

  @Override public String getParent() {
    return parent;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public String getAction() {
    return ACTION;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public void setParent( String parent ) {
    this.parent = parent;
  }

  @Override public Date getDate() {
    return date;
  }

  public void setDate( Date date ) {
    this.date = date;
  }
}
