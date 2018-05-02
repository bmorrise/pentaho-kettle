package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.extension.KettleExtensionPoint;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.ui.util.Images;
import org.pentaho.di.repository.RepositoryMeta;

/**
 * Created by bmorrise on 4/16/18.
 */
public class RepositoryResult implements SearchResult {

  private Image REPO_IMAGE = Images.getImage( RepositoryResult.class, "slave.svg", 24 );

  private RepositoryMeta repositoryMeta;

  public RepositoryResult( RepositoryMeta repositoryMeta ) {
    this.repositoryMeta = repositoryMeta;
  }

  @Override
  public String getId() {
    return null;
  }

  @Override
  public String getName() {
    return repositoryMeta.getName();
  }

  @Override
  public String getDescription() {
    return repositoryMeta.getName();
  }

  @Override
  public void execute() {
    try {
      ExtensionPointHandler.callExtensionPoint( null, KettleExtensionPoint.RequestLoginToRepository.id,
              repositoryMeta );
    } catch ( KettleException ignored ) {

    }
  }

  @Override
  public Image getImage() {
    return REPO_IMAGE;
  }
}
