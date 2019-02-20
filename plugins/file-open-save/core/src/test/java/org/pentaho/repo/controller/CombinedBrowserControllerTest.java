package org.pentaho.repo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetails;
import org.pentaho.di.connections.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.repo.provider.Tree;

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
    combinedBrowserController = new CombinedBrowserController();
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

}
