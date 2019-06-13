package org.pentaho.di.connections.vfs.provider;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.common.TestConnectionDetails;
import org.pentaho.di.connections.common.TestConnectionProvider;
import org.pentaho.di.connections.common.TestFileProvider;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;

public class ConnectionFileProviderTest {

  public static final String PVFS_FILE_PATH = "pvfs://Connection Name/path/to/file.txt";
  public static final String PVFS_FILE_PATH_1 = "pvfs://Fake Item/path/to/file.txt";
  public static final String DEFAULT_FILE_PATH = "file:///path/to/file.txt";
  public static final String REAL_FILE_PATH = "test://path/to/file.txt";
  private static String CONNECTION_NAME = "Connection Name";

  private ConnectionManager connectionManager;
  private MemoryMetaStore memoryMetaStore = new MemoryMetaStore();

  @Before
  public void setup() throws Exception {
    connectionManager = ConnectionManager.getInstance();
    connectionManager.setMetastoreSupplier( () -> memoryMetaStore );

    DefaultFileSystemManager fsm = (DefaultFileSystemManager) KettleVFS.getInstance().getFileSystemManager();
    if ( !fsm.hasProvider( ConnectionFileProvider.SCHEME ) ) {
      fsm.addProvider( ConnectionFileProvider.SCHEME, new ConnectionFileProvider() );
      fsm.addProvider( TestFileProvider.SCHEME, new TestFileProvider() );
    }
  }

  private void addProvider() {
    TestConnectionProvider testConnectionProvider = new TestConnectionProvider( connectionManager );
    connectionManager.addConnectionProvider( TestConnectionProvider.SCHEME, testConnectionProvider );
  }

  private void addOne() {
    addProvider();
    TestConnectionDetails testConnectionDetails = new TestConnectionDetails();
    testConnectionDetails.setName( CONNECTION_NAME );
    connectionManager.save( testConnectionDetails );
  }

  @Test
  public void testGetFile() throws Exception {
    addOne();

    FileObject fileObject = KettleVFS.getFileObject( PVFS_FILE_PATH );
    Assert.assertTrue( fileObject.exists() );
    Assert.assertEquals( REAL_FILE_PATH, fileObject.getPublicURIString() );
  }

  @Test
  public void testGetFileNotFound() throws Exception {
    addOne();

    FileObject fileObject = KettleVFS.getFileObject( PVFS_FILE_PATH_1 );
    Assert.assertFalse( fileObject.exists() );
    Assert.assertEquals( DEFAULT_FILE_PATH, fileObject.getPublicURIString() );
  }

}
