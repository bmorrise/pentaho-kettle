package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/21/18.
 */
public class Term extends ParamType {
  public Term( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return null;
  }
}
