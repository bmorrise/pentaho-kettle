package org.pentaho.di.vfs.model;

import java.util.HashMap;

/**
 * Created by bmorrise on 2/7/19.
 */
public class FieldMeta {
  private String name;
  private String label;

  public FieldMeta( String name, String label ) {
    this.name = name;
    this.label = label;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }
}
