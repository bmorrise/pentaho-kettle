package org.pentaho.platform.quicksearch.client;

import org.pentaho.platform.quicksearch.client.model.Request;

/**
 * Created by bmorrise on 5/2/18.
 */
public interface SearchClient {
  Result search( Request request );
  void indexById( String id );
  void index();
}
