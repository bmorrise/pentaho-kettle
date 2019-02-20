package org.pentaho.repo.provider.local;

import org.pentaho.repo.provider.File;
import org.pentaho.repo.provider.FileProvider;
import org.pentaho.repo.provider.Tree;
import org.pentaho.repo.provider.local.model.LocalDirectory;
import org.pentaho.repo.provider.local.model.LocalFile;
import org.pentaho.repo.provider.local.model.LocalTree;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bmorrise on 2/16/19.
 */
public class LocalFileProvider implements FileProvider {

  public static final String NAME = "Local";
  public static final String TYPE = "LOCALFILE";

  @Override public String getName() {
    return NAME;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public Tree getTree() {
    LocalTree localTree = new LocalTree( NAME );
    String home = System.getProperty( "user.home" );
    String user = System.getProperty( "user.dir" );
//    localTree.addChild( getDirectory( "/" ) );
//    localTree.addChild( getDirectory( user ) );
    localTree.addChild( getDirectory( home + "/Documents" ) );
    localTree.addChild( getDirectory( home + "/Downloads" ) );
    localTree.addChild( getDirectory( home + "/Desktop" ) );
    return localTree;
  }

  public LocalDirectory getDirectory( String path ) {
    java.io.File file = new java.io.File( path );
    LocalDirectory localDirectory = new LocalDirectory();
    localDirectory.setName( file.getName() );
    localDirectory.setPath( file.getAbsolutePath() );
    localDirectory.setDate( new Date( file.lastModified() ) );
    return localDirectory;
  }

  @Override public List<LocalFile> getFiles( String name, String path ) {
    List<LocalFile> files = new ArrayList<>();
    java.io.File file = new java.io.File( path );
    for ( java.io.File child : file.listFiles() ) {
      if ( child.isFile() && !child.isHidden() ) {
        LocalFile localFile = new LocalFile();
        localFile.setName( child.getName() );
        localFile.setPath( child.getAbsolutePath() );
        localFile.setDate( new Date( child.lastModified() ) );
        files.add( localFile );
      } else if ( child.isDirectory() && !child.isHidden() ) {
        LocalDirectory localDirectory = new LocalDirectory();
        localDirectory.setName( child.getName() );
        localDirectory.setPath( child.getAbsolutePath() );
        localDirectory.setDate( new Date( child.lastModified() ) );
        files.add( localDirectory );
      }
    }
    return files;
  }

  @Override public boolean isAvailable() {
    return true;
  }
}
