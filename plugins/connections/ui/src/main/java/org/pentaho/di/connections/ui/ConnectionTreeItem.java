package org.pentaho.di.connections.ui;

/**
 * Created by bmorrise on 2/4/19.
 */
public class ConnectionTreeItem {
  private String label;

  public ConnectionTreeItem( String label ) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }
}
