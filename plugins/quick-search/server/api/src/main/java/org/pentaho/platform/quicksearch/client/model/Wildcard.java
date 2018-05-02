package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Wildcard extends ParamType implements Occurrence {

  public Wildcard( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return "wildcard";
  }
}
