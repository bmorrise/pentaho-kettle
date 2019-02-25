/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2018 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.repo.provider.repository.model;

import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.repo.provider.Directory;
import org.pentaho.repo.provider.repository.RepositoryFileProvider;

/**
 * Created by bmorrise on 5/16/17.
 */
public class RepositoryDirectory extends RepositoryObject implements Directory {

  public static final String DIRECTORY = "folder";
  private String root;

  public RepositoryDirectory() {
    this.setHasChildren( true );
  }

  @Override public String getType() {
    return DIRECTORY;
  }

  public static RepositoryDirectory build( String parentPath,
                                           RepositoryDirectoryInterface repositoryDirectoryInterface ) {
    RepositoryDirectory repositoryDirectory = new RepositoryDirectory();
    repositoryDirectory.setParent( parentPath );
    repositoryDirectory.setName( repositoryDirectoryInterface.getName() );
    repositoryDirectory.setPath( repositoryDirectoryInterface.getPath() );
    repositoryDirectory.setObjectId( repositoryDirectoryInterface.getObjectId() );
    repositoryDirectory.setHidden( !repositoryDirectoryInterface.isVisible() );
    repositoryDirectory.setRoot( RepositoryFileProvider.NAME );

    return repositoryDirectory;
  }

  public static RepositoryDirectory build( String parentPath,
                                           org.pentaho.platform.api.repository2.unified.RepositoryFile repositoryFile,
                                           Boolean isAdmin ) {
    RepositoryDirectory repositoryDirectory = new RepositoryDirectory();
    repositoryDirectory.setParent( parentPath );
    repositoryDirectory.setName( repositoryFile.getName() );
    repositoryDirectory.setPath( repositoryFile.getPath() );
    repositoryDirectory.setObjectId( () -> (String) repositoryFile.getId() );
    repositoryDirectory.setHidden( repositoryFile.isHidden() && !isAdmin );
    repositoryDirectory.setDate( repositoryFile.getLastModifiedDate() != null ? repositoryFile.getLastModifiedDate()
      : repositoryFile.getCreatedDate() );
    repositoryDirectory.setRoot( RepositoryFileProvider.NAME );

    return repositoryDirectory;
  }

  @Override public String getRoot() {
    return root;
  }

  public void setRoot( String root ) {
    this.root = root;
  }
}
