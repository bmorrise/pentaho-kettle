package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;

/**
 * Created by bmorrise on 3/27/18.
 */
public class RegExp extends ParamType implements Occurrence {
  public RegExp( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return "regexp";
  }
}
