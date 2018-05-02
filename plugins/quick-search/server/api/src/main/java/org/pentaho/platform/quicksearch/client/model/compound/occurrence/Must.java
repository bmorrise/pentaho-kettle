package org.pentaho.platform.quicksearch.client.model.compound.occurrence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Must extends ArrayList {

  public Must( Builder builder ) {
    addAll( builder.occurrences );
  }

  public static class Builder {
    private List<Occurrence> occurrences = new ArrayList<>();

    public Builder occurrence( Occurrence occurrence ) {
      this.occurrences.add( occurrence );
      return this;
    }

    public Must build() {
      return new Must( this );
    }
  }

}
