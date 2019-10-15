package org.pentaho.di.ui.core.events.dialog.extension;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.ui.core.FileDialogOperation;

public class SpoonOpenExtensionPointWrapper implements ExtensionPointWrapper {

  public SpoonOpenExtensionPointWrapper() {
    //empty constructor
  }

  @Override
  public void callExtensionPoint( LogChannelInterface log, String id, Object object ) throws KettleException {
    FileDialogOperation fileDialogOperation = (FileDialogOperation) object;
    ExtensionPointHandler.callExtensionPoint( log, id, fileDialogOperation );
  }
}
