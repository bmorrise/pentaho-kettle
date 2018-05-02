package org.pentaho.di.quicksearch.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.ui.util.Colors;

/**
 * Created by bmorrise on 3/26/18.
 */
public class SearchResultListItem extends Composite {

  private Label label;
  private SearchResult searchResult;

  public SearchResultListItem( Composite composite, int i, SearchResult searchResult ) {
    super( composite, i );
    this.searchResult = searchResult;
    init();
  }

  public SearchResult getSearchResult() {
    return searchResult;
  }

  public void setText( String text ) {
    label.setText( text );
    layout();
  }

  private void init() {
    setBackground( Colors.LIGHT_GRAY );
    setLayout( new GridLayout( 2, false ) );
    setLayoutData( new GridData(SWT.FILL, SWT.BEGINNING, true, false) );

    Label fileIcon = new Label( this, SWT.NONE );
    if ( searchResult != null ) {
      fileIcon.setImage( searchResult.getImage() );
    }

    label = new Label( this, SWT.NONE );

    final SearchResultListItem searchResultListItem = this;
    addMouseListener( this, new MouseAdapter() {
      @Override
      public void mouseDown( MouseEvent mouseEvent ) {
        searchResultListItem.setSelected( true );
      }
    } );

    layout();
  }

  @Override
  public void addMouseListener( MouseListener mouseListener ) {
    super.addMouseListener( mouseListener );
    for ( Control control : getChildren() ) {
      addMouseListener( control, mouseListener );
    }
  }

  private void addMouseListener( Control control, MouseListener mouseListener ) {
    control.addMouseListener( mouseListener );
    if ( control instanceof Composite ) {
      for ( Control control1 : ((Composite) control).getChildren() ) {
        addMouseListener( control1, mouseListener );
      }
    }
  }

  public void setSelected( boolean selected ) {
    if ( selected ) {
      setBackground( this.getDisplay().getSystemColor( SWT.COLOR_BLUE ) );
      label.setForeground( this.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    } else {
      setBackground( Colors.LIGHT_GRAY );
      label.setForeground( this.getDisplay().getSystemColor( SWT.COLOR_BLACK ) );
    }
    layout();
  }

}
