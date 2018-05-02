package org.pentaho.di.quicksearch.service;

import org.pentaho.di.quicksearch.result.SearchResult;

import java.util.List;

/**
 * Created by bmorrise on 4/2/18.
 */
public interface ResponseListener {
  void call( List<SearchResult> searchResults );
}
