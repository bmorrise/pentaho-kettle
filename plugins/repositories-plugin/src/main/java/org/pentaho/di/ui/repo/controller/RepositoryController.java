/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2016 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.ui.repo.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.model.RepositoryModel;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Created by bmorrise on 4/18/16.
 */
public class RepositoryController {

  public static final String DEFAULT_URL = "defaultUrl";
  public static final String ERROR_MESSAGE = "errorMessage";
  public static final String ERROR_401 = "401";

  private static Class<?> PKG = RepositoryController.class;
  private static LogChannelInterface log =
    KettleLogStore.getLogChannelInterfaceFactory().create( RepositoryController.class );

  private RepositoriesMeta repositoriesMeta;
  private PluginRegistry pluginRegistry;
  private Spoon spoon;
  private RepositoryModel repositoryModel;
  private List<RepositoryListener> listeners = new ArrayList<>();

  public RepositoryController( PluginRegistry pluginRegistry, Spoon spoon, RepositoriesMeta repositoriesMeta,
                               RepositoryModel repositoryModel ) {
    this.pluginRegistry = pluginRegistry;
    this.spoon = spoon;
    this.repositoriesMeta = repositoriesMeta;
    this.repositoryModel = repositoryModel;
    try {
      repositoriesMeta.readData();
    } catch ( KettleException ke ) {
      log.logError( "Unable to load repositories", ke );
    }
    repositoryModel.setRepositories( getRepositoryList() );
  }

  public RepositoryController( RepositoryModel repositoryModel ) {
    this( PluginRegistry.getInstance(), Spoon.getInstance(), new RepositoriesMeta(), repositoryModel );
  }

  @SuppressWarnings( "unchecked" )
  public String getPlugins() {
    List<PluginInterface> plugins = pluginRegistry.getPlugins( RepositoryPluginType.class );
    JSONArray list = new JSONArray();
    for ( PluginInterface pluginInterface : plugins ) {
      if ( !pluginInterface.getIds()[0].equals( "PentahoEnterpriseRepository" ) ) {
        JSONObject repoJSON = new JSONObject();
        repoJSON.put( "id", pluginInterface.getIds()[ 0 ] );
        repoJSON.put( "name", pluginInterface.getName() );
        repoJSON.put( "description", pluginInterface.getDescription() );
        list.add( repoJSON );
      }
    }
    return list.toString();
  }

  public boolean createRepository( String id, Map<String, Object> items ) {
    try {
      RepositoryMeta repositoryMeta = pluginRegistry.loadClass( RepositoryPluginType.class, id, RepositoryMeta.class );
      repositoryMeta.populate( items, repositoriesMeta );

      if ( repositoryMeta.getName() != null ) {
        Repository repository =
          pluginRegistry.loadClass( RepositoryPluginType.class, repositoryMeta.getId(), Repository.class );
        repository.init( repositoryMeta );
        if ( repositoryModel.getCurrentRepositoryMeta() != null ) {
          repositoriesMeta.removeRepository( repositoriesMeta.indexOfRepository(
            repositoryModel.getCurrentRepositoryMeta() ) );
        }
        repositoriesMeta.addRepository( repositoryMeta );
        repositoryModel.setCurrentRepositoryMeta( repositoryMeta );
        save();
        if ( !( (AbstractRepository) repository ).test() ) {
          return false;
        }
        ( (AbstractRepository) repository ).create();
      }
    } catch ( KettleException ke ) {
      log.logError( "Unable to load repository type", ke );
      return false;
    }
    return true;
  }

  public List<String> getRepositoryList() {
    List<String> repositories = new ArrayList<>();
    if ( repositoriesMeta != null ) {
      for ( int i = 0; i < repositoriesMeta.nrRepositories(); i++ ) {
        repositories.add( repositoriesMeta.getRepository( i ).getName() );
      }
    }
    return repositories;
  }

  @SuppressWarnings( "unchecked" )
  public String getRepositories() {
    JSONArray list = new JSONArray();
    if ( repositoriesMeta != null ) {
      for ( int i = 0; i < repositoriesMeta.nrRepositories(); i++ ) {
        list.add( repositoriesMeta.getRepository( i ).toJSONObject() );
      }
    }
    return list.toString();
  }

  public String getRepository( String name ) {
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( name );
    if ( repositoryMeta != null ) {
      repositoryModel.setCurrentRepositoryMeta( repositoryMeta );
      return repositoryMeta.toJSONObject().toString();
    }
    return "";
  }

  public DatabaseMeta getDatabase( String name ) {
    return repositoriesMeta.searchDatabase( name );
  }

