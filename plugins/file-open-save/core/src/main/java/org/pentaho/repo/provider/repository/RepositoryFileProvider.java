package org.pentaho.repo.provider.repository;

import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.repo.controller.RepositoryBrowserController;
import org.pentaho.repo.provider.File;
import org.pentaho.repo.provider.FileProvider;
import org.pentaho.repo.provider.repository.model.RepositoryDirectory;
import org.pentaho.repo.provider.repository.model.RepositoryTree;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/14/19.
 */
public class RepositoryFileProvider implements FileProvider {

  private RepositoryBrowserController repositoryBrowserController;

  public static final String NAME = "Pentaho Repository";
  public static final String TYPE = "PENTAHO_REPOSITORY";

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public RepositoryFileProvider( RepositoryBrowserController repositoryBrowserController ) {
    this.repositoryBrowserController = repositoryBrowserController;
  }

  @Override public String getName() {
    return NAME;
  }

  @Override public String getType() { return TYPE; }

  @Override public RepositoryTree getTree() {
    RepositoryTree repositoryTree = new RepositoryTree( NAME );
    repositoryTree.setChildren( repositoryBrowserController.loadDirectoryTree().getChildren() );
    return repositoryTree;
  }

  @Override public List<File> getFiles( String name, String path ) {
    return null;
  }

  @Override public boolean isAvailable() {
    return spoonSupplier.get() != null && spoonSupplier.get().rep != null;
  }
}
