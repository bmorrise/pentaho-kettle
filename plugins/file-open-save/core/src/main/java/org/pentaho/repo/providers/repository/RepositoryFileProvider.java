/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.repo.providers.repository;

import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.repo.controller.RepositoryBrowserController;
import org.pentaho.repo.providers.File;
import org.pentaho.repo.providers.FileProvider;
import org.pentaho.repo.providers.Properties;
import org.pentaho.repo.providers.Result;
import org.pentaho.repo.providers.repository.model.RepositoryTree;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class RepositoryFileProvider implements FileProvider {

  private RepositoryBrowserController repositoryBrowserController;

  public static final String NAME = "Pentaho Repository";
  public static final String TYPE = "repository";

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public RepositoryFileProvider( RepositoryBrowserController repositoryBrowserController ) {
    this.repositoryBrowserController = repositoryBrowserController;
  }

  @Override public String getName() {
    return NAME;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public RepositoryTree getTree() {
    RepositoryTree repositoryTree = new RepositoryTree( NAME );
    repositoryTree.setChildren( repositoryBrowserController.loadDirectoryTree().getChildren() );
    return repositoryTree;
  }

  @Override public List<File> getFiles( String path, String filters, Properties properties ) {
    return null;
  }

  @Override public boolean isAvailable() {
    return spoonSupplier.get() != null && spoonSupplier.get().rep != null;
  }

  @Override public Result deleteFiles( List<String> paths, Properties properties ) {
    return null;
  }

  @Override public Result addFolder( String path, Properties properties ) {
    return null;
  }

  @Override public Result renameFile( String path, String newPath, String overwrite, Properties properties ) {
    return null;
  }

  @Override public Result moveFiles( List<String> paths, String newPath, boolean overwrite, Properties properties ) {
    return null;
  }

  @Override public InputStream readFile( String path, Properties properties ) {
    return null;
  }

  @Override public boolean writeFile( InputStream file, String path, Properties properties, boolean overwrite ) {
    return false;
  }
}
