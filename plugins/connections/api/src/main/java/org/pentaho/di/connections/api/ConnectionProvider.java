package org.pentaho.di.connections.api;

/**
 * Created by bmorrise on 2/12/19.
 */
public interface ConnectionProvider {
  String getName();

  String getKey();

  Class<? extends ConnectionDetails> getClassType();
}
