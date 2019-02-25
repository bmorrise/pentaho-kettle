package org.pentaho.repo.controller;

import org.pentaho.repo.provider.File;
import org.pentaho.repo.provider.FileProvider;
import org.pentaho.repo.provider.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public class CombinedBrowserController {

  private List<FileProvider> fileProviders = new ArrayList<>();

  public CombinedBrowserController( List<FileProvider> fileProviders ) {
    this.fileProviders = fileProviders;
  }

  public FileProvider getFileProvider( String type ) {
    return fileProviders.stream().filter( fileProvider1 -> fileProvider1.getType().equalsIgnoreCase( type ) )
      .findFirst()
      .orElse( null );
  }

  public List<Tree> load() {
    List<Tree> trees = new ArrayList<>();
    for ( FileProvider fileProvider : fileProviders ) {
      if ( fileProvider.isAvailable() ) {
        trees.add( fileProvider.getTree() );
      }
    }
    return trees;
  }

  public List<? extends File> getFiles( String type, String connection, String path, String filters ) {
    FileProvider fileProvider = getFileProvider( type );
    if ( fileProvider != null && fileProvider.isAvailable() ) {
      return fileProvider.getFiles( connection, path, filters );
    }
    return Collections.emptyList();
  }
}
