/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.vfs.ui;

import org.pentaho.di.core.annotations.LifecyclePlugin;
import org.pentaho.di.core.lifecycle.LifeEventHandler;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.lifecycle.LifecycleListener;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.vfs.VFSConnectionManager;
import org.pentaho.di.vfs.providers.other.OtherConnectionDetailsProvider;
import org.pentaho.osgi.metastore.locator.api.MetastoreLocator;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 7/6/18.
 */
@LifecyclePlugin( id = "VFSConnectionLifecycleListener" )
public class VFSConnectionLifecycleListener implements LifecycleListener {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private Supplier<VFSConnectionManager> connectionManagerSupplier = VFSConnectionManager::getInstance;
  private MetastoreLocator metastoreLocator;

  public VFSConnectionLifecycleListener( MetastoreLocator metastoreLocator ) {
    this.metastoreLocator = metastoreLocator;
  }

  @Override
  public void onStart( LifeEventHandler handler ) throws LifecycleException {
    Spoon spoon = spoonSupplier.get();
    if ( spoon != null ) {
      spoon.getTreeManager().addTreeProvider( Spoon.STRING_TRANSFORMATIONS, new VFSConnectionFolderProvider( metastoreLocator ) );
      spoon.getTreeManager().addTreeProvider( Spoon.STRING_JOBS, new VFSConnectionFolderProvider( metastoreLocator ) );
    }
    connectionManagerSupplier.get().addVFSConnectionProvider( "other", new OtherConnectionDetailsProvider() );
  }

  @Override
  public void onExit( LifeEventHandler handler ) throws LifecycleException {

  }
}
