package org.pentaho.di.quicksearch.plugin;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.quicksearch.service.FileSearchService;

/**
 * Created by bmorrise on 3/23/18.
 */
@ExtensionPoint(
        id = "QuickSearchExtensionPoint",
        extensionPointId = "TransImportAfterSaveToRepo",
        description = "Trigger a indexing of a file"
)
public class QuickSearchExtensionPoint implements ExtensionPointInterface {
  private FileSearchService service;

  public QuickSearchExtensionPoint( FileSearchService service ) {
    this.service = service;
  }

  @Override
  public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {
    if ( !( o instanceof TransMeta ) ) {
      return;
    }

    TransMeta transMeta = (TransMeta) o;
    service.indexById( transMeta.getObjectId().getId(), object -> System.out.println( "Indexed" ) );
  }
}
