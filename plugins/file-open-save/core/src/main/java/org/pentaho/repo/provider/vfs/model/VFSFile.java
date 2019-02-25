package org.pentaho.repo.provider.vfs.model;

import org.pentaho.repo.provider.File;
import org.pentaho.repo.provider.Providerable;
import org.pentaho.repo.provider.vfs.VFSFileProvider;

import javax.xml.ws.Provider;
import java.util.Date;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSFile implements File, Providerable {
  public static String TYPE = "file";

  private String name;
  private String path;
  private String parent;
  private String connection;
  private String root;
  private Date date;

  @Override public String getType() {
    return TYPE;
  }

  @Override public String getProvider() {
    return VFSFileProvider.TYPE;
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

  @Override public String getRoot() {
    return root;
  }

  public void setRoot( String root ) {
    this.root = root;
  }
}
