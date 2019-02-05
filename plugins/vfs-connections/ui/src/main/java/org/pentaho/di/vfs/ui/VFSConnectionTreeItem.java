package org.pentaho.di.vfs.ui;

/**
 * Created by bmorrise on 2/4/19.
 */
public class VFSConnectionTreeItem {
  private String label;

  public VFSConnectionTreeItem( String label ) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }
}
