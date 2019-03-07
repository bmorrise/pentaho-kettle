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

package org.pentaho.repo.providers.vfs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.ConnectionProvider;
import org.pentaho.di.connections.vfs.VFSConnectionDetails;
import org.pentaho.di.connections.vfs.VFSConnectionProvider;
import org.pentaho.di.connections.vfs.VFSHelper;
import org.pentaho.di.connections.vfs.VFSRoot;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.repo.api.providers.File;
import org.pentaho.repo.api.providers.FileProvider;
import org.pentaho.repo.api.providers.Properties;
import org.pentaho.repo.api.providers.Result;
import org.pentaho.repo.api.providers.Utils;
import org.pentaho.repo.providers.vfs.model.VFSDirectory;
import org.pentaho.repo.providers.vfs.model.VFSFile;
import org.pentaho.repo.providers.vfs.model.VFSLocation;
import org.pentaho.repo.providers.vfs.model.VFSTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class VFSFileProvider implements FileProvider {

  public static final String NAME = "Environments";
  public static final String TYPE = "vfs";
  public static final String CONNECTION = "connection";
  public static final String FROM_CONNECTION = "fromConnection";
  public static final String TO_CONNECTION = "toConnection";

  private Supplier<ConnectionManager> connectionManagerSupplier = ConnectionManager::getInstance;

  @Override public String getName() {
    return NAME;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public VFSTree getTree() {
    List<ConnectionProvider<? extends ConnectionDetails>> providers =
      connectionManagerSupplier.get().getProvidersByType( VFSConnectionProvider.class );

    VFSTree vfsTree = new VFSTree( NAME );

    for ( ConnectionProvider<? extends ConnectionDetails> provider : providers ) {
      for ( ConnectionDetails connectionDetails : provider.getConnectionDetails() ) {
        VFSConnectionProvider<VFSConnectionDetails> vfsConnectionProvider =
          (VFSConnectionProvider<VFSConnectionDetails>) provider;

        VFSLocation vfsLocation = new VFSLocation();
        vfsLocation.setName( connectionDetails.getName() );
        vfsLocation.setRoot( NAME );
        vfsLocation.setHasChildren( true );
        vfsLocation.setCanDelete( true );
        vfsTree.addChild( vfsLocation );

        VFSConnectionDetails vfsConnectionDetails = (VFSConnectionDetails) connectionDetails;

        List<VFSRoot> roots = vfsConnectionProvider.getLocations( vfsConnectionDetails );
        for ( VFSRoot root : roots ) {
          VFSDirectory vfsDirectory = new VFSDirectory();
          vfsDirectory.setName( root.getName() );
          vfsDirectory.setDate( root.getModifiedDate() );
          vfsDirectory.setHasChildren( true );
          vfsDirectory.setCanAddChildren( true );
          vfsDirectory.setConnection( connectionDetails.getName() );
          vfsDirectory.setPath( vfsConnectionProvider.getProtocol( vfsConnectionDetails ) + "://" + root.getName() );
          vfsDirectory.setRoot( NAME );
          vfsLocation.addChild( vfsDirectory );
        }
      }
    }

    return vfsTree;
  }

  public List<File> getFiles( String path, String filters, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    List<File> files = new ArrayList<>();
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      FileType fileType = fileObject.getType();
      if ( fileType.hasChildren() ) {
        for ( FileObject child : fileObject.getChildren() ) {
          FileType fileType1 = child.getType();
          if ( fileType1.hasChildren() ) {
            files.add( VFSDirectory.create( child, connection ) );
          } else {
            if ( Utils.matches( child.getName().getBaseName(), filters ) ) {
              files.add( VFSFile.create( child, connection ) );
            }
          }
        }
      }
    } catch ( KettleFileException | FileSystemException ignored ) {
      // File does not exist
    }
    return files;
  }

  @Override
  public Result deleteFiles( List<String> paths, Properties properties ) {
    List<String> deletedFiles = new ArrayList<>();
    String connection = properties.getString( CONNECTION );
    for ( String path : paths ) {
      try {
        FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
        if ( fileObject.delete() ) {
          deletedFiles.add( path );
        }
      } catch ( KettleFileException | FileSystemException kfe ) {
        // Ignore don't add
      }
    }
    return new Result( Result.Status.SUCCESS, "Delete Files Completed", deletedFiles );
  }

  @Override public Result addFolder( String path, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      fileObject.createFolder();
      new Result( Result.Status.SUCCESS, "Add Folder Completed", VFSDirectory.create( fileObject, connection ) );
    } catch ( KettleFileException | FileSystemException kfe ) {
      // TODO: Do something smart here
      System.out.println( "Could not create folder" );
    }
    return new Result( Result.Status.ERROR, "Add Folder Completed", null );
  }

  @Override public boolean isAvailable() {
    return true;
  }

  @Override public Result renameFile( String path, String newPath, boolean overwrite, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      FileObject renameObject =
        KettleVFS.getFileObject( newPath, new Variables(), VFSHelper.getOpts( path, connection ) );
      if ( overwrite ) {
        if ( renameObject.exists() ) {
          renameObject.delete();
        }
      }
      fileObject.moveTo( renameObject );
      return Result.success( "Success renaming file", newPath );
    } catch ( KettleFileException | FileSystemException e ) {
      return Result.error( "Error renaming file", path );
    }
  }

  @Override public Result copyFile( String path, String newPath, boolean overwrite, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      FileObject copyObject =
        KettleVFS.getFileObject( newPath, new Variables(), VFSHelper.getOpts( path, connection ) );
      copyObject.copyFrom( fileObject, Selectors.SELECT_SELF );
      return Result.success( "Success copying file", path );
    } catch ( KettleFileException | FileSystemException e ) {
      return Result.error( "Error copying file", path );
    }
  }

  @Override public Result fileExists( String path, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      return Result.success( "File exists", fileObject.exists() );
    } catch ( KettleFileException | FileSystemException e ) {
      return Result.success( "Error", null );
    }
  }

  @Override public InputStream readFile( String path, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
      return fileObject.getContent().getInputStream();
    } catch ( KettleException | FileSystemException e ) {
      return null;
    }
  }

  @Override public boolean writeFile( InputStream file, String path, Properties properties, boolean overwrite )
    throws FileAlreadyExistsException {
    String connection = properties.getString( CONNECTION );
    FileObject fileObject = null;
    try {
      fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, connection ) );
    } catch ( KettleException ke ) {
      // ignored
    }
    if ( fileObject != null ) {
      try ( OutputStream outputStream = fileObject.getContent().getOutputStream(); ) {
        IOUtils.copy( file, outputStream );
        outputStream.flush();
        return true;
      } catch ( IOException e ) {
        return false;
      }
    }
    return false;
  }

  @Override public boolean isSame( String provider, Properties properties ) {
    return provider.equals( TYPE ) && properties.getString( FROM_CONNECTION )
      .equals( properties.getString( TO_CONNECTION ) );
  }

  @Override public Result getNewName( String path, Properties properties ) {
    String connection = properties.getString( CONNECTION );
    String extension = Utils.getExtension( path );
    String parent = Utils.getParent( path );
    String name = Utils.getName( path ).replace( "." + extension, "" );
    int i = 1;
    String testName = path;
    try {
      while ( KettleVFS.getFileObject( testName, new Variables(), VFSHelper.getOpts( testName, connection ) )
        .exists() ) {
        if ( Utils.isValidExtension( extension ) ) {
          testName = parent + name + " " + String.valueOf( i ) + "." + extension;
        } else {
          testName = path + " " + String.valueOf( i );
        }
        i++;
      }
    } catch ( KettleFileException | FileSystemException e ) {
      return Result.error( "Error getting new name", testName );
    }
    return Result.success( "Got new name", testName );
  }
}
