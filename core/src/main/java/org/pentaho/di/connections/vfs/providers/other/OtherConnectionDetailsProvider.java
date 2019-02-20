package org.pentaho.di.connections.vfs.providers.other;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.vfs.VFSConnectionProvider;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/3/19.
 */
public class OtherConnectionDetailsProvider implements VFSConnectionProvider<OtherConnectionDetails> {

  private Supplier<ConnectionManager> connectionManagerSupplier = ConnectionManager::getInstance;

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

  @Override public List<String> getLocations( OtherConnectionDetails vfsConnectionDetails ) {
    String host = vfsConnectionDetails.getHost();
    String port = vfsConnectionDetails.getPort();

    String location = host + ( port != null && !port.equals( "" ) ? ":" + port : "" );
    return Collections.singletonList( location );
  }

  @Override public List<String> getNames() {
    return connectionManagerSupplier.get().getNamesByType( getClass() );
  }

  @SuppressWarnings( "unchecked" )
  @Override public List<OtherConnectionDetails> getConnectionDetails() {
    return (List<OtherConnectionDetails>) connectionManagerSupplier.get().getConnectionDetailsByScheme( getKey() );
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

