package org.pentaho.di.connections;

import java.util.List;

/**
 * Created by bmorrise on 2/12/19.
 */
public interface ConnectionProvider {
  String getName();

  String getKey();

  Class<? extends ConnectionDetails> getClassType();

  List<String> getNames();

  List<? extends ConnectionDetails> getConnectionDetails();
}
