package org.pentaho.repo.provider.local.model;

import org.pentaho.repo.provider.Tree;
import org.pentaho.repo.provider.local.LocalFileProvider;
import org.pentaho.repo.provider.vfs.model.VFSLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/16/19.
 */
public class LocalTree implements Tree<LocalFile> {

  @Override public String getProvider() {
    return LocalFileProvider.TYPE;
  }

  private List<LocalFile> localFiles = new ArrayList<>();
  private String name;

  public LocalTree( String name ) {
    this.name = name;
  }

  @Override public String getName() {
    return name;
  }

  @Override public List<LocalFile> getChildren() {
    return localFiles;
  }

  @Override public void addChild( LocalFile child ) {
    localFiles.add( child );
  }

  public void setFiles( List<LocalFile> localFiles ) {
    this.localFiles = localFiles;
  }
}
