package org.pentaho.repo.provider.vfs.model;

import org.pentaho.repo.provider.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSDirectory extends VFSFile implements Directory {
  private boolean hasChildren;
  private List<VFSFile> children = new ArrayList<>();

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

  public List<VFSFile> getChildren() {
    return children;
  }

  public void setChildren( List<VFSFile> children ) {
    this.children = children;
  }

  public void addChild( VFSFile file ) {
    this.children.add( file );
  }

}
