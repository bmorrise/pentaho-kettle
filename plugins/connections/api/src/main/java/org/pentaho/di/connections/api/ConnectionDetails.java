package org.pentaho.di.connections.api;

/**
 * Created by bmorrise on 2/12/19.
 */
public interface ConnectionDetails {
  String getName();

  void setName( String name );

  String getType();
}
