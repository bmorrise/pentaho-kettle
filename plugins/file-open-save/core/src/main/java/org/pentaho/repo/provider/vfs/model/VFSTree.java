package org.pentaho.repo.provider.vfs.model;

import org.pentaho.repo.provider.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSTree implements Tree<VFSLocation> {

  public static final String PROVIDER = "vfs";

  public VFSTree( String name ) {
    this.name = name;
  }

  public String getProvider() {
    return PROVIDER;
  }

  private String name;

  @Override public String getName() {
    return name;
  }

  private List<VFSLocation> vfsLocations = new ArrayList<>();

  @Override public List<VFSLocation> getChildren() {
    return vfsLocations;
  }

  @Override public void addChild( VFSLocation child ) {
    vfsLocations.add( child );
  }
}
