package org.pentaho.di.connections.core;

import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.connections.api.ConnectionDetails;
import org.pentaho.di.connections.api.ConnectionProvider;
import org.pentaho.di.connections.api.vfs.VFSConnectionDetails;
import org.pentaho.di.connections.api.vfs.VFSConnectionProvider;
import org.pentaho.di.connections.api.vfs.VFSLookupFilter;
import org.pentaho.di.connections.core.vfs.providers.other.OtherConnectionDetails;
import org.pentaho.di.connections.core.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;

import java.util.List;

/**
 * Created by bmorrise on 2/3/19.
 */
public class ConnectionManagerTest {

  private static final String CONNECTION_NAME = "Connection Name";
  private static final String HOST = "localhost";
  private static final String PORT = "80";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String OTHER = "other";
  private static final String TEST_NAME = "Test Name";
  private static final String FTP_SCHEMA = "ftp";
  private static final String HTTP_SCHEMA = "http";
  private static final String FTP_EXAMPLE_FILE = "ftp://example.com/file.txt";
  private static final String HTTP_EXAMPLE_FILE = "http://example.com/test.txt";
  private ConnectionManager connectionManager = ConnectionManager.getInstance();
  private MemoryMetaStore memoryMetaStore = new MemoryMetaStore();

  @Before
  public void setup() {
    connectionManager.setMetastoreSupplier( () -> memoryMetaStore );
    connectionManager.addConnectionProvider( OTHER, new OtherConnectionDetailsProvider() );
    connectionManager.addConnectionProvider( MyTestConnectionProvider.KEY, new MyTestConnectionProvider() );
    VFSLookupFilter vfsLookupFilter = new VFSLookupFilter();
    vfsLookupFilter.addKeyLookup( FTP_SCHEMA, OTHER );
    vfsLookupFilter.addKeyLookup( HTTP_SCHEMA, OTHER );
    connectionManager.addLookupFilter( vfsLookupFilter );
  }

  @Test
  public void testSaveAndLoadConnectionDetails() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setHost( HOST );
    otherConnectionDetails.setPort( PORT );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    connectionManager.save( otherConnectionDetails );

    OtherConnectionDetails otherConnectionDetails2 = (OtherConnectionDetails) connectionManager
      .getConnectionDetails( OTHER, CONNECTION_NAME );

    Assert.assertEquals( otherConnectionDetails2.getName(), CONNECTION_NAME );
    Assert.assertEquals( otherConnectionDetails2.getHost(), HOST );
    Assert.assertEquals( otherConnectionDetails2.getPort(), PORT );
    Assert.assertEquals( otherConnectionDetails2.getUsername(), USERNAME );
    Assert.assertEquals( otherConnectionDetails2.getPassword(), PASSWORD );

    VFSConnectionProvider<OtherConnectionDetails> vfsConnectionProvider =
      (VFSConnectionProvider<OtherConnectionDetails>) connectionManager.getConnectionProvider( FTP_EXAMPLE_FILE );
    FileSystemOptions fileSystemOptions = vfsConnectionProvider
      .getOpts( (OtherConnectionDetails) connectionManager.loadConnectionDetails( CONNECTION_NAME ) );
    Assert.assertNotNull( fileSystemOptions );
  }

  @Test
  public void testNullConnectionDetails() {
    VFSConnectionProvider<OtherConnectionDetails> vfsConnectionProvider =
      (VFSConnectionProvider<OtherConnectionDetails>) connectionManager.getConnectionProvider( HTTP_EXAMPLE_FILE );
    FileSystemOptions fileSystemOptions = vfsConnectionProvider
      .getOpts( (OtherConnectionDetails) connectionManager.loadConnectionDetails( CONNECTION_NAME ) );
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
    connectionManager.save( otherConnectionDetails );

    List<String> names = connectionManager.getNames();
    Assert.assertEquals( CONNECTION_NAME, names.get( 0 ) );
  }

  @Test
  public void loadData() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    connectionManager.save( otherConnectionDetails );

    OtherConnectionDetails vfsConnectionDetails =
      (OtherConnectionDetails) connectionManager.loadConnectionDetails( CONNECTION_NAME );

    Assert.assertNotNull( vfsConnectionDetails );

    Assert.assertEquals( USERNAME, ( vfsConnectionDetails ).getUsername() );
    Assert.assertEquals( PASSWORD, ( vfsConnectionDetails ).getPassword() );
    Assert.assertEquals( OTHER, vfsConnectionDetails.getType() );
  }

  @Test
  public void testGetNamesByType() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    connectionManager.save( otherConnectionDetails );

    MyTestConnectionDetails myTestConnectionDetails = new MyTestConnectionDetails();
    myTestConnectionDetails.setName( TEST_NAME );
    connectionManager.save( myTestConnectionDetails );

    Assert.assertEquals( CONNECTION_NAME, connectionManager.getNamesByType( VFSConnectionProvider.class ).get( 0 ) );
    Assert.assertEquals( TEST_NAME, connectionManager.getNamesByType( TestConnectionProvider.class ).get( 0 ) );
  }

  @MetaStoreElementType(
    name = "Test Connection",
    description = "Defines the connection details for a generic test connection" )
  public class MyTestConnectionDetails implements TestConnectionDetails {

    public static final String TYPE = "test";

    @MetaStoreAttribute
    private String name;

    @Override public String getName() {
      return name;
    }

    @Override public void setName( String name ) {
      this.name = name;
    }

    @Override public String getType() {
      return TYPE;
    }
  }

  public class MyTestConnectionProvider implements TestConnectionProvider<TestConnectionDetails> {

    public static final String NAME = "Test";
    public static final String KEY = "test";

    @Override public String getName() {
      return NAME;
    }

    @Override public String getKey() {
      return KEY;
    }

    @Override public Class<? extends ConnectionDetails> getClassType() {
      return MyTestConnectionDetails.class;
    }

    @Override public String doStuff( TestConnectionDetails testConnectionDetails ) {
      return null;
    }
  }

  public interface TestConnectionProvider<T extends TestConnectionDetails> extends ConnectionProvider {
    String doStuff( T testConnectionDetails );
  }

  public interface TestConnectionDetails extends ConnectionDetails {

  }
}
