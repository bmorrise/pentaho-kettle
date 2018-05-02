package org.pentaho.di.quicksearch.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.ui.util.Colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bmorrise on 3/27/18.
 */
public class SearchResultList extends Composite {

  private Map<String, List<SearchResultListItem>> searchResultListItems = new HashMap<>();
  private Map<String, Composite> sections = new HashMap<>();
  private Map<String, Composite> headers = new HashMap<>();
  private ArrayList<String> sectionTitles = new ArrayList<>();

  private List<SelectionAdapter> selectionAdapters = new ArrayList<>();
  private SearchResultListItem selected = null;
  private ScrolledComposite scrolledComposite;
  private Composite result;
  private int selectedIndex;

  public SearchResultList( Composite composite, int i ) {
    super( composite, i );
    init();
  }

  public void init() {
    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    setLayout( gridLayout );
    scrolledComposite = new ScrolledComposite( this, SWT.V_SCROLL );
    scrolledComposite.setBackground( Colors.LIGHT_GRAY );
    scrolledComposite.setExpandVertical( true );
    scrolledComposite.setExpandHorizontal( true );
    GridData gridData = new GridData( SWT.FILL, SWT.FILL, true, true );
    gridData.heightHint = 400;
    scrolledComposite.setLayoutData( gridData );
    scrolledComposite.setSize( 400, 340 );
    scrolledComposite.layout();
    scrolledComposite.setShowFocusedControl( true );
  }

  public void addSection( String title ) {
    Composite header = new Composite( result, SWT.NONE );
    header.setLayout( new GridLayout( 1, false ) );
    header.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );

    Label headerLabel = new Label( header, SWT.NONE );
    headerLabel.setText( title );
    header.layout();

    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;

    Composite section = new Composite( result, SWT.NONE );
    section.setBackground( Colors.LIGHT_GRAY );
    section.setLayout( gridLayout );
    section.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );
    section.pack();

    sectionTitles.add( title );
    sections.put( title, section );
    searchResultListItems.put( title, new ArrayList<>() );
    headers.put( title, header );
  }

  public void render() {
    GridLayout gridLayout = new GridLayout( 1, false );
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;

    result = new Composite( scrolledComposite, SWT.NONE );
    result.setBackground( Colors.LIGHT_GRAY );
    result.setLayout( gridLayout );
    result.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, true, false ) );
    result.pack();

    scrolledComposite.setContent( result );
  }

  private void toggleHeader( String title, boolean show ) {
    Composite header = headers.get( title );
    header.setVisible( show );
    ((GridData) header.getLayoutData()).exclude = !show;
  }

  public void addSectionContent( String title, List<SearchResult> searchResults ) {
    Composite content = sections.get( title );
    if ( content != null ) {
      toggleHeader( title, searchResults.size() > 0 );
      List<SearchResultListItem> items = new ArrayList<>();
      for ( SearchResult searchResult : searchResults ) {
        SearchResultListItem searchResultListItem = new SearchResultListItem( content, SWT.NONE, searchResult );
        searchResultListItem.setText( searchResult.getName() );
        searchResultListItem.setToolTipText( searchResult.getDescription() );
        searchResultListItem.addMouseListener( new MouseAdapter() {
          @Override
          public void mouseDown( MouseEvent mouseEvent ) {
            List<SearchResultListItem> items = searchResultListItems.get( title );
            for ( SearchResultListItem item : items ) {
              if ( !((Control) mouseEvent.getSource()).isDisposed() ) {
                if ( mouseEvent.getSource() != item && ((Control) mouseEvent.getSource()).getParent() != item ) {
                  item.setSelected( false );
                } else {
                  selected = item;
                  for ( SelectionAdapter selectionAdapter : selectionAdapters ) {
                    selectionAdapter.widgetSelected( null );
                  }
                }
              }
            }
          }
        } );
        items.add( searchResultListItem );
      }
      for ( SearchResultListItem searchResultListItem : searchResultListItems.get( title ) ) {
        searchResultListItem.dispose();
      }
      searchResultListItems.put( title, items );
      content.layout();
    }
  }

  public void addSelectionAdapter( SelectionAdapter selectionAdapter ) {
    selectionAdapters.add( selectionAdapter );
  }

  public SearchResultListItem getSelected() {
    return selected;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void first() {
    highlight( 0 );
  }

  private void highlight( String section, int index ) {
    if ( selected != null && !selected.isDisposed() ) {
      selected.setSelected( false );
    }
    if ( searchResultListItems.get( section ).size() > 0 ) {
      SearchResultListItem searchResultListItem = searchResultListItems.get( section ).get( index );
      searchResultListItem.setSelected( true );
      selected = searchResultListItem;
    }
  }

  public void highlight( int index ) {
    int start = 0;
    for ( int i = 0; i < sectionTitles.size(); i++ ) {
      int size = searchResultListItems.get( sectionTitles.get( i ) ).size();
      if ( index < start + size ) {
        highlight( sectionTitles.get( i ), index - start );
        selectedIndex = index;
        return;
      }
      start += size;
    }
  }

  public void doUpdate() {
    int width = scrolledComposite.getClientArea().width;
    scrolledComposite.setMinSize( result.computeSize( width, SWT.DEFAULT ) );
    result.layout();
  }

  public int size() {
    return sections.size();
  }
}
