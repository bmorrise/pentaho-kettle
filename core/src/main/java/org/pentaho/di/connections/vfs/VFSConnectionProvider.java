package org.pentaho.di.connections.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.connections.ConnectionProvider;

import java.util.List;

/**
 * Created by bmorrise on 2/3/19.
 */
public interface VFSConnectionProvider<T extends VFSConnectionDetails> extends ConnectionProvider<T> {
  FileSystemOptions getOpts( T vfsConnectionDetails );
  List<String> getLocations( T vfsConnectionDetails );
  String getProtocol( T vfsConnectionDetails );
}
