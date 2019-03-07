package org.pentaho.repo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.vfs.VFSLookupFilter;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetails;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.repo.api.providers.Properties;
import org.pentaho.repo.api.providers.Result;
import org.pentaho.repo.api.providers.Tree;
import org.pentaho.repo.api.providers.Utils;
import org.pentaho.repo.providers.local.LocalFileProvider;
import org.pentaho.repo.providers.vfs.VFSFileProvider;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class FileBrowserControllerTest {
  public static final String FTP_SCHEMA = "ftp";
  public static final String OTHER = "other";
  public static final String CONNECTION_NAME = "Connection Name";
  private FileBrowserController fileBrowserController;
  private MemoryMetaStore memoryMetaStore = new MemoryMetaStore();

  private Supplier<ConnectionManager> connectionManager = ConnectionManager::getInstance;

  @Before
  public void setup() throws Exception {
    connectionManager.get().setMetastoreSupplier( () -> memoryMetaStore );
    connectionManager.get().addConnectionProvider( "other", new OtherConnectionDetailsProvider() );
    fileBrowserController = new FileBrowserController( Arrays.asList(
      new LocalFileProvider(),
      new VFSFileProvider(),
      new TestFileProvider()
    ) );
  }

  @Test
  public void testLoadTrees() throws Exception {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );

    connectionManager.get().save( otherConnectionDetails );

    List<Tree> treeList = fileBrowserController.load();

    ObjectMapper objectMapper = new ObjectMapper();
    System.out.println( objectMapper.writeValueAsString( treeList ) );
  }

  @Test
  public void testFilters() throws Exception {
    String filters = ".kjb|.ktr";
    List<String> names =
      Arrays.asList( "file.csv", "file.txt", "file.ktr", "file.svg", "file.jpg", "file.kjb", "file1.ktr" );

    List<String> filtered = new ArrayList<>();
    for ( String name : names ) {
      if ( Utils.matches( name, filters ) ) {
        filtered.add( name );
      }
    }
    Assert.assertEquals( 3, filtered.size() );
    Assert.assertEquals( filtered.get( 0 ), "file.ktr" );
    Assert.assertEquals( filtered.get( 1 ), "file.kjb" );
    Assert.assertEquals( filtered.get( 2 ), "file1.ktr" );
  }

  @Test
  public void testCopyFileFromFTP() throws Exception {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );

    connectionManager.get().save( otherConnectionDetails );
    VFSLookupFilter vfsLookupFilter = new VFSLookupFilter();
    vfsLookupFilter.addKeyLookup( FTP_SCHEMA, OTHER );
    connectionManager.get().addLookupFilter( vfsLookupFilter );

    fileBrowserController.copyFile( "vfs", "local", "ftp://speedtest.tele2.net/2MB.zip", "/tmp/2MB.zip", true,
      Properties.create( "fromConnection", CONNECTION_NAME, "toConnection", CONNECTION_NAME ) );
  }

  @Test
  public void testCopyFileToFTP() throws Exception {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );

    connectionManager.get().save( otherConnectionDetails );
    VFSLookupFilter vfsLookupFilter = new VFSLookupFilter();
    vfsLookupFilter.addKeyLookup( FTP_SCHEMA, OTHER );
    connectionManager.get().addLookupFilter( vfsLookupFilter );


    java.io.File file = new java.io.File( "/tmp/testfile.txt" );
    FileOutputStream fileOutputStream = new FileOutputStream( file );
    try ( BufferedOutputStream bufferedOutputStream = new BufferedOutputStream( fileOutputStream ) ) {
      bufferedOutputStream.write( "This is a test".getBytes() );
    }

    fileBrowserController
      .copyFile( "local", "vfs", "/tmp/testfile.txt", "ftp://speedtest.tele2.net/upload/testfile.txt", true,
        Properties.create( "fromConnection", CONNECTION_NAME, "toConnection", CONNECTION_NAME ) );
  }

  @Test
  public void testNoProvider() throws Exception {
    fileBrowserController.addFolder( "none", "testpath", Properties.create( "connection", "testconnection" ) );
  }

  @Test
  public void testGetNewName() throws Exception {
    Result result = fileBrowserController.getNewName( "local", "/tmp/test", Properties.create() );
    System.out.println( result.getData() );
  }

  public class TestFileProvider extends LocalFileProvider {
    private static final String TYPE = "test";

    @Override public String getType() {
      return TYPE;
    }
  }

}
