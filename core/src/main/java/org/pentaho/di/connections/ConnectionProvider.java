package org.pentaho.di.connections;

import java.util.List;

/**
 * Created by bmorrise on 2/12/19.
 */
public interface ConnectionProvider<T extends ConnectionDetails> {
  String getName();

  String getKey();

  Class<T> getClassType();

  List<String> getNames();

  List<T> getConnectionDetails();

  boolean test( T connectionDetails );
}
