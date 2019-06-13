package org.pentaho.di.connections.common;

import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.ConnectionProvider;

import java.util.List;

public class TestConnectionProvider implements ConnectionProvider<TestConnectionDetails> {

  private ConnectionManager connectionManager;

  public TestConnectionProvider( ConnectionManager connectionManager ) {
    this.connectionManager = connectionManager;
  }

  public static final String NAME = "Test";
  public static final String SCHEME = "test";

  @Override public String getName() {
    return NAME;
  }

  @Override public String getKey() {
    return SCHEME;
  }

  @Override public Class<TestConnectionDetails> getClassType() {
    return TestConnectionDetails.class;
  }

  @Override public List<String> getNames() {
    return connectionManager.getNamesByType( getClass() );
  }

  @SuppressWarnings( "unchecked" )
  @Override public List<TestConnectionDetails> getConnectionDetails() {
    return (List<TestConnectionDetails>) connectionManager.getConnectionDetailsByScheme( getKey() );
  }

  @Override public boolean test( TestConnectionDetails connectionDetails ) {
    return true;
  }

  @Override public TestConnectionDetails prepare( TestConnectionDetails connectionDetails ) {
    return connectionDetails;
  }
}
