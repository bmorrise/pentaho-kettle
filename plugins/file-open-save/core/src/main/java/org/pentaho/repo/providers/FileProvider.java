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

package org.pentaho.repo.providers;

import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/**
 * Created by bmorrise on 2/14/19.
 */
public interface FileProvider {
  String getName();

  String getType();

  boolean isAvailable();

  Tree getTree();

  List<? extends File> getFiles( String path, String filters, Properties properties );

  Result deleteFiles( List<String> paths, Properties properties );

  Result addFolder( String path, Properties properties );

  Result renameFile( String path, String newPath, String overwrite, Properties properties );

  Result moveFiles( List<String> paths, String newPath, boolean overwrite, Properties properties );

  InputStream readFile( String path, Properties properties );

  boolean writeFile( InputStream file, String path, Properties properties, boolean overwrite )
    throws FileAlreadyExistsException;
}
