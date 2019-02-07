package org.pentaho.di.vfs.providers.other;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.pentaho.di.vfs.VFSConnectionProvider;
import org.pentaho.di.vfs.model.FieldMeta;
import org.pentaho.di.vfs.utils.FieldBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by bmorrise on 2/3/19.
 */
public class OtherConnectionDetailsProvider implements VFSConnectionProvider<OtherConnectionDetails> {

  public static final String NAME = "Other";
  public static final String SCHEMA = "other";

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

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getSchema() {
    return SCHEMA;
  }

  @Override
  public String getTemplate() {
    try {
      return IOUtils.toString( getClass().getClassLoader().getResourceAsStream( "other-template.html" ) );
    } catch ( IOException e ) {
      return null;
    }
  }

  @Override
  public List<FieldMeta> getFields() {
    return new FieldBuilder().build( getType() );
  }
}
