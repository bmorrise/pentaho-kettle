package org.pentaho.repo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetails;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.repo.provider.Tree;
import org.pentaho.repo.provider.Utils;
import org.pentaho.repo.provider.vfs.VFSFileProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class CombinedBrowserControllerTest {

  public static final String CONNECTION_NAME = "Connection Name";
  private CombinedBrowserController combinedBrowserController;
  private MemoryMetaStore memoryMetaStore = new MemoryMetaStore();

  private Supplier<ConnectionManager> connectionManager = ConnectionManager::getInstance;

  @Before
  public void setup() throws Exception {
    connectionManager.get().setMetastoreSupplier( () -> memoryMetaStore );
    connectionManager.get().addConnectionProvider( "other", new OtherConnectionDetailsProvider() );
    combinedBrowserController = new CombinedBrowserController( Arrays.asList(
      new VFSFileProvider()
    ) );
  }

  @Test
  public void testLoadTrees() throws Exception {
    OtherConnectionDetails otherConnectionDetails = new OtherConnectionDetails();
    otherConnectionDetails.setName( CONNECTION_NAME );

    connectionManager.get().save( otherConnectionDetails );

    List<Tree> treeList = combinedBrowserController.load();

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

}
