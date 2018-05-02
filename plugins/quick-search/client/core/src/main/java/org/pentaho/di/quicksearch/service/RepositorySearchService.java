package org.pentaho.di.quicksearch.service;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.quicksearch.service.ResponseListener;
import org.pentaho.di.quicksearch.service.SearchOptions;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.quicksearch.result.RepositoryResult;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 4/16/18.
 */
public class RepositorySearchService implements QuickSearchService {

  private Class<?> PKG = RepositorySearchService.class;

  private RepositoriesMeta repositoriesMeta;
  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public RepositorySearchService() {
     repositoriesMeta = new RepositoriesMeta();
     try {
       repositoriesMeta.readData();
     } catch ( KettleException ignored ) {

     }
  }

  @Override
  public void search( String term, SearchOptions searchOptions, ResponseListener responseListener ) {
    int count = repositoriesMeta.nrRepositories();
    List<SearchResult> searchResults = new ArrayList<>();
    for ( int i = 0; i < count; i++ ) {
      RepositoryMeta repositoryMeta = repositoriesMeta.getRepository( i );
      if ( repositoryMeta.getName().toLowerCase().contains( term.toLowerCase() ) ) {
        searchResults.add( new RepositoryResult( repositoryMeta ) );
      }
    }
    responseListener.call( searchResults );
  }

  @Override
  public boolean isAvailable() {
    return spoonSupplier.get().rep == null;
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.repositories.service.label" );
  }

  @Override
  public void close() {

  }

  @Override
  public int rank() {
    return 5;
  }

  @Override
  public void init() {

  }
}
