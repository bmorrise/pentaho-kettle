package org.pentaho.platform.repository.quicksearch.client.elasticsearch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.RepositoryFileTree;
import org.pentaho.platform.api.repository2.unified.RepositoryRequest;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.quicksearch.client.Result;
import org.pentaho.platform.quicksearch.client.SearchClient;
import org.pentaho.platform.quicksearch.client.model.File;
import org.pentaho.platform.quicksearch.client.model.Match;
import org.pentaho.platform.quicksearch.client.model.ParamType;
import org.pentaho.platform.quicksearch.client.model.RegExp;
import org.pentaho.platform.quicksearch.client.model.Request;
import org.pentaho.platform.quicksearch.client.model.Sort;
import org.pentaho.platform.quicksearch.client.model.Wildcard;
import org.pentaho.platform.repository.quicksearch.util.FileUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bmorrise on 3/21/18.
 */
public class ElasticSearchClient implements SearchClient {

  public static final String HTTP = "http";
  private static String DEFAULT_HOST = "localhost";
  private static String DEFAULT_PORT = "9200";
  private static String SEARCH_ENDPOINT = "_search";

  private ObjectMapper objectMapper;
  private String host;
  private String port;
  private IUnifiedRepository repository;

  public ElasticSearchClient( String host, String port ) {
    this.host = host;
    this.port = port;
    init();
  }

  public ElasticSearchClient() {
    this( DEFAULT_HOST, DEFAULT_PORT );
  }

  private void init() {
    this.objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addSerializer( Match.class, new ParamTypeSerializer() );
    simpleModule.addSerializer( Wildcard.class, new ParamTypeSerializer() );
    simpleModule.addSerializer( Sort.class, new ParamTypeSerializer() );
    simpleModule.addSerializer( RegExp.class, new ParamTypeSerializer() );
    objectMapper.registerModule( simpleModule );

  }

  @Override
  public Result search( Request request ) {

    String json = "";
    try {
      json = objectMapper.writeValueAsString( request );
    } catch ( JsonProcessingException jpe ) {
      return null;
    }

    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) jsonParser.parse( post( SEARCH_ENDPOINT, new StringEntity( json ) ) );
    } catch ( UnsupportedEncodingException | ParseException uee ) {
      uee.printStackTrace();
    }

    JSONObject hits = jsonObject != null ? (JSONObject) jsonObject.get( "hits" ) : null;
    JSONArray hitsArray = hits != null ? (JSONArray) hits.get( "hits" ) : new JSONArray();

    List<File> fileList = new ArrayList<>();
    for ( Object hit : hitsArray ) {
      JSONObject source = (JSONObject) ((JSONObject) hit).get( "_source" );
      try {
        fileList.add( objectMapper.readValue( source.toJSONString(), File.class ) );
      } catch ( IOException ioe ) {
        ioe.printStackTrace();
      }
    }

    Result result = new Result();
    result.setFileList( fileList );

    return result;
  }

  class ParamTypeSerializer extends StdSerializer<ParamType> {

    private ParamTypeSerializer() {
      this( null );
    }

    private ParamTypeSerializer( Class<ParamType> t ) {
      super( t );
    }

    @Override
    public void serialize( ParamType paramType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider )
            throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeObjectField( paramType.getType(), Collections.singletonMap( paramType.getKey(), paramType
              .getValue() ) );
      jsonGenerator.writeEndObject();
    }
  }

  private String post( String endpoint, HttpEntity httpEntity ) {
    HttpPost httpPost = new HttpPost( buildUrl( "pentaho", SEARCH_ENDPOINT ) );
    httpPost.addHeader( new BasicHeader( "Content-Type", "application/json" ) );
    httpPost.setEntity( httpEntity );
    CloseableHttpClient httpClient = null;
    try {
      httpClient = HttpClients.createDefault();
      HttpResponse httpResponse = httpClient.execute( httpPost );
      return IOUtils.toString( httpResponse.getEntity().getContent() );
    } catch ( IOException e ) {
      e.printStackTrace();
    } finally {
      if ( httpClient != null ) {
        try {
          httpClient.close();
        } catch ( IOException e ) {
          // Ignore
        }
      }
    }
    return null;
  }

  private String buildUrl( String...path ) {
    return HTTP + "://" + host + ":" + port + "/" + StringUtils.join( path, "/" );
  }

  @Override
  public void indexById( String id ) {
    if ( repository == null ) {
      repository = PentahoSystem.get( IUnifiedRepository.class );
    }
    RepositoryFile repositoryFile = repository.getFileById( id );
    indexFile( repositoryFile );
  }

  @Override
  public void index() {
    if ( repository == null ) {
      repository = PentahoSystem.get( IUnifiedRepository.class );
    }
    RepositoryRequest repoRequest = new RepositoryRequest();
    repoRequest.setPath( "/" );
    repoRequest.setChildNodeFilter( "*" );
    repoRequest.setDepth( -1 );
    repoRequest.setShowHidden( true );
    repoRequest.setIncludeSystemFolders( false );
    repoRequest.setIncludeAcls( false );
    repoRequest.setTypes( RepositoryRequest.FILES_TYPE_FILTER.FILES_FOLDERS );

    RepositoryFileTree fileTree = repository.getTree( repoRequest );

    loadFiles( fileTree );
  }

  private void loadFiles( RepositoryFileTree repositoryFileTree ) {
    for ( RepositoryFileTree child : repositoryFileTree.getChildren() ) {
      RepositoryFile repositoryFile = child.getFile();
      indexFile( repositoryFile );
      loadFiles( child );
    }
  }

  private void indexFile( RepositoryFile repositoryFile ) {
    File file = new File();
    file.setExtension( FileUtil.getExtension( repositoryFile.getName() ) );
    file.setId( repositoryFile.getId().toString() );
    file.setPath( repositoryFile.getPath() );
    file.setType( repositoryFile.isFolder() ? File.DIRECTORY : File.FILE );
    file.setName( repositoryFile.getName() );

    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String json = objectMapper.writeValueAsString( file );
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpPut httpPut = new HttpPut( "http://localhost:9200/pentaho/file/" + repositoryFile.getId().toString() );
      httpPut.addHeader( new BasicHeader( "Content-Type", "application/json" ) );
      httpPut.setEntity( new StringEntity( json ) );
      httpClient.execute( httpPut );
      httpClient.close();
    } catch ( IOException e ) {
      e.printStackTrace();
      // Ignore do nothing
    }
  }
}
