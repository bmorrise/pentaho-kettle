package org.pentaho.repo.providers;

/**
 * Created by bmorrise on 3/5/19.
 */
public class FromTo {
  public FromTo( String from, String to ) {
    this.from = from;
    this.to = to;
  }

  private String from;
  private String to;

  public String getFrom() {
    return from;
  }

  public void setFrom( String from ) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo( String to ) {
    this.to = to;
  }
}
