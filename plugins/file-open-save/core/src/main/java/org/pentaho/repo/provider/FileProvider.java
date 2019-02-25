package org.pentaho.repo.provider;

import java.util.List;

/**
 * Created by bmorrise on 2/14/19.
 */
public interface FileProvider {
  String getName();
  String getType();
  Tree getTree();
  List<? extends File> getFiles( String name, String path, String filters );
  boolean isAvailable();
}
