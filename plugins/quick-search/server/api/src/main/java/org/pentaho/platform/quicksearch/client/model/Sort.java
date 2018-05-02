package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/27/18.
 */
public class Sort extends ParamType {

  public Sort( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return "sort";
  }
}
