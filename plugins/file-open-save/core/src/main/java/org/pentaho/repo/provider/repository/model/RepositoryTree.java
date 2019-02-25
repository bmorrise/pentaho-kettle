/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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

import org.pentaho.repo.provider.Tree;
import org.pentaho.repo.provider.repository.RepositoryFileProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 2/28/18.
 */
public class RepositoryTree implements Tree<RepositoryDirectory> {

  private String name;
  private boolean includeRoot;

  @Override public String getProvider() {
    return RepositoryFileProvider.TYPE;
  }

  public RepositoryTree( String name ) {
    this.name = name;
  }

  @Override public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  private List<RepositoryDirectory> children = new ArrayList<>();

  public List<RepositoryDirectory> getChildren() {
    return children;
  }

  public void setChildren( List<RepositoryDirectory> children ) {
    this.children = children;
  }

  @Override
  public void addChild( RepositoryDirectory repositoryDirectory ) {
    children.add( repositoryDirectory );
  }

  public boolean isIncludeRoot() {
    return includeRoot;
  }

  public void setIncludeRoot( boolean includeRoot ) {
    this.includeRoot = includeRoot;
  }
}