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

package org.pentaho.di.ui.repo.model;

import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.handler.RepositoryMenuHandler;
import org.pentaho.di.ui.repo.ui.RepositoryMenuItem;
import org.pentaho.ui.xul.XulEventSourceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 5/20/16.
 */
public class RepositoryModel extends XulEventSourceAdapter {

  private RepositoriesMeta repositoriesMeta;
  private RepositoryMeta currentRepositoryMeta;
  private List<String> repositories = new ArrayList<>();
  private String connectedRepository;
  private String connectionName;

  public void setRepositories( List<String> repositories ) {
    this.repositories = repositories;
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
    firePropertyChange( "available", null, isAvailable() );
  }

  public List<String> getRepositories() {
    return repositories;
  }

  public List<RepositoryMenuItem> getRepositoryItems() {
    List<RepositoryMenuItem> repoMenuItems = new ArrayList<>();
    for ( String repository : repositories ) {
      String command = RepositoryMenuHandler.HANDLER_NAME + ".reset()";
      Boolean selected = false;
      if ( repository.equals( connectedRepository ) ) {
        selected = true;
      } else {
        command = RepositoryMenuHandler.HANDLER_NAME + ".doLogin(\"" + repository + "\")";
      }
      RepositoryMenuItem repoMenuItem = new RepositoryMenuItem( repository, command, "checkbox" );
      repoMenuItem.setSelected( selected );
      repoMenuItems.add( repoMenuItem );
    }
    return repoMenuItems;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName( String connectionName ) {
    this.connectionName = connectionName;
    firePropertyChange( "connectionName", null, connectionName );
  }

  public RepositoriesMeta getRepositoriesMeta() {
    return repositoriesMeta;
  }

  public void setRepositoriesMeta( RepositoriesMeta repositoriesMeta ) {
    this.repositoriesMeta = repositoriesMeta;
  }

  public RepositoryMeta getCurrentRepositoryMeta() {
    return currentRepositoryMeta;
  }

  public void setCurrentRepositoryMeta( RepositoryMeta currentRepositoryMeta ) {
    this.currentRepositoryMeta = currentRepositoryMeta;
  }

  public String getConnectedRepository() {
    return connectedRepository;
  }

  public Boolean isDisconnectDisabled() {
    return connectedRepository == null;
  }

  public Boolean isAvailable() {
    return repositories.size() > 0;
  }

  public void setConnectedRepository( String connectedRepository ) {
    this.connectedRepository = connectedRepository;
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
    firePropertyChange( "disconnectDisabled", null, isDisconnectDisabled() );
  }

  public void fireBindings() {
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
  }
}
