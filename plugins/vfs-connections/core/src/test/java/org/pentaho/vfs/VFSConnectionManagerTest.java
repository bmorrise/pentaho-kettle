package org.pentaho.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.vfs.VFSConnectionManager;
import org.pentaho.di.vfs.providers.OtherConnectionDetails;
import org.pentaho.di.vfs.providers.OtherConnectionDetailsProvider;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;

import java.util.List;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManagerTest {

  private static final String CONNECTION_NAME = "Connection Name";
  private static final String HOST = "localhost";
  private static final String PORT = "80";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String FTP_SCHEMA = "ftp";
  private static final String FTP_EXAMPLE_FILE = "ftp://example.com/file.txt";
  private static final String HTTP_EXAMPLE_FILE = "http://example.com/test.txt";
  private VFSConnectionManager vfsConnectionManager;

  @Before
  public void setup() {
    vfsConnectionManager = new VFSConnectionManager( new MemoryMetaStore() );
    vfsConnectionManager.addVFSConnectionProvider( FTP_SCHEMA, new OtherConnectionDetailsProvider() );
  }

  @Test
  public void testSaveAndLoadConnectionDetails() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setHost( HOST );
    otherConnectionDetails.setPort( PORT );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    vfsConnectionManager.save( otherConnectionDetails );

    OtherConnectionDetails otherConnectionDetails2 = (OtherConnectionDetails) vfsConnectionManager
            .getConnectionDetails( FTP_SCHEMA, CONNECTION_NAME );

    Assert.assertEquals( otherConnectionDetails2.getName(), CONNECTION_NAME );
    Assert.assertEquals( otherConnectionDetails2.getHost(), HOST );
    Assert.assertEquals( otherConnectionDetails2.getPort(), PORT );
    Assert.assertEquals( otherConnectionDetails2.getUsername(), USERNAME );
    Assert.assertEquals( otherConnectionDetails2.getPassword(), PASSWORD );

    FileSystemOptions fileSystemOptions = vfsConnectionManager.getFileSystemOpts( FTP_EXAMPLE_FILE, CONNECTION_NAME );

    Assert.assertNotNull( fileSystemOptions );
  }

  @Test
  public void testNullConnectionDetails() {
    FileSystemOptions fileSystemOptions = vfsConnectionManager.getFileSystemOpts( HTTP_EXAMPLE_FILE, CONNECTION_NAME );
    Assert.assertNull( fileSystemOptions );
  }

  @Test
  public void testGetNames() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setHost( HOST );
    otherConnectionDetails.setPort( PORT );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    vfsConnectionManager.save( otherConnectionDetails );

    List<String> names = vfsConnectionManager.getNames();
    Assert.assertEquals( CONNECTION_NAME, names.get( 0 ) );
  }
}
