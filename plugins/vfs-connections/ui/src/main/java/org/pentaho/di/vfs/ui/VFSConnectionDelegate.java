package org.pentaho.di.vfs.ui;

import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.vfs.VFSConnectionManager;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/4/19.
 */
public class VFSConnectionDelegate {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  private static final int WIDTH = 630;
  private static final int HEIGHT = 630;

  public void openDialog() {
    VFSConnectionDialog vfsConnectionDialog = new VFSConnectionDialog( spoonSupplier.get().getShell(), WIDTH, HEIGHT );
    vfsConnectionDialog.open( "New VFS Connection" );
  }

  public void openDialog( String label ) {
    VFSConnectionDialog vfsConnectionDialog = new VFSConnectionDialog( spoonSupplier.get().getShell(), WIDTH, HEIGHT );
    vfsConnectionDialog.open( "New VFS Connection", label );
  }

  public void delete( String label ) {
    VFSConnectionManager vfsConnectionManager = VFSConnectionManager.getInstance();
    vfsConnectionManager.delete( label );
    spoonSupplier.get().getShell().getDisplay().asyncExec( () -> spoonSupplier.get().refreshTree(
            VFSConnectionFolderProvider.STRING_VFS_CONNECTIONS ) );
  }

}
