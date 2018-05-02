package org.pentaho.di.quicksearch.result;

import org.eclipse.swt.graphics.Image;

/**
 * Created by bmorrise on 4/2/18.
 */
public interface SearchResult {
  String getId();
  String getName();
  String getDescription();
  void execute();
  Image getImage();
}
