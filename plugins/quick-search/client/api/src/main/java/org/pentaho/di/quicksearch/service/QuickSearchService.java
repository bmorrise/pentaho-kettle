package org.pentaho.di.quicksearch.service;

/**
 * Created by bmorrise on 3/22/18.
 */
public interface QuickSearchService {
  void search( String term, SearchOptions searchOptions, ResponseListener responseListener );
  boolean isAvailable();
  String getLabel();
  void close();
  int rank();
  void init();
}
