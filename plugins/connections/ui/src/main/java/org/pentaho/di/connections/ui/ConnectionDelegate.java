package org.pentaho.di.connections.ui;

import org.eclipse.swt.SWT;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/4/19.
 */
public class ConnectionDelegate {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  private static final int WIDTH = 630;
  private static final int HEIGHT = 630;

  public void openDialog() {
    ConnectionDialog connectionDialog = new ConnectionDialog( spoonSupplier.get().getShell(), WIDTH, HEIGHT );
    connectionDialog.open( "New VFS Connection" );
  }

  public void openDialog( String label ) {
    ConnectionDialog connectionDialog = new ConnectionDialog( spoonSupplier.get().getShell(), WIDTH, HEIGHT );
    connectionDialog.open( "Edit VFS Connection", label );
  }

  public void delete( String label ) {
    ConnectionDeleteDialog connectionDeleteDialog = new ConnectionDeleteDialog( spoonSupplier.get().getShell() );
    if ( connectionDeleteDialog.open( label ) == SWT.YES ) {
      ConnectionManager connectionManager = ConnectionManager.getInstance();
      connectionManager.delete( label );
      spoonSupplier.get().getShell().getDisplay().asyncExec( () -> spoonSupplier.get().refreshTree(
        ConnectionFolderProvider.STRING_VFS_CONNECTIONS ) );
    }
  }

}
