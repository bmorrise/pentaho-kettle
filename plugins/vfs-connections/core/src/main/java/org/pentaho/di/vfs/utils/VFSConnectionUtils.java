package org.pentaho.di.vfs.utils;

/**
 * Created by bmorrise on 2/4/19.
 */
public class VFSConnectionUtils {

  public static String getSchema( String path ) {
    if ( !path.contains( ":" ) ) {
      return null;
    }
    return path.substring( 0, path.indexOf( ':' ) );
  }

}
