package org.pentaho.di.quicksearch.plugin;

import org.pentaho.di.core.annotations.LifecyclePlugin;
import org.pentaho.di.core.lifecycle.LifeEventHandler;
import org.pentaho.di.core.lifecycle.LifecycleException;
import org.pentaho.di.core.lifecycle.LifecycleListener;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/2/18.
 */
@LifecyclePlugin(id = "QuickSearchLifecycleListener")
public class QuickSearchLifecycleListener implements LifecycleListener {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private final List<QuickSearchService> services;

  public QuickSearchLifecycleListener( List<QuickSearchService> services ) {
    this.services = services;
  }

  @Override
  public void onStart( LifeEventHandler lifeEventHandler ) throws LifecycleException {
    for ( QuickSearchService service : services ) {
      service.init();
    }
  }

  @Override
  public void onExit( LifeEventHandler lifeEventHandler ) throws LifecycleException {

  }
}
