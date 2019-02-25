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

import org.pentaho.repo.providers.File;
import org.pentaho.repo.providers.FileProvider;
import org.pentaho.repo.providers.InvalidFileProviderException;
import org.pentaho.repo.providers.Properties;
import org.pentaho.repo.providers.Result;
import org.pentaho.repo.providers.Tree;
import org.pentaho.repo.providers.processor.Processor;

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
  private Processor processor;

  public FileBrowserController( List<FileProvider> fileProviders, Processor processor ) {
    this.fileProviders = fileProviders;
    this.processor = processor;
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

  public Result renameFile( String type, String path, String newPath, Properties properties ) {
    try {
      return getFileProvider( type ).renameFile( path, newPath, "", properties );
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

  public Result moveFiles( String type, List<String> paths, String newPath, Properties properties ) {
    try {
      return getFileProvider( type ).moveFiles( paths, newPath, false, properties );
    } catch ( InvalidFileProviderException e ) {
      return null;
    }
  }

  public Result copyFiles( String fromProvider, String toProvider, List<String> paths, String newPath,
                           Properties properties, boolean overwrite ) {

    Result.Status status = Result.Status.SUCCESS;

    List<String> copiedFiles = new ArrayList<>();
    FileProvider fromFileProvider = null;
    FileProvider toFileProvider = null;
    try {
      fromFileProvider = getFileProvider( fromProvider );
      toFileProvider = getFileProvider( toProvider );
      for ( String path : paths ) {
        String fileName = path.substring( path.lastIndexOf( "/" ), path.length() );
        String copyPath = newPath + "/" + fileName;
        try ( InputStream inputStream = fromFileProvider
          .readFile( path, Properties.create( "connection", properties.getString( "fromConnection" ) ) ) ) {
          toFileProvider.writeFile( inputStream, copyPath,
            Properties.create( "connection", properties.getString( "toConnection" ) ), overwrite );
          copiedFiles.add( path );
        } catch ( IOException e ) {
          status = Result.Status.FILE_COLLISION;
        }
      }
    } catch ( InvalidFileProviderException ignored ) {
      // Don't add it to the list
    }
    return new Result( status, "Copy files complete", copiedFiles );
  }

  public Result getStatus( String uuid ) {
    return processor.getStatus( uuid );
  }
}
