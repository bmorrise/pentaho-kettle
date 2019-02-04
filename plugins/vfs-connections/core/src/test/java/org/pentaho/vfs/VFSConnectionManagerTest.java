package org.pentaho.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.vfs.VFSConnectionManager;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.di.vfs.providers.OtherConnectionDetails;
import org.pentaho.di.vfs.providers.OtherConnectionDetailsProvider;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManagerTest {

  private static final String CONNECTION_NAME = "Connection Name";
  private static final String HOST = "localhost";
  private static final String PORT = "80";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private VFSConnectionManager vfsConnectionManager;

  @Before
  public void setup() {
    vfsConnectionManager = new VFSConnectionManager( new MemoryMetaStore() );
    vfsConnectionManager.addVFSConnectionProvider( new OtherConnectionDetailsProvider() );
  }

  @Test
  public void testSaveAndLoadConnectionDetails() throws MetaStoreException {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setHost( HOST );
    otherConnectionDetails.setPort( PORT );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    vfsConnectionManager.save( otherConnectionDetails );

    OtherConnectionDetails otherConnectionDetails2 = (OtherConnectionDetails) vfsConnectionManager.getConnectionDetails(
            OtherConnectionDetails.class, CONNECTION_NAME );

    Assert.assertEquals( otherConnectionDetails2.getName(), CONNECTION_NAME );
    Assert.assertEquals( otherConnectionDetails2.getHost(), HOST );
    Assert.assertEquals( otherConnectionDetails2.getPort(), PORT );
    Assert.assertEquals( otherConnectionDetails2.getUsername(), USERNAME );
    Assert.assertEquals( otherConnectionDetails2.getPassword(), PASSWORD );

    FileSystemOptions fileSystemOptions = vfsConnectionManager.getFileSystemOpts(
            OtherConnectionDetails.class, CONNECTION_NAME );

    Assert.assertNotNull( fileSystemOptions );
  }

}
