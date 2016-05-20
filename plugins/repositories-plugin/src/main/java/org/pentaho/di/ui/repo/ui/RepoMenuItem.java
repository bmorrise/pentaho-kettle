package org.pentaho.di.ui.repo.ui;

/**
 * Created by bmorrise on 5/20/16.
 */
public class RepoMenuItem {
  private String label;
  private String command;
  private Boolean selected;

  public RepoMenuItem( String label, String command ) {
    this.label = label;
    this.command = command;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel( String label ) {
    this.label = label;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand( String command ) {
    this.command = command;
  }

  public void setSelected( Boolean selected ) {
    this.selected = selected;
  }

  public Boolean getSelected() {
    return selected;
  }
}
