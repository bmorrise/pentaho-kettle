package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Request {

  private int from;
  private int size;
  private Sort sort;
  private Query query;

  public Request( Builder builder ) {
    this.query = builder.query;
    this.from = builder.from;
    this.size = builder.size;
    this.sort = builder.sort;
  }

  public Query getQuery() {
    return query;
  }

  public void setQuery( Query query ) {
    this.query = query;
  }

  public int getFrom() {
    return from;
  }

  public void setFrom( int from ) {
    this.from = from;
  }

  public int getSize() {
    return size;
  }

  public void setSize( int size ) {
    this.size = size;
  }

  public static class Builder {
    private Query query;
    private int from;
    private int size;
    private Sort sort;

    public Builder query( Query query ) {
      this.query = query;
      return this;
    }

    public Builder from( int from ) {
      this.from = from;
      return this;
    }

    public Builder size( int size ) {
      this.size = size;
      return this;
    }

    public Builder sort( Sort sort ) {
      this.sort = sort;
      return this;
    }

    public Request build() {
      return new Request( this );
    }
  }
}
