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

package org.pentaho.di.ui.repo.handler;

import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.RepositoryDialogFactory;
import org.pentaho.di.ui.repo.controller.RepositoryController;
import org.pentaho.di.ui.repo.model.RepositoryModel;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.Binding;
import org.pentaho.ui.xul.binding.BindingConvertor;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

/**
 * Created by bmorrise on 5/19/16.
 */
public class RepositoryMenuHandler extends AbstractXulEventHandler {

  private static LogChannelInterface log =
    KettleLogStore.getLogChannelInterfaceFactory().create( RepositoryController.class );

  public static String HANDLER_NAME = "repositoryMenuHandler";

  private RepositoryController repositoryController;
  private RepositoryModel repositoryModel;
  private RepositoryDialogFactory repositoryDialogFactory;
  private BindingFactory bindingFactory = new DefaultBindingFactory();

  public RepositoryMenuHandler( RepositoryController repositoryController,
                                RepositoryModel repositoryModel, RepositoryDialogFactory repositoryDialogFactory ) {
    this.repositoryController = repositoryController;
    this.repositoryModel = repositoryModel;
    this.repositoryDialogFactory = repositoryDialogFactory;
  }

  public RepositoryMenuHandler( RepositoryController repositoryController, RepositoryModel repositoryModel ) {
    this( repositoryController, repositoryModel,
      new RepositoryDialogFactory( Spoon.getInstance().getShell(), repositoryController ) );
  }

  @Override public void setXulDomContainer( XulDomContainer xulDomContainer ) {
    super.setXulDomContainer( xulDomContainer );

    bindingFactory.setDocument( xulDomContainer.getDocumentRoot() );
    bindingFactory.setBindingType( Binding.Type.ONE_WAY );
    Binding itemsBinding =
      bindingFactory.createBinding( repositoryModel, "repositoryItems", "repository-connect-popup", "elements" );
    Binding disconnectBinding =
      bindingFactory.createBinding( repositoryModel, "disconnectDisabled", "repository-disconnect", "disabled" );
    Binding connectMenuBinding =
      bindingFactory.createBinding( repositoryModel, "available", "repository-connect-menu", "visible" );
    Binding connectBinding =
      bindingFactory.createBinding( repositoryModel, "available", "repository-connect", "visible", not() );
    Binding repositoryManagerBinding =
      bindingFactory.createBinding( repositoryModel, "available", "repository-manager", "disabled", not() );

    try {
      itemsBinding.fireSourceChanged();
      disconnectBinding.fireSourceChanged();
      connectMenuBinding.fireSourceChanged();
      connectBinding.fireSourceChanged();
      repositoryManagerBinding.fireSourceChanged();
    } catch ( Exception e ) {
      log.logError( "Repository menu bindings failed to fire.", e );
    }
  }

  public void connect() {
    repositoryDialogFactory.getDialog().openCreation();
  }

  public void showRepositoryManager() {
    repositoryDialogFactory.getDialog().openManager();
    repositoryModel.fireBindings();
  }

  public void reset() {
    repositoryModel.fireBindings();
  }

  public void disconnect() {
    repositoryController.disconnect();
  }

  public void doLogin( String repository ) {
    RepositoryMeta repositoryMeta = repositoryController.getRepositoryMeta( repository );
    if ( repositoryMeta != null ) {
      if ( repositoryMeta.getId().equals( "KettleFileRepository" ) ) {
        repositoryController.connectToRepository( repositoryMeta );
      } else {
        repositoryModel.fireBindings();
        repositoryDialogFactory.getDialog().openLogin( repositoryMeta );
      }
    }
  }

  @Override
  public String getName() {
    return HANDLER_NAME;
  }

  public static BindingConvertor<Boolean, Boolean> not() {
    return new BindingConvertor<Boolean, Boolean>() {
      @Override public Boolean sourceToTarget( Boolean value ) {
        return !value;
      }

      @Override public Boolean targetToSource( Boolean value ) {
        return !value;
      }
    };
  }
}
