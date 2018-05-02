package org.pentaho.di.quicksearch.result;

import java.util.List;

/**
 * Created by bmorrise on 4/2/18.
 */
public class SearchResults {
  private String id;
  private List<SearchResult> searchResults;

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public List<SearchResult> getSearchResults() {
    return searchResults;
  }

  public void setSearchResults( List<SearchResult> searchResults ) {
    this.searchResults = searchResults;
  }
}
