package org.pentaho.di.vfs;

import org.apache.commons.vfs2.FileSystemOptions;

/**
 * Created by bmorrise on 2/3/19.
 */
public interface VFSConnectionProvider<T extends VFSConnectionDetails> {
  Class<T> getType();
  FileSystemOptions getOpts( T vfsConnectionDetails );
}
