package org.pentaho.platform.quicksearch.client.model;

import org.pentaho.platform.quicksearch.client.model.compound.Bool;

/**
 * Created by bmorrise on 3/21/18.
 */
public class Query {
  private Bool bool;

  public Query( Builder builder ) {
    this.bool = builder.bool;
  }

  public Bool getBool() {
    return bool;
  }

  public void setBool(Bool bool) {
    this.bool = bool;
  }

  public static class Builder {
    private Bool bool;

    public Builder bool( Bool bool ) {
      this.bool = bool;
      return this;
    }

    public Query build() {
      return new Query( this );
    }
  }
}
