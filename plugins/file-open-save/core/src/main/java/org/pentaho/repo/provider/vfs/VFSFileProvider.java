package org.pentaho.repo.provider.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.ConnectionProvider;
import org.pentaho.di.connections.vfs.VFSConnectionDetails;
import org.pentaho.di.connections.vfs.VFSConnectionProvider;
import org.pentaho.di.connections.vfs.VFSHelper;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.repo.provider.File;
import org.pentaho.repo.provider.FileProvider;
import org.pentaho.repo.provider.vfs.model.VFSDirectory;
import org.pentaho.repo.provider.vfs.model.VFSFile;
import org.pentaho.repo.provider.vfs.model.VFSLocation;
import org.pentaho.repo.provider.vfs.model.VFSTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class VFSFileProvider implements FileProvider {

  public static final String NAME = "Environments";
  public static final String TYPE = "VFS";

  private Supplier<ConnectionManager> connectionManagerSupplier = ConnectionManager::getInstance;

  @Override public String getName() {
    return NAME;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public VFSTree getTree() {
    List<ConnectionProvider> providers =
      connectionManagerSupplier.get().getProvidersByType( VFSConnectionProvider.class );

    VFSTree vfsTree = new VFSTree( NAME );

    for ( ConnectionProvider provider : providers ) {
      for ( ConnectionDetails connectionDetails : provider.getConnectionDetails() ) {
        VFSConnectionProvider vfsConnectionProvider = (VFSConnectionProvider) provider;

        VFSLocation vfsLocation = new VFSLocation();
        vfsLocation.setName( connectionDetails.getName() );
        vfsTree.addChild( vfsLocation );

        List<String> locations = vfsConnectionProvider.getLocations( (VFSConnectionDetails) connectionDetails );
        for ( String location : locations ) {
          VFSDirectory vfsDirectory = new VFSDirectory();
          vfsDirectory.setName( location );
          vfsDirectory.setConnection( connectionDetails.getName() );
          vfsDirectory.setPath( vfsConnectionProvider.getKey() + "://" + location );
          vfsLocation.addChild( vfsDirectory );
        }
      }
    }

    return vfsTree;
  }

  public List<File> getFiles( String name, String path ) {
    List<File> files = new ArrayList<>();
    try {
      FileObject fileObject = KettleVFS.getFileObject( path, new Variables(), VFSHelper.getOpts( path, name ) );
      FileType fileType = fileObject.getType();
      if ( fileType.hasChildren() ) {
        FileObject[] children = fileObject.getChildren();
        for ( FileObject child : children ) {
          FileType fileType1 = child.getType();
          if ( fileType1.hasChildren() ) {
            VFSDirectory vfsDirectory = new VFSDirectory();
            vfsDirectory.setName( child.getName().getBaseName() );
            vfsDirectory.setPath( child.getName().getFriendlyURI() );
            vfsDirectory.setConnection( name );
            files.add( vfsDirectory );
          } else {
            VFSFile vfsFile = new VFSFile();
            vfsFile.setName( child.getName().getBaseName() );
            vfsFile.setPath( child.getName().getFriendlyURI() );
            vfsFile.setConnection( name );
            files.add( vfsFile );
          }
        }
      }

      System.out.println( fileObject );
    } catch ( KettleFileException | FileSystemException kfe ) {
      // Do something smart here
    }
    return files;
  }

  @Override public boolean isAvailable() {
    return true;
  }
}
