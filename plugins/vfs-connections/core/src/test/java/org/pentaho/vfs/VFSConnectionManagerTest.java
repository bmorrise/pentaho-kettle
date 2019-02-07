package org.pentaho.vfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.vfs.VFSConnectionDetails;
import org.pentaho.di.vfs.VFSConnectionManager;
import org.pentaho.di.vfs.model.Data;
import org.pentaho.di.vfs.providers.other.OtherConnectionDetails;
import org.pentaho.di.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.osgi.metastore.locator.api.MetastoreLocator;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManagerTest {

  private static final String CONNECTION_NAME = "Connection Name";
  private static final String HOST = "localhost";
  private static final String PORT = "80";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String OTHER = "other";
  private static final String FTP_SCHEMA = "ftp";
  private static final String HTTP_SCHEMA = "http";
  private static final String FTP_EXAMPLE_FILE = "ftp://example.com/file.txt";
  private static final String HTTP_EXAMPLE_FILE = "http://example.com/test.txt";
  private VFSConnectionManager vfsConnectionManager = VFSConnectionManager.getInstance();
  private MemoryMetaStore memoryMetaStore = new MemoryMetaStore();

  @Before
  public void setup() {
    vfsConnectionManager.setMetastoreSupplier( () -> memoryMetaStore );
    vfsConnectionManager.addVFSConnectionProvider( OTHER, new OtherConnectionDetailsProvider() );
    vfsConnectionManager.addSchemaLookup( FTP_SCHEMA, OTHER );
    vfsConnectionManager.addSchemaLookup( HTTP_SCHEMA, OTHER );
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
            .getConnectionDetails( OTHER, CONNECTION_NAME );

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

  @Test
  public void testGetTypes() {
    Data data = vfsConnectionManager.getData( OTHER );
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enableDefaultTyping();

    try {
      String value = objectMapper.writeValueAsString( data.getModel() );
      VFSConnectionDetails newData = objectMapper.readValue( value, VFSConnectionDetails.class );
      Assert.assertNotNull( newData );
    } catch ( Exception e ) {
      Assert.fail();
    }
  }

  @Test
  public void loadData() {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );
    otherConnectionDetails.setUsername( USERNAME );
    otherConnectionDetails.setPassword( PASSWORD );
    vfsConnectionManager.save( otherConnectionDetails );

    Data data = vfsConnectionManager.loadData( CONNECTION_NAME );

    Assert.assertNotNull( data.getModel() );
    Assert.assertNotNull( data.getFields() );

    Assert.assertEquals( USERNAME, ((OtherConnectionDetails) data.getModel()).getUsername() );
    Assert.assertEquals( PASSWORD, ((OtherConnectionDetails) data.getModel()).getPassword() );
    Assert.assertEquals( OTHER, data.getType() );
  }

  @Test
  public void testGetFields() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enableDefaultTyping();

    System.out.println( objectMapper.writeValueAsString( new OtherConnectionDetailsProvider().getFields() ) );
  }
}
