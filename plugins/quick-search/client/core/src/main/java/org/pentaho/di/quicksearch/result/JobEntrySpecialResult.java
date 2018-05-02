package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.spoon.job.JobGraph;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/17/18.
 */
public class JobEntrySpecialResult implements SearchResult {

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;
  private JobEntryCopy jobEntryCopy;
  private Image image;

  public JobEntrySpecialResult( JobEntryCopy jobEntryCopy, Image image ) {
    this.jobEntryCopy = jobEntryCopy;
    this.image = image;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return jobEntryCopy.getName();
  }

  @Override
  public String getDescription() {
    return jobEntryCopy.getDescription();
  }

  @Override
  public void execute() {
    JobGraph jobGraph = spoonSupplier.get().getActiveJobGraph();
    if ( jobGraph != null ) {
      jobGraph.addJobEntryToChain( jobEntryCopy.getName(), false );
    }
  }

  @Override
  public Image getImage() {
    return image;
  }
}
