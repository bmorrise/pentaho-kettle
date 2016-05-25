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

package org.pentaho.di.ui.repo;

import org.eclipse.swt.widgets.ToolBar;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.repo.controller.RepositoryController;
import org.pentaho.di.ui.repo.handler.RepositoryMenuHandler;
import org.pentaho.di.ui.repo.model.RepositoryModel;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.containers.XulToolbar;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

@org.pentaho.di.ui.spoon.SpoonPlugin( id = "repositories-plugin", image = "" )
@SpoonPluginCategories( { "spoon" } )
public class RepositorySpoonPlugin implements SpoonPluginInterface {

  private static final String SPOON_CATEGORY = "spoon";

  private ResourceBundle createResourceBundle( final Class<?> packageClass ) {
    return new ResourceBundle() {
      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }

      @Override
      protected Object handleGetObject( String key ) {
        return BaseMessages.getString( packageClass, key );
      }
    };
  }

  @Override
  public void applyToContainer( String category, XulDomContainer container ) throws XulException {
    if ( category.equals( SPOON_CATEGORY ) ) {
      container.registerClassLoader( getClass().getClassLoader() );

      RepositoryModel repositoryModel = new RepositoryModel();
      RepositoryController repositoryController = new RepositoryController( repositoryModel );

      XulToolbar toolbar = (XulToolbar) container.getDocumentRoot().getElementById( "main-toolbar" );
      RepositoryConnectMenu repositoryConnectMenu =
        new RepositoryConnectMenu( container, (ToolBar) toolbar.getManagedObject(),
          repositoryController, repositoryModel );
      repositoryConnectMenu.render();

      container.loadOverlay( "org/pentaho/di/ui/repo/xul/repository_menu.xul",
        createResourceBundle( RepositorySpoonPlugin.class ) );
      container.addEventHandler( new RepositoryMenuHandler( repositoryController, repositoryModel ) );
    }
  }

  @Override
  public SpoonLifecycleListener getLifecycleListener() {
    return null;
  }

  @Override
  public SpoonPerspective getPerspective() {
    // no perspective
    return null;
  }

  // destroy-method in blueprint xml
  public void removeFromContainer() throws XulException {
    // create removal code
  }
}
