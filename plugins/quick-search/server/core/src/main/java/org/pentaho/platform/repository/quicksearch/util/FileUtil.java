package org.pentaho.platform.repository.quicksearch.util;

/**
 * Created by bmorrise on 3/19/18.
 */
public class FileUtil {

  public static String getExtension( String filename ) {
    int index = filename.lastIndexOf( "." );
    return index != -1 ? filename.substring( index + 1 ) : "";
  }

}
