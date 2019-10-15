package org.pentaho.di.ui.core.events.dialog;

import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.ui.core.widget.TextVar;

public class SelectionAdapterFileDialogTextVar extends SelectionAdapterFileDialog<TextVar> {

  public SelectionAdapterFileDialogTextVar( LogChannelInterface log, TextVar textUiWidget, AbstractMeta meta,
                                            SelectionOperation selectionOperation, String[] filter ) {
    super( log, textUiWidget, meta, selectionOperation, filter);
  }

  @Override protected String getText() {
    return this.getTextWidget().getText();
  }

  @Override protected void setText( String text ) {
    this.getTextWidget().setText( text );
  }
}
