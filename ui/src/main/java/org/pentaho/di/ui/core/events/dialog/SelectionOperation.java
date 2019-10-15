package org.pentaho.di.ui.core.events.dialog;

/**
 * enum for dialog selection operations. Meant to be an abstraction layer between the select values
 * of @see org.pentaho.di.ui.core.FileDialogOperation
 * <br> For example: @link org.pentaho.di.ui.core.FileDialogOperation#SELECT_FILE
 */
public enum SelectionOperation {
  FILE,
  FOLDER;
}
