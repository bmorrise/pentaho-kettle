package org.pentaho.repo.provider;

import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public interface Tree<T extends Entity> extends Providerable {
  String getName();
  List<T> getChildren();
  void addChild( T child );
}
