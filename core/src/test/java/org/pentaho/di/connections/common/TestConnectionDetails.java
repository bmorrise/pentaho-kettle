package org.pentaho.di.connections.common;

import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.metastore.persist.MetaStoreElementType;

@MetaStoreElementType(
  name = "Test VFS Connection",
  description = "Defines the connection details for a test vfs connection" )
public class TestConnectionDetails implements ConnectionDetails {

  private static String TYPE = "test";

  private String name;
  private String description;

  @Override public String getName() {
    return name;
  }

  @Override public void setName( String name ) {
    this.name = name;
  }

  @Override public String getType() {
    return TYPE;
  }

  @Override public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }
}