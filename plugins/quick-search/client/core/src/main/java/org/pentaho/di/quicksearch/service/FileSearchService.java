package org.pentaho.di.quicksearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.result.SearchResults;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.quicksearch.result.FileResult;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.ui.spoon.Spoon;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Created by bmorrise on 3/22/18.
 */
public class FileSearchService implements QuickSearchService {

  private Class<?> PKG = FileSearchService.class;
  private static String SEARCH_ENDPOINT = "/plugin/quick-search/api/datasource/search";
  private static String INDEX_ENDPOINT = "/plugin/quick-search/api/datasource/indexById";
  private String DEFAULT_USER = "admin";
  private String DEFAULT_PASSWORD = "password";
  private String DEFAULT_URL = "http://localhost:8080/pentaho";
  private ObjectMapper objectMapper;
  private String latest;
  private CloseableHttpAsyncClient asyncClient;

  public FileSearchService() {
    objectMapper = new ObjectMapper();
    asyncClient = getClient();
  }

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  @Override
  public boolean isAvailable() {
    Repository repository = spoonSupplier.get().rep;
    return repository != null && repository.getRepositoryMeta().getId().equals( "PentahoEnterpriseRepository" );
  }

  @Override
  public String getLabel() {
    return BaseMessages.getString( PKG, "quicksearch.files.service.label" );
  }

  @Override
  public int rank() {
    return 10;
  }

  @Override
  public void init() {

  }

  public String buildUrl( String endpoint ) {
    RepositoryMeta repositoryMeta = spoonSupplier.get().rep.getRepositoryMeta();
    String url = DEFAULT_URL;
    try {
      Method method = repositoryMeta.getClass().getMethod( "getRepositoryLocation" );
      Object object = method.invoke( repositoryMeta );
      Method method1 = object.getClass().getMethod( "getUrl" );
      url = (String) method1.invoke( object );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    return url + endpoint;
  }

  public void indexById( String id, ResponseListener listener ) {
    try {
      HttpGet httpGet = new HttpGet( buildUrl( INDEX_ENDPOINT ) + "?id=" + URLEncoder.encode( id, "UTF-8" ) );
      CloseableHttpAsyncClient asyncClient = getClient();
      Future<HttpResponse> httpResponseFuture = asyncClient.execute( httpGet, null );
      HttpResponse httpResponse = httpResponseFuture.get();
      httpResponse.getEntity();
      listener.call( null );
      asyncClient.close();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  @Override
  public void search( String filename, SearchOptions searchOptions, ResponseListener listener ) {
    latest = UUID.randomUUID().toString();

    try {
      HttpGet httpGet = new HttpGet( buildUrl( SEARCH_ENDPOINT ) + "?filename=*" + URLEncoder.encode( filename, "UTF-8" ) +
              "*&extension=" + URLEncoder.encode( searchOptions.getExtension(), "UTF-8" ) + "&id=" + URLEncoder
              .encode( latest, "UTF-8" ) );

      asyncClient.execute( httpGet, new FutureCallback<HttpResponse>() {
        @Override
        public void completed( HttpResponse httpResponse ) {
          HttpEntity entity = httpResponse.getEntity();
          try {
            SearchResults searchResults = createFiles( IOUtils.toString( entity.getContent() ) );
            // Ignore anything that isn't the latest call
            if ( searchResults.getId().equals( latest ) ) {
              listener.call( searchResults.getSearchResults() );
            }
          } catch ( IOException e ) {
            listener.call( null );
          }
        }

        @Override
        public void failed( Exception e ) {
          e.printStackTrace();
        }

        @Override
        public void cancelled() {

        }
      });
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  private SearchResults createFiles( String json ) {
    SearchResults searchResults = new SearchResults();
    JSONParser jsonParser = new JSONParser();
    try {
      JSONObject jsonObject = (JSONObject) jsonParser.parse( json );
      String id = (String) jsonObject.get( "id" );
      searchResults.setId( id );

      List<SearchResult> files = new ArrayList<>();
      JSONArray jsonArray = (JSONArray) jsonObject.get( "fileList" );
      for ( Object fileJson : jsonArray ) {
        try {
          FileResult fileResult = objectMapper.readValue( ((JSONObject) fileJson).toJSONString(), FileResult.class );
          files.add( fileResult );
        } catch ( IOException e ) {
          // Do nothing
        }
      }
      searchResults.setSearchResults( files );
    } catch ( ParseException e ) {
      //Ignore
    }
    return searchResults;
  }

  protected CloseableHttpAsyncClient getClient() {
    String username = DEFAULT_USER;
    String password = DEFAULT_PASSWORD;
    if ( spoonSupplier.get() != null && spoonSupplier.get().rep != null ) {
      Repository repository = spoonSupplier.get().rep;
      username = repository.getUserInfo().getName();
      password = repository.getUserInfo().getPassword();
    }

    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    UsernamePasswordCredentials credentials = new UsernamePasswordCredentials( username, password );
    credentialsProvider.setCredentials( AuthScope.ANY, credentials );

    CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create().setDefaultCredentialsProvider(
            credentialsProvider ).build();
    httpClient.start();

    return httpClient;
  }

  @Override
  public void close() {
    try {
      asyncClient.close();
    } catch ( IOException e ) {
      // Ignore
    }
  }
}
