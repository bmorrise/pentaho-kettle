package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Occurrence;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Match extends ParamType implements Occurrence {
  public Match( String key, String value ) {
    super( key, value );
  }

  @Override
  public String getType() {
    return "match";
  }
}
