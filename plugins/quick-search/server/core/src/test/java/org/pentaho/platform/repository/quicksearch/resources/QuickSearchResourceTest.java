package org.pentaho.platform.repository.quicksearch.resources;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.platform.repository.quicksearch.client.elasticsearch.ElasticSearchClient;
import org.pentaho.platform.repository.quicksearch.service.QuickSearchService;

import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by bmorrise on 3/20/18.
 */
public class QuickSearchResourceTest {

  private QuickSearchResource quickSearchResource;

  @Before
  public void setup() {
    QuickSearchService quickSearchService = new QuickSearchService( new ElasticSearchClient() );
    quickSearchResource = new QuickSearchResource( quickSearchService );
  }

  @Test
  public void testSearch() throws Exception {
    Response response = quickSearchResource.doSearch( UUID.randomUUID().toString(),"Trans", "ktr" );
    System.out.println( response.getEntity() );
  }

}
