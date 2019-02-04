package org.pentaho.di.vfs.providers;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pentaho.di.vfs.VFSConnectionProvider;

/**
 * Created by bmorrise on 2/3/19.
 */
public class OtherConnectionDetailsProvider implements VFSConnectionProvider<OtherConnectionDetails> {
  @Override
  public Class<OtherConnectionDetails> getType() {
    return OtherConnectionDetails.class;
  }

  @Override
  public FileSystemOptions getOpts( OtherConnectionDetails vfsConnectionDetails ) {
    StaticUserAuthenticator auth = new StaticUserAuthenticator( vfsConnectionDetails.getHost(), vfsConnectionDetails
            .getUsername(), vfsConnectionDetails.getPassword() );
    FileSystemOptions opts = new FileSystemOptions();
    try {
      DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
    } catch ( FileSystemException fse ) {
      // Ignore and return default options
    }
    return opts;
  }

}
