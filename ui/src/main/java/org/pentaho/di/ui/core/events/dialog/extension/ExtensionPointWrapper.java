package org.pentaho.di.ui.core.events.dialog.extension;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;

public interface ExtensionPointWrapper {
  void callExtensionPoint( final LogChannelInterface log, final String id, final Object object ) throws
    KettleException;
}
