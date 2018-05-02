package org.pentaho.di.quicksearch.plugin;

import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.quicksearch.ui.QuickSearchDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.SpoonLifecycleListener;
import org.pentaho.di.ui.spoon.SpoonPerspective;
import org.pentaho.di.ui.spoon.SpoonPlugin;
import org.pentaho.di.ui.spoon.SpoonPluginCategories;
import org.pentaho.di.ui.spoon.SpoonPluginInterface;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

import java.util.List;

/**
 * Created by bmorrise on 3/21/18.
 */
@SpoonPlugin(id = "quicksearch-plugin", image = "")
@SpoonPluginCategories({"spoon"})
public class QuickSearchPlugin extends AbstractXulEventHandler implements SpoonPluginInterface {

  private static final String SPOON_CATEGORY = "spoon";
  private QuickSearchDialog quickSearchDialog;
  final List<QuickSearchService> services;

  public QuickSearchPlugin( List<QuickSearchService> services ) {
    this.services = services;
  }

  @Override
  public void applyToContainer( String category, XulDomContainer xulDomContainer ) throws XulException {
    if ( category.equals( SPOON_CATEGORY ) ) {
      xulDomContainer.registerClassLoader( getClass().getClassLoader() );
      xulDomContainer.loadOverlay( "spoon_overlay.xul" );
      setName( "quickSearchMenuHandler" );
      xulDomContainer.addEventHandler( this );
      Spoon.getInstance().enableMenus();
    }
  }

  public void openQuickSearch() {
    if ( quickSearchDialog == null || quickSearchDialog.isDisposed() ) {
      quickSearchDialog = new QuickSearchDialog( Spoon.getInstance().getShell(), services );
      quickSearchDialog.open();
    }
  }

  @Override
  public SpoonLifecycleListener getLifecycleListener() {
    return null;
  }

  @Override
  public SpoonPerspective getPerspective() {
    return null;
  }

}
