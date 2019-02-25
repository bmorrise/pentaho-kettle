package org.pentaho.di.connections.vfs.providers.other;

import org.pentaho.di.connections.vfs.VFSConnectionDetails;
import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

/**
 * Created by bmorrise on 2/3/19.
 */
@MetaStoreElementType(
  name = "Other VFS Connection",
  description = "Defines the connection details for a generic vfs connection" )
public class OtherConnectionDetails implements VFSConnectionDetails {

  public static final String TYPE = "other";

  @MetaStoreAttribute
  private String protocol;

  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private String host;

  @MetaStoreAttribute
  private String port;

  @MetaStoreAttribute
  private String username;

  @MetaStoreAttribute
  private String password;

  @MetaStoreAttribute
  private String description;

  public String getHost() {
    return host;
  }

  public void setHost( String host ) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort( String port ) {
    this.port = port;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername( String username ) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol( String protocol ) {
    this.protocol = protocol;
  }

  @Override public String getDescription() {
    return description;
  }

  public void setDescription( String description ) {
    this.description = description;
  }
}
