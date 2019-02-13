package org.pentaho.di.connections.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.connections.ConnectionProvider;

/**
 * Created by bmorrise on 2/3/19.
 */
public interface VFSConnectionProvider<T extends VFSConnectionDetails> extends ConnectionProvider {
  FileSystemOptions getOpts( T vfsConnectionDetails );
}

