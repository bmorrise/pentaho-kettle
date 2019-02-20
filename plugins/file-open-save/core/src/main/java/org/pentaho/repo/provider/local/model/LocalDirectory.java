package org.pentaho.repo.provider.local.model;

import org.pentaho.repo.provider.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/16/19.
 */
public class LocalDirectory extends LocalFile implements Directory {
  private boolean hasChildren;
  private List<LocalFile> children = new ArrayList<>();

  public static String DIRECTORY = "folder";

  @Override public String getType() {
    return DIRECTORY;
  }

  public boolean hasChildren() {
    return hasChildren;
  }

  public void setHasChildren( boolean hasChildren ) {
    this.hasChildren = hasChildren;
  }

  public List<LocalFile> getChildren() {
    return children;
  }

  public void setChildren( List<LocalFile> children ) {
    this.children = children;
  }

  public void addChild( LocalFile file ) {
    this.children.add( file );
  }
}
