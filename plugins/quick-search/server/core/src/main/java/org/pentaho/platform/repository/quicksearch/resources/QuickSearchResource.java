package org.pentaho.platform.repository.quicksearch.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pentaho.platform.quicksearch.client.Result;
import org.pentaho.platform.repository.quicksearch.client.elasticsearch.ElasticSearchClient;
import org.pentaho.platform.repository.quicksearch.service.QuickSearchService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by bmorrise on 3/20/18.
 */
@Path("/quick-search/api/datasource")
public class QuickSearchResource {

  private QuickSearchService quickSearchService;
  private ObjectMapper objectMapper;

  public QuickSearchResource( QuickSearchService quickSearchService ) {
    this.objectMapper = new ObjectMapper();
    this.quickSearchService = quickSearchService;
  }

  @GET
  @Path("/search")
  @Produces({APPLICATION_JSON})
  public Response doSearch( @QueryParam("id") String id, @QueryParam("filename") String filename, @QueryParam
          ("extension") String extension ) throws Exception {
    Result result = quickSearchService.query( filename, extension );
    result.setId( id );
    return Response.ok( objectMapper.writeValueAsString( result ) ).build();
  }

  @GET
  @Path("/index")
  public Response doIndex() {
    quickSearchService.index();
    return Response.ok().build();
  }

  @GET
  @Path("/indexById")
  public Response doIndexById( @QueryParam("id") String id ) {
    quickSearchService.indexById( id );
    return Response.ok().build();
  }
}
