package org.pentaho.di.ui.repo.handler;

import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.RepositoryDialog;
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

  public static String HANDLER_NAME = "repoMenuController";

  private Spoon spoon;
  private RepositoryController repositoryController;
  private RepositoryModel repositoryModel;
  private BindingFactory bindingFactory = new DefaultBindingFactory();

  public RepositoryMenuHandler( Spoon spoon, RepositoryController repositoryController,
                                RepositoryModel repositoryModel ) {
    this.spoon = spoon;
    this.repositoryController = repositoryController;
    this.repositoryModel = repositoryModel;
  }

  public RepositoryMenuHandler( RepositoryController repositoryController, RepositoryModel repositoryModel ) {
    this( Spoon.getInstance(), repositoryController, repositoryModel );
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
    try {
      itemsBinding.fireSourceChanged();
      disconnectBinding.fireSourceChanged();
      connectMenuBinding.fireSourceChanged();
      connectBinding.fireSourceChanged();
    } catch ( Exception e ) {
      System.out.println( "Firing the binding didn't work" );
    }
  }

  public void connect() {
    RepositoryDialog repositoryDialog = new RepositoryDialog( spoon.getShell(), repositoryController );
    repositoryDialog.openCreation();
  }

  public void showRepositoryManager() {
    RepositoryDialog repositoryDialog = new RepositoryDialog( spoon.getShell(), repositoryController );
    repositoryDialog.openManager();
    repositoryModel.fireBindings();
  }

  public void disconnect() {
    repositoryController.disconnect();
  }

  public void doNothing() {
    repositoryModel.fireBindings();
  }

  public void doLogin( String repository ) {
    RepositoryMeta repositoryMeta = repositoryController.getRepositoryMeta( repository );
    if ( repositoryMeta != null ) {
      if ( repositoryMeta.getId().equals( "KettleFileRepository" ) ) {
        repositoryController.connectToRepository( repositoryMeta );
      } else {
        repositoryModel.fireBindings();
        new RepositoryDialog( spoon.getShell(), repositoryController ).openLogin( repositoryMeta );
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
