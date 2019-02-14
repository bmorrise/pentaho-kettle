package org.pentaho.di.connections.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.connections.ConnectionManager;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSHelper {
  public static FileSystemOptions getOpts( String file, String connection ) {
    VFSConnectionDetails vfsConnectionDetails =
      (VFSConnectionDetails) ConnectionManager.getInstance().getConnectionDetails( file, connection );
    VFSConnectionProvider<VFSConnectionDetails> vfsConnectionProvider =
      (VFSConnectionProvider<VFSConnectionDetails>) ConnectionManager.getInstance().getConnectionProvider( file );
    return vfsConnectionProvider.getOpts( vfsConnectionDetails );
  }
}