  public void removeDatabase( String name ) {
    int index = repositoriesMeta.indexOfDatabase( repositoriesMeta.searchDatabase( name ) );
    if ( index != -1 ) {
      repositoriesMeta.removeDatabase( index );
    }
    save();
  }

  @SuppressWarnings( "unchecked" )
  public String getDatabases() {
    JSONArray list = new JSONArray();
    for ( int i = 0; i < repositoriesMeta.nrDatabases(); i++ ) {
      JSONObject databaseJSON = new JSONObject();
      databaseJSON.put( "name", repositoriesMeta.getDatabase( i ).getName() );
      list.add( databaseJSON );
    }
    return list.toString();
  }

  public String connectToRepository() {
    return connectToRepository( repositoryModel.getCurrentRepositoryMeta() );
  }

  public String connectToRepository( String username, String password ) {
    return connectToRepository( repositoryModel.getCurrentRepositoryMeta(), username, password );
  }

  public String connectToRepository( RepositoryMeta repositoryMeta ) {
    return connectToRepository( repositoryMeta, null, null );
  }

  public String connectToRepository( RepositoryMeta repositoryMeta, String username, String password ) {
    JSONObject jsonObject = new JSONObject();
    try {
      Repository repository =
        pluginRegistry.loadClass( RepositoryPluginType.class, repositoryMeta.getId(), Repository.class );
      repository.init( repositoryMeta );
      repository.connect( username, password );
      repositoryModel.setConnectedRepository( repositoryMeta.getName() );
      if ( spoon != null ) {
        spoon.setRepository( repository );
      }
      fireListeners();
      jsonObject.put( "success", true );
    } catch ( KettleException ke ) {
      if ( ke.getMessage().contains( ERROR_401 ) ) {
        jsonObject.put( ERROR_MESSAGE, BaseMessages.getString( PKG, "RepositoryConnection.Error.InvalidCredentials" ) );
      } else {
        jsonObject.put( ERROR_MESSAGE, BaseMessages.getString( PKG, "RepositoryConnection.Error.InvalidServer" ) );
      }
      jsonObject.put( "success", false );
      log.logError( "Unable to connect to repository", ke );
    }
    return jsonObject.toString();
  }

  public boolean deleteRepository( String name ) {
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( name );
    int index = repositoriesMeta.indexOfRepository( repositoryMeta );
    if ( index != -1 ) {
      if ( spoon != null && spoon.getRepositoryName() != null && spoon.getRepositoryName()
        .equals( repositoryMeta.getName() ) ) {
        disconnect();
      }
      repositoriesMeta.removeRepository( index );
      save();
    }
    return true;
  }

  public void addDatabase( DatabaseMeta databaseMeta ) {
    if ( databaseMeta != null ) {
      repositoriesMeta.addDatabase( databaseMeta );
      save();
    }
  }

  public boolean setDefaultRepository( String name ) {
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( name );
    if ( repositoryMeta != null ) {
      for ( int i = 0; i < repositoriesMeta.nrRepositories(); i++ ) {
        repositoriesMeta.getRepository( i ).setDefault( false );
      }
      repositoryMeta.setDefault( true );
    }
    try {
      repositoriesMeta.writeData();
    } catch ( KettleException ke ) {
      log.logError( "Unable to set default repository", ke );
    }
    return true;
  }

  public String getDefaultUrl() {
    ResourceBundle resourceBundle = PropertyResourceBundle.getBundle( PKG.getPackage().getName() + ".plugin" );
    return resourceBundle.getString( DEFAULT_URL );
  }

  public void setCurrentRepository( RepositoryMeta repositoryMeta ) {
    repositoryModel.setCurrentRepositoryMeta( repositoryMeta );
  }

  public RepositoryMeta getRepositoryMeta( String name ) {
    return repositoriesMeta.findRepository( name );
  }

  public void disconnect() {
    repositoryModel.setConnectedRepository( null );
    spoon.closeRepository();
    for ( RepositoryListener listener : listeners ) {
      fireListeners();
    }
  }

  public void save() {
    try {
      repositoriesMeta.writeData();
      repositoryModel.setRepositories( getRepositoryList() );
      fireListeners();
    } catch ( KettleException ke ) {
      log.logError( "Unable to write to repositories", ke );
    }
  }

  public void fireListeners() {
    for ( RepositoryListener listener : listeners ) {
      listener.change();
    }
  }

  public void addListener( RepositoryListener repositoryListener ) {
    listeners.add( repositoryListener );
  }

  public interface RepositoryListener {
    void change();
  }
}
