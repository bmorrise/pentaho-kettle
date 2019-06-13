package org.pentaho.di.connections.vfs.provider;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileNotFoundException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.URLFileName;
import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;

import java.util.Collection;
import java.util.function.Supplier;

public class ConnectionFileSystem extends AbstractFileSystem implements FileSystem {

  public static final String CONNECTION = "connection";
  private Supplier<ConnectionManager> connectionManager = ConnectionManager::getInstance;

  public ConnectionFileSystem( FileName rootName, FileSystemOptions fileSystemOptions ) {
    super( rootName, null, fileSystemOptions );
  }

  @Override
  protected FileObject createFile( AbstractFileName abstractFileName ) throws Exception {
    String connectionName = ( (URLFileName) abstractFileName ).getHostName();
    ConnectionDetails connectionDetails = connectionManager.get().getConnectionDetails( connectionName );

    if ( connectionDetails != null ) {
      String url = connectionDetails.getType() + ":/" + abstractFileName.getPath();
      Variables variables = new Variables();
      variables.setVariable( CONNECTION, connectionName );
      return KettleVFS.getFileObject( url, variables );
    }

    return KettleVFS.getFileObject( abstractFileName.getPath() );
  }

  @Override protected void addCapabilities( Collection<Capability> collection ) {
    collection.addAll( ConnectionFileProvider.capabilities );
  }

}
