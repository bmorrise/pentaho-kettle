package org.pentaho.platform.repository.quicksearch.service;

import org.pentaho.platform.quicksearch.client.Result;
import org.pentaho.platform.quicksearch.client.SearchClient;
import org.pentaho.platform.quicksearch.client.model.Query;
import org.pentaho.platform.quicksearch.client.model.RegExp;
import org.pentaho.platform.quicksearch.client.model.Request;
import org.pentaho.platform.quicksearch.client.model.Sort;
import org.pentaho.platform.quicksearch.client.model.Wildcard;
import org.pentaho.platform.quicksearch.client.model.compound.Bool;
import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Must;

/**
 * Created by bmorrise on 3/21/18.
 */
public class QuickSearchService {

  private SearchClient client;

  public QuickSearchService( SearchClient client ) {
    this.client = client;
  }

  public Result query( String filename, String extension ) {

    Must.Builder mustBuilder = new Must.Builder()
            .occurrence( new Wildcard( "name", filename.toLowerCase() ) );
    if ( extension != null ) {
      mustBuilder.occurrence( new RegExp( "extension", extension ) );
    }

    Request request = new Request.Builder()
            .from( 0 )
            .size( 10 )
            .query( new Query.Builder()
                    .bool( new Bool.Builder()
                            .must( mustBuilder.build() )
                            .build() )
                    .build() )
            .sort( new Sort( "name", "asc" ) )
            .build();

    return client.search( request );
  }

  public void indexById( String id ) {
    client.indexById( id );
  }

  public void index() {
    client.index();
  }
}
