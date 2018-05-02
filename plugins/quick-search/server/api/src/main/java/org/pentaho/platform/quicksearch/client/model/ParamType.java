package org.pentaho.platform.quicksearch.client.model;

/**
 * Created by bmorrise on 3/20/18.
 */
public abstract class ParamType {
  private String key;
  private String value;

  public ParamType( String key, String value ) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey( String key ) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  public abstract String getType();
}
