package org.pentaho.di.connections;

/**
 * Created by bmorrise on 2/13/19.
 */
public interface ConnectionDetails {
  String getName();

  void setName( String name );

  String getType();

  String getDescription();
}
