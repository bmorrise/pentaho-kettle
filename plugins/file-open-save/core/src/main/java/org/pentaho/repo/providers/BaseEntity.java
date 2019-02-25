package org.pentaho.repo.providers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

/**
 * Created by bmorrise on 2/25/19.
 */
public class BaseEntity implements Entity, Providerable {
  private String provider;
  private String name;
  private String path;
  private String parent;
  private String type;
  private String root;
  private Date date;
  private boolean canEdit;

  @Override public String getProvider() {
    return provider;
  }

  public void setProvider( String provider ) {
    this.provider = provider;
  }

  @Override public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @Override public String getPath() {
    return path;
  }

  public void setPath( String path ) {
    this.path = path;
  }

  @Override public String getParent() {
    return parent;
  }

  public void setParent( String parent ) {
    this.parent = parent;
  }

  @Override public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  @Override public String getRoot() {
    return root;
  }

  public void setRoot( String root ) {
    this.root = root;
  }

  @Override public Date getDate() {
    return date;
  }

  public void setDate( Date date ) {
    this.date = date;
  }

  @Override public boolean isCanEdit() {
    return canEdit;
  }

  public void setCanEdit( boolean canEdit ) {
    this.canEdit = canEdit;
  }
}
