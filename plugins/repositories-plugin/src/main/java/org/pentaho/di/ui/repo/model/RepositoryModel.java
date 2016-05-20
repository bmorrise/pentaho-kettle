package org.pentaho.di.ui.repo.model;

import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.repo.handler.RepositoryMenuHandler;
import org.pentaho.di.ui.repo.ui.RepoMenuItem;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.ui.xul.XulEventSourceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 5/20/16.
 */
public class RepositoryModel extends XulEventSourceAdapter {

  private RepositoriesMeta repositoriesMeta;
  private RepositoryMeta currentRepositoryMeta;
  private List<String> repositories = new ArrayList<>();
  private String connectedRepository;
  private Spoon spoon;

  public RepositoryModel() {
    this( Spoon.getInstance() );
  }

  public RepositoryModel( Spoon spoon ) {
    this.spoon = spoon;
  }

  public void setRepositories( List<String> repositories ) {
    this.repositories = repositories;
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
    firePropertyChange( "available", null, isAvailable() );
  }

  public List<String> getRepositories() {
    return repositories;
  }

  public List<RepoMenuItem> getRepositoryItems() {
    List<RepoMenuItem> repoMenuItems = new ArrayList<>();
    for ( String repository : repositories ) {
      String command = RepositoryMenuHandler.HANDLER_NAME + ".doNothing(\"" + repository + "\")";;
      Boolean selected = false;
      if ( repository.equals( connectedRepository ) ) {
        selected = true;
      } else {
        command = RepositoryMenuHandler.HANDLER_NAME + ".doLogin(\"" + repository + "\")";
      }
      RepoMenuItem repoMenuItem = new RepoMenuItem( repository, command );
      repoMenuItem.setSelected( selected );
      repoMenuItems.add( repoMenuItem );
    }
    repoMenuItems.add( new RepoMenuItem( null, null ) );
    repoMenuItems.add(
      new RepoMenuItem( "Repository Manager...", RepositoryMenuHandler.HANDLER_NAME + ".showRepositoryManager()" ) );
    return repoMenuItems;
  }

  public RepositoriesMeta getRepositoriesMeta() {
    return repositoriesMeta;
  }

  public void setRepositoriesMeta( RepositoriesMeta repositoriesMeta ) {
    this.repositoriesMeta = repositoriesMeta;
  }

  public RepositoryMeta getCurrentRepositoryMeta() {
    return currentRepositoryMeta;
  }

  public void setCurrentRepositoryMeta( RepositoryMeta currentRepositoryMeta ) {
    this.currentRepositoryMeta = currentRepositoryMeta;
  }

  public String getConnectedRepository() {
    return connectedRepository;
  }

  public Boolean isDisconnectDisabled() {
    return connectedRepository == null;
  }

  public Boolean isAvailable() {
    return repositories.size() > 0;
  }

  public void setConnectedRepository( String connectedRepository ) {
    this.connectedRepository = connectedRepository;
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
    firePropertyChange( "disconnectDisabled", null, isDisconnectDisabled() );
  }

  public void fireBindings() {
    firePropertyChange( "repositoryItems", null, getRepositoryItems() );
  }
}
