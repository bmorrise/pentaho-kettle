package org.pentaho.di.quicksearch.service;

/**
 * Created by bmorrise on 4/2/18.
 */
public class SearchOptions {
  private String extension;

  public SearchOptions( Builder builder ) {
    this.extension = builder.extension;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension( String extension ) {
    this.extension = extension;
  }

  public static class Builder {

    private String extension;

    public Builder extension( String extension ) {
      this.extension = extension;
      return this;
    }

    public SearchOptions build() {
      return new SearchOptions( this );
    }
  }
}
