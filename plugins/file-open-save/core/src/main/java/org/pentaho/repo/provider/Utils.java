package org.pentaho.repo.provider;

/**
 * Created by bmorrise on 2/23/19.
 */
public class Utils {
  public static boolean matches( String name, String filters ) {
    if ( filters == null ) {
      return true;
    }
    return name.matches( filters.replace( ".", ".*\\." ) );
  }
}
