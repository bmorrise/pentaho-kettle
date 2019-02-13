package org.pentaho.di.connections.core.vfs.builder;

import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemOptions;

/**
 * Created by bmorrise on 11/7/18.
 */
public abstract class VFSConnectionConfigurationBuilder extends FileSystemConfigBuilder {

  public VFSConnectionConfigurationBuilder( FileSystemOptions fileSystemOptions ) {
    this.fileSystemOptions = fileSystemOptions;
  }

  private FileSystemOptions fileSystemOptions;

  public FileSystemOptions getFileSystemOptions() {
    return fileSystemOptions;
  }

  public void setFileSystemOptions( FileSystemOptions fileSystemOptions ) {
    this.fileSystemOptions = fileSystemOptions;
  }
}
