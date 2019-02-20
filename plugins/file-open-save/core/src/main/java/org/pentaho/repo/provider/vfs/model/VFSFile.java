package org.pentaho.repo.provider.vfs.model;

import org.pentaho.repo.provider.File;

import java.util.Date;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSFile implements File {
  public static String TYPE = "file";
  public static String ACTION = "VFS";

  private String name;
  private String path;
  private String parent;
  private String connection;
  private Date date;

  @Override public String getType() {
    return TYPE;
  }

  @Override public String getAction() {
    return ACTION;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection( String connection ) {
    this.connection = connection;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  public String getParent() {
    return parent;
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
