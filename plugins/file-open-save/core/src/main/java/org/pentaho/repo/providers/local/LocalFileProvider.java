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

import mondrian.olap.Util;
import org.pentaho.repo.providers.FileOpts;
import org.pentaho.repo.providers.FileProvider;
import org.pentaho.repo.providers.FromTo;
import org.pentaho.repo.providers.Properties;
import org.pentaho.repo.providers.Result;
import org.pentaho.repo.providers.Snipes;
import org.pentaho.repo.providers.Tree;
import org.pentaho.repo.providers.Utils;
import org.pentaho.repo.providers.local.model.LocalDirectory;
import org.pentaho.repo.providers.local.model.LocalFile;
import org.pentaho.repo.providers.local.model.LocalTree;
import org.pentaho.repo.providers.processor.Process;
import org.pentaho.repo.providers.processor.Processor;

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
  public static final int ONE_MINUTE = 60000;

  private final Processor processor;

  public LocalFileProvider( Processor processor ) {
    this.processor = processor;
  }

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
    localTree.setFiles( getFiles( "/", null, null ) );
    return localTree;
  }

  public LocalDirectory getDirectory( String path ) {
    File file = new File( path );
    LocalDirectory localDirectory = new LocalDirectory();
    localDirectory.setName( file.getName() );
    localDirectory.setPath( file.getAbsolutePath() );
    localDirectory.setDate( new Date( file.lastModified() ) );
    return localDirectory;
  }

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

  @Override public boolean isAvailable() {
    return true;
  }

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

  @Override public Result addFolder( String path, Properties properties ) {
    try {
      Path newPath = Files.createDirectories( Paths.get( path ) );
      LocalDirectory localDirectory = new LocalDirectory();
      localDirectory.setName( newPath.getFileName().toString() );
      localDirectory.setPath( newPath.getFileName().toString() );
      localDirectory.setDate( new Date( Files.getLastModifiedTime( newPath ).toMillis() ) );
      localDirectory.setRoot( NAME );
      localDirectory.setCanAddChildren( true );
      localDirectory.setCanEdit( true );
      return new Result( Result.Status.SUCCESS, "Add Folder Completed", localDirectory );
    } catch ( IOException e ) {
      return new Result( Result.Status.ERROR, "Add Folder Error", null );
    }
  }

  /**
   * @param path
   * @param newPath
   * @param overwrite
   * @param properties
   * @return
   */
  @Override public Result renameFile( String path, String newPath, String overwrite, Properties properties ) {
    Result.Status status = Result.Status.SUCCESS;
    String updatedPath = "";
    try {
      switch ( overwrite ) {
        case FileOpts.OVERWRITE_ALL:
        case FileOpts.OVERWRITE_ONE:
          updatedPath =
            Files.move( Paths.get( path ), Paths.get( newPath ), StandardCopyOption.REPLACE_EXISTING ).toString();
          break;
        case FileOpts.RENAME_ONE:
        case FileOpts.RENAME_ALL:
          int index = 1;
          while ( Files.exists( Paths.get( newPath ) ) ) {
            newPath = newPath + " " + String.valueOf( index++ );
          }
        default:
          updatedPath = Files.move( Paths.get( path ), Paths.get( newPath ) ).toString();
          break;
      }
    } catch ( FileAlreadyExistsException e ) {
      status = Result.Status.FILE_COLLISION;
    } catch ( IOException e ) {
      status = Result.Status.ERROR;
    }
    return new Result( status, "Rename File Completed", updatedPath );
  }

  /**
   * @param paths
   * @param newPath
   * @param overwrite
   * @param properties
   * @return
   */
  @Override public Result moveFiles( List<String> paths, String newPath, boolean overwrite, Properties properties ) {
    String uuid = processor.submit( new Process() {
      @Override public void run() {
        Result.Status status = Result.Status.SUCCESS;
        List<String> moved = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> error = new ArrayList<>();
        for ( String path : paths ) {
          String movePath = newPath + path.substring( path.lastIndexOf( "/" ), path.length() );
          setStatus( new Result( Result.Status.PENDING, "Moving Files In Progress", new FromTo( path, movePath ) ) );
          Result result = renameFile( path, movePath, getProperties().getString( FileOpts.OVERWRITE ), properties );
          System.out.println( path + ":" + movePath );
          if ( result.getStatus().equals( Result.Status.FILE_COLLISION ) ) {
            setStatus(
              new Result( Result.Status.FILE_COLLISION, "Moving Files Collision", new FromTo( path, movePath ) ) );
            setState( State.PAUSED );
            System.out.println( getState() );
            synchronized ( this ) {
              while ( isState( State.PAUSED ) ) {
                try {
                  this.wait();
                } catch ( InterruptedException e ) {
                  e.printStackTrace();
                }
              }
            }
            String overwrite = getProperties().getString( FileOpts.OVERWRITE );
            if ( !Util.isEmpty( overwrite ) ) {
              if ( renameFile( path, movePath, overwrite, properties ).getStatus()
                .equals( Result.Status.SUCCESS ) ) {
                moved.add( path );
              }
            } else {
              skipped.add( path );
            }
            if ( overwrite.equals( FileOpts.OVERWRITE_ONE ) || overwrite.equals( FileOpts.RENAME_ONE ) ) {
              setProperty( FileOpts.OVERWRITE, null );
            }
          } else if ( !result.getStatus().equals( Result.Status.SUCCESS ) ) {
            status = Result.Status.ERROR;
            error.add( path );
          } else {
            moved.add( path );
          }
        }
        setStatus( new Result( status, "Moving Files Complete", new Snipes( moved, skipped, error ) ) );
      }
    } );
    return new Result( Result.Status.PENDING, "Moving Files Initiated", uuid );
  }

  @Override public InputStream readFile( String path, Properties properties ) {
    try {
      return new BufferedInputStream( new FileInputStream( new File( path ) ) );
    } catch ( FileNotFoundException e ) {
      return null;
    }
  }

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
}
