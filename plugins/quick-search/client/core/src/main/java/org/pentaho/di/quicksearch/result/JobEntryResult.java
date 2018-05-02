package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.quicksearch.ui.util.Images;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/2/18.
 */
public class JobEntryResult implements SearchResult {
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private PluginInterface pluginInterface;

  public JobEntryResult( PluginInterface pluginInterface ) {
    this.pluginInterface = pluginInterface;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return pluginInterface.getName();
  }

  @Override
  public String getDescription() {
    return pluginInterface.getDescription();
  }

  @Override
  public void execute() {
    JobGraph jobGraph = spoonSupplier.get().getActiveJobGraph();
    if ( jobGraph != null ) {
      jobGraph.addJobEntryToChain( pluginInterface.getName(), false );
    }
  }

  @Override
  public Image getImage() {
    ClassLoader classLoader = null;
    try {
      classLoader = PluginRegistry.getInstance().getClassLoader( pluginInterface );
    } catch ( KettlePluginException e ) {
      // Just let it fail
    }
    return Images.getImage( classLoader, pluginInterface.getImageFile(), 24 );
  }
}
