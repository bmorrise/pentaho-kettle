package org.pentaho.platform.quicksearch.client.model.compound;

import org.pentaho.platform.quicksearch.client.model.compound.occurrence.Must;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Bool {
  private Must must;

  public Bool( Builder builder ) {
    this.must = builder.must;
  }

  public Must getMust() {
    return must;
  }

  public void setMust( Must must ) {
    this.must = must;
  }

  public static class Builder {
    private Must must;

    public Builder must( Must must ) {
      this.must = must;
      return this;
    }

    public Bool build() {
      return new Bool( this );
    }
  }
}
