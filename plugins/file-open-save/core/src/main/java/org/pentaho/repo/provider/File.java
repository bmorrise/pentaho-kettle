package org.pentaho.repo.provider;

import java.util.Date;

/**
 * Created by bmorrise on 2/13/19.
 */
public interface File extends Entity, Providerable {
  String getName();
  String getPath();
  String getParent();
  String getType();
  String getRoot();
  Date getDate();
}
