package org.pentaho.di.ui.repo;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.RepositoryConnectController;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by bmorrise on 5/3/16.
 */
@RunWith( MockitoJUnitRunner.class )
public class RepositoryConnectControllerTest {

  public static final String PLUGIN_NAME = "PLUGIN NAME";
  public static final String ID = "ID";
  public static final String PLUGIN_DESCRIPTION = "PLUGIN DESCRIPTION";
  public static final String DATABASE_NAME = "DATABASE NAME";
  public static final String REPOSITORY_NAME = "Repository Name";

  @Mock
  RepositoriesMeta repositoriesMeta;

  @Mock
  PluginRegistry pluginRegistry;

  @Mock
  RepositoryMeta repositoryMeta;

  @Mock
  PluginInterface pluginInterface;

  @Mock
  AbstractRepository repository;

  @Mock
  DatabaseMeta databaseMeta;

  @Mock
  Spoon spoon;

  private RepositoryConnectController controller;

  @BeforeClass
  public static void setUpClass() throws Exception {
    if ( !KettleEnvironment.isInitialized() ) {
      KettleEnvironment.init();
    }
  }

  @Before
  public void setUp() {
    controller = new RepositoryConnectController( pluginRegistry, spoon, repositoriesMeta );

    when( pluginInterface.getName() ).thenReturn( PLUGIN_NAME );
    when( pluginInterface.getIds() ).thenReturn( new String[] { ID } );
    when( pluginInterface.getDescription() ).thenReturn( PLUGIN_DESCRIPTION );

    List<PluginInterface> plugins = new ArrayList<>();
    plugins.add( pluginInterface );

    when( pluginRegistry.getPlugins( RepositoryPluginType.class ) ).thenReturn( plugins );

    when( repositoryMeta.getId() ).thenReturn( ID );
    when( repositoryMeta.getName() ).thenReturn( PLUGIN_NAME );
    when( repositoryMeta.getDescription() ).thenReturn( PLUGIN_DESCRIPTION );
  }

  @Test
  public void testGetPlugins() throws Exception {
    String plugins = controller.getPlugins();
    assertEquals( "[{\"name\":\"PLUGIN NAME\",\"description\":\"PLUGIN DESCRIPTION\",\"id\":\"ID\"}]", plugins );
  }

  @Test
  public void testCreateRepository() throws Exception {
    String id = ID;
    Map<String, Object> items = new HashMap<>();

    when( pluginRegistry.loadClass( RepositoryPluginType.class, id, RepositoryMeta.class ) )
      .thenReturn( repositoryMeta );
    when( pluginRegistry.loadClass( RepositoryPluginType.class, repositoryMeta.getId(), Repository.class ) )
      .thenReturn( repository );

    when( repository.test() ).thenReturn( true );

    boolean result = controller.createRepository( id, items );

    assertEquals( true, result );

    when( repository.test() ).thenReturn( false );

    result = controller.createRepository( id, items );

    assertEquals( false, result );

    when( repository.test() ).thenReturn( true );
    doThrow( new KettleException() ).when( repositoriesMeta ).writeData();

    result = controller.createRepository( id, items );
    assertEquals( false, result );
  }

  @Test
  public void testGetRepositories() {
    when( repositoriesMeta.nrRepositories() ).thenReturn( 1 );
    when( repositoriesMeta.getRepository( 0 ) ).thenReturn( repositoryMeta );

    String repositories = controller.getRepositories();

    assertEquals( "[{\"name\":\"PLUGIN NAME\",\"description\":\"PLUGIN DESCRIPTION\",\"id\":\"ID\"}]", repositories );
  }

  @Test
  public void testConnectToRepository() throws Exception {
    when( pluginRegistry.loadClass( RepositoryPluginType.class, repositoryMeta.getId(), Repository.class ) )
      .thenReturn( repository );

    controller.setCurrentRepository( repositoryMeta );
    controller.connectToRepository();

    verify( repository ).init( repositoryMeta );
    verify( repository ).connect( null, null );
  }

  @Test
  public void testGetDatabases() throws Exception {
    when( repositoriesMeta.nrDatabases() ).thenReturn( 1 );
    when( repositoriesMeta.getDatabase( 0 ) ).thenReturn( databaseMeta );
    when( databaseMeta.getName() ).thenReturn( DATABASE_NAME );

    String databases = controller.getDatabases();
    assertEquals( "[{\"name\":\"DATABASE NAME\"}]", databases );
  }

  @Test
  public void testDeleteRepository() throws Exception {
    String repositoryName = REPOSITORY_NAME;
    int index = 1;
    when( repositoriesMeta.findRepository( repositoryName ) ).thenReturn( repositoryMeta );
    when( repositoriesMeta.indexOfRepository( repositoryMeta ) ).thenReturn( index );

    boolean result = controller.deleteRepository( repositoryName );

    assertEquals( true, result );
    verify( repositoriesMeta ).removeRepository( index );
    verify( repositoriesMeta ).writeData();
  }

  @Test
  public void testSetDefaultRepository() {
    boolean result = controller.setDefaultRepository( REPOSITORY_NAME );
    assertEquals( true, result );
  }

  @Test
  public void testAddDatabase() throws Exception {
    controller.addDatabase( databaseMeta );

    verify( repositoriesMeta ).addDatabase( databaseMeta );
    verify( repositoriesMeta ).writeData();
  }

  @Test
  public void testGetDefaultUrl() throws Exception {
    String defaultUrl = controller.getDefaultUrl();
    assertNotNull( defaultUrl );
  }
}
