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

package org.pentaho.repo.controller;

import org.pentaho.repo.api.providers.File;
import org.pentaho.repo.api.providers.FileProvider;
import org.pentaho.repo.api.providers.exception.InvalidFileProviderException;
import org.pentaho.repo.api.providers.Properties;
import org.pentaho.repo.api.providers.Result;
import org.pentaho.repo.api.providers.Tree;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public class FileBrowserController {

  private List<FileProvider> fileProviders = new ArrayList<>();

  public FileBrowserController( List<FileProvider> fileProviders ) {
    this.fileProviders = fileProviders;
  }

  private FileProvider getFileProvider( String type ) throws InvalidFileProviderException {
    return fileProviders.stream().filter( fileProvider1 ->
      fileProvider1.getType().equalsIgnoreCase( type ) && fileProvider1.isAvailable() )
      .findFirst()
      .orElseThrow( InvalidFileProviderException::new );
  }

  public List<Tree> load() {
    List<Tree> trees = new ArrayList<>();
    for ( FileProvider fileProvider : fileProviders ) {
      if ( fileProvider.isAvailable() ) {
        trees.add( fileProvider.getTree() );
      }
    }
    return trees;
  }

  public List<? extends File> getFiles( String type, String path, String filters, Properties properties ) {
    try {
      return getFileProvider( type ).getFiles( path, filters, properties );
    } catch ( InvalidFileProviderException e ) {
      return Collections.emptyList();
    }
  }

  public Result deleteFiles( String type, List<String> paths, Properties properties ) {
    try {
      return getFileProvider( type ).deleteFiles( paths, properties );
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  public Result getNewName( String type, String path, Properties properties ) {
    try {
      return getFileProvider( type ).getNewName( path, properties );
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  public Result addFolder( String type, String path, Properties properties ) {
    try {
      return getFileProvider( type ).addFolder( path, properties );
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  public Result fileExists( String provider, String path, Properties properties ) {
    try {
      return getFileProvider( provider ).fileExists( path, properties );
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  // TODO: If from the same provider run the native copy
  public Result renameFile( String fromProvider, String toProvider, String path, String newPath,
                           boolean overwrite, Properties properties ) {
    try {
      FileProvider fileProvider = getFileProvider( fromProvider );
      boolean isSame = fileProvider.isSame( toProvider, properties );
      if ( isSame ) {
        // TODO: This shouldn't be in here, nor should the other one a few lines down
        properties.put( "connection", properties.getString( "fromConnection" ) );
        return fileProvider.renameFile( path, newPath, overwrite, properties );
      } else {
        return renameBetweenProviders( fromProvider, toProvider, path, newPath, properties, overwrite );
      }
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  // TODO: If from the same provider run the native copy
  public Result copyFile( String fromProvider, String toProvider, String path, String newPath,
                           boolean overwrite, Properties properties ) {
    try {
      FileProvider fileProvider = getFileProvider( fromProvider );
      boolean isSame = fileProvider.isSame( toProvider, properties );
      if ( isSame ) {
        // TODO: This shouldn't be in here, nor should the other one a few lines down
        properties.put( "connection", properties.getString( "fromConnection" ) );
        return fileProvider.copyFile( path, newPath, overwrite, properties );
      } else {
        return copyBetweenProviders( fromProvider, toProvider, path, newPath, properties, overwrite );
      }
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  public Result renameBetweenProviders( String fromProvider, String toProvider, String path, String newPath,
                                      Properties properties, boolean overwrite ) {
    return null;
  }

  public Result copyBetweenProviders( String fromProvider, String toProvider, String path, String newPath,
                                      Properties properties, boolean overwrite ) {
    Result.Status status = Result.Status.SUCCESS;
    List<String> copiedFiles = new ArrayList<>();
    FileProvider fromFileProvider = null;
    FileProvider toFileProvider = null;
    try {
      fromFileProvider = getFileProvider( fromProvider );
      toFileProvider = getFileProvider( toProvider );
      try ( InputStream inputStream = fromFileProvider
        .readFile( path, Properties.create( "connection", properties.getString( "fromConnection" ) ) ) ) {
        toFileProvider.writeFile( inputStream, newPath,
          Properties.create( "connection", properties.getString( "toConnection" ) ), overwrite );
        copiedFiles.add( path );
      } catch ( IOException e ) {
        status = Result.Status.FILE_COLLISION;
      }
    } catch ( InvalidFileProviderException ignored ) {
      // Don't add it to the list
    }
    return new Result( status, "Copy files complete", copiedFiles );
  }
}
