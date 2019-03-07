/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.repo.providers.local;

import org.pentaho.repo.api.providers.FileProvider;
import org.pentaho.repo.api.providers.Properties;
import org.pentaho.repo.api.providers.Result;
import org.pentaho.repo.api.providers.Tree;
import org.pentaho.repo.api.providers.Utils;
import org.pentaho.repo.providers.local.model.LocalDirectory;
import org.pentaho.repo.providers.local.model.LocalFile;
import org.pentaho.repo.providers.local.model.LocalTree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bmorrise on 2/16/19.
 */
public class LocalFileProvider implements FileProvider {

  public static final String NAME = "Local";
  public static final String TYPE = "local";

  /**
   * @return
   */
  @Override public String getName() {
    return NAME;
  }

  /**
   * @return
   */
  @Override public String getType() {
    return TYPE;
  }

  /**
   * @return
   */
  @Override public Tree getTree() {
    LocalTree localTree = new LocalTree( NAME );
    String home = System.getProperty( "user.home" );
    String user = System.getProperty( "user.dir" );
    localTree.setFiles( getFiles( "/", null, null ) );
    return localTree;
  }

  /**
   * @param path
   * @return
   */
  public LocalDirectory getDirectory( String path ) {
    File file = new File( path );
    LocalDirectory localDirectory = new LocalDirectory();
    localDirectory.setName( file.getName() );
    localDirectory.setPath( file.getAbsolutePath() );
    localDirectory.setDate( new Date( file.lastModified() ) );
    return localDirectory;
  }

  /**
   * @param path
   * @param filters
   * @param properties
   * @return
   */
  // TODO: Filter out certain files from root
  @Override public List<LocalFile> getFiles( String path, String filters, Properties properties ) {
    List<LocalFile> files = new ArrayList<>();
    try {
      Files.list( Paths.get( path ) ).forEach( path1 -> {
        String name = path1.getFileName().toString();
        try {
          if ( Files.isDirectory( path1 ) && !Files.isHidden( path1 ) ) {
            files.add( LocalDirectory.create( path1 ) );
          } else if ( !Files.isHidden( path1 ) && Utils.matches( name, filters ) ) {
            files.add( LocalFile.create( path1 ) );
          }
        } catch ( IOException e ) {
          // Do nothing yet
        }
      } );
    } catch ( IOException e ) {
      // Do nothing yet
    }
    return files;
  }

  /**
   * @return
   */
  @Override public boolean isAvailable() {
    return true;
  }

  /**
   * @param paths
   * @param properties
   * @return
   */
  //TODO: Handle directories with files in them
  @Override public Result deleteFiles( List<String> paths, Properties properties ) {
    Result.Status status = Result.Status.SUCCESS;
    List<String> deletedFiles = new ArrayList<>();
    for ( String path : paths ) {
      try {
        Files.delete( Paths.get( path ) );
        deletedFiles.add( path );
      } catch ( IOException e ) {
        status = Result.Status.ERROR;
      }
    }
    return new Result( status, "Delete Files Completed", deletedFiles );
  }

  /**
   * @param path
   * @param properties
   * @return
   */
  @Override public Result addFolder( String path, Properties properties ) {
    Path folderPath = Paths.get( path );
    if ( Files.exists( folderPath ) ) {
      return Result.fileCollision( "Folder already exists", null );
    }
    try {
      Path newPath = Files.createDirectories( Paths.get( path ) );
      LocalDirectory localDirectory = new LocalDirectory();
      localDirectory.setName( newPath.getFileName().toString() );
      localDirectory.setPath( newPath.getFileName().toString() );
      localDirectory.setDate( new Date( Files.getLastModifiedTime( newPath ).toMillis() ) );
      localDirectory.setRoot( NAME );
      localDirectory.setCanAddChildren( true );
      localDirectory.setCanEdit( true );
      return Result.success( "Add Folder Completed", localDirectory );
    } catch ( IOException e ) {
      return Result.error( "Add Folder Error", null );
    }
  }

  /**
   * @param path
   * @param newPath
   * @param overwrite
   * @param properties
   * @return
   */
  @Override public Result renameFile( String path, String newPath, boolean overwrite, Properties properties ) {
    try {
      if ( overwrite ) {
        Files.move( Paths.get( path ), Paths.get( newPath ), StandardCopyOption.REPLACE_EXISTING );
      } else {
        Files.move( Paths.get( path ), Paths.get( newPath ) );
      }
      return Result.success( "Success renaming file", newPath );
    } catch ( IOException e ) {
      return Result.error( "Error renaming file", path );
    }
  }

  @Override public Result copyFile( String path, String newPath, boolean overwrite, Properties properties ) {
    try {
      Files.copy( Paths.get( path ), Paths.get( newPath ), StandardCopyOption.REPLACE_EXISTING );
      return Result.success( "Copy Finished", newPath );
    } catch ( IOException e ) {
      return Result.error( "Error during copy", path );
    }
  }

  @Override public Result fileExists( String path, Properties properties ) {
    return Result.success( "File exists", Files.exists( Paths.get( path ) ) );
  }

  /**
   * @param path
   * @param properties
   * @return
   */
  @Override public InputStream readFile( String path, Properties properties ) {
    try {
      return new BufferedInputStream( new FileInputStream( new File( path ) ) );
    } catch ( FileNotFoundException e ) {
      return null;
    }
  }

  /**
   * @param inputStream
   * @param path
   * @param properties
   * @param overwrite
   * @return
   * @throws FileAlreadyExistsException
   */
  @Override public boolean writeFile( InputStream inputStream, String path, Properties properties, boolean overwrite )
    throws FileAlreadyExistsException {
    try {
      Files.copy( inputStream, Paths.get( path ) );
      return true;
    } catch ( FileAlreadyExistsException e ) {
      throw e;
    } catch ( IOException e ) {
      return false;
    }
  }

  /**
   * @param provider
   * @param properties
   * @return
   */
  @Override public boolean isSame( String provider, Properties properties ) {
    return provider.equals( TYPE );
  }

  @Override public Result getNewName( String path, Properties properties ) {
    String extension = Utils.getExtension( path );
    String parent = Utils.getParent( path );
    String name = Utils.getName( path ).replace( "." + extension, "" );
    int i = 1;
    String testName = path;
    while ( Files.exists( Paths.get( testName ) ) ) {
      if ( Utils.isValidExtension( extension ) ) {
        testName = parent + name + " " + String.valueOf( i ) + "." + extension;
      } else {
        testName = path + " " + String.valueOf( i );
      }
      i++;
    }
    return Result.success( "Got new name", testName );
  }
}
