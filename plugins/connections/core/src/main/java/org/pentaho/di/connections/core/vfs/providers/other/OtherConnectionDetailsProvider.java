package org.pentaho.di.connections.core.vfs.providers.other;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pentaho.di.connections.api.vfs.VFSConnectionProvider;

/**
 * Created by bmorrise on 2/3/19.
 */
public class OtherConnectionDetailsProvider implements VFSConnectionProvider<OtherConnectionDetails> {

  public static final String NAME = "Other";
  public static final String SCHEME = "other";

  @Override public Class<OtherConnectionDetails> getClassType() {
    return OtherConnectionDetails.class;
  }

  @Override public FileSystemOptions getOpts( OtherConnectionDetails otherConnectionDetails ) {
    if ( otherConnectionDetails == null ) {
      return null;
    }
    StaticUserAuthenticator auth =
      new StaticUserAuthenticator( otherConnectionDetails.getHost(), otherConnectionDetails.getUsername(),
        otherConnectionDetails.getPassword() );
    FileSystemOptions opts = new FileSystemOptions();
    try {
      DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator( opts, auth );
    } catch ( FileSystemException fse ) {
      // Ignore and return default options
    }
    return opts;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getKey() {
    return SCHEME;
  }
}
