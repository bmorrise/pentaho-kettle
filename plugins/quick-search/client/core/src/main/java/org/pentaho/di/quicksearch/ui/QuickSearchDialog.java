package org.pentaho.di.quicksearch.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.quicksearch.service.QuickSearchService;
import org.pentaho.di.quicksearch.service.SearchOptions;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.quicksearch.ui.list.SearchResultList;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by bmorrise on 3/21/18.
 */
public class QuickSearchDialog extends Dialog {

  private Class<?> PKG = QuickSearchDialog.class;

  public static final int KEY_UP = 16777217;
  public static final int KEY_DOWN = 16777218;
  public static final int DIALOG_WIDTH = 700;
  public static final String SEARCH_SVG = "search.svg";
  protected Shell dialog;
  protected Display display;

  private SearchResultList list;
  private Composite results;
  private FormData fdHidden;
  private FormData fdResults;

  private List<QuickSearchService> services;
  private List<Section> sections;

  public QuickSearchDialog( Shell shell, List<QuickSearchService> services ) {
    super( shell );
    this.services = services;
    sections = new ArrayList<>();
    for ( QuickSearchService service : services ) {
      sections.add( new Section( service.isAvailable(), service.getLabel(), service.rank() ) );
    }
    sections.sort( Comparator.comparingInt( Section::getRank ) );
  }

  class Section {
    private boolean isAvailable;
    private String title;
    private int rank;

    public Section( boolean isAvailable, String title, int rank ) {
      this.isAvailable = isAvailable;
      this.title = title;
      this.rank = rank;
    }

    public boolean isAvailable() {
      return isAvailable;
    }

    public String getTitle() {
      return title;
    }

    public int getRank() {
      return rank;
    }
  }

  public void open() {
    Shell parent = this.getParent();
    display = parent.getDisplay();
    dialog = new Shell( parent, SWT.RESIZE );
    dialog.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );

    construct();

    dialog.pack();
    Rectangle bounds = parent.getBounds();
    Rectangle rect = dialog.getBounds();
    int x = bounds.x + (bounds.width - rect.width) / 2;
    int y = bounds.y + (bounds.height - 400) / 2;
    dialog.setLocation( x, y );
    dialog.open();

    while ( !dialog.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
  }

  private void construct() {
    FormLayout formLayout = new FormLayout();
    formLayout.marginHeight = 0;
    formLayout.marginWidth = 0;
    dialog.setLayout( formLayout );

    FormLayout searchFormLayout = new FormLayout();
    searchFormLayout.marginHeight = 10;
    searchFormLayout.marginWidth = 10;
    Composite searchComposite = new Composite( dialog, SWT.NONE );
    searchComposite.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    searchComposite.setLayout( searchFormLayout );

    Label searchIcon = new Label( searchComposite, SWT.NONE );
    searchIcon.setImage( getImage( SEARCH_SVG, ConstUI.MEDIUM_ICON_SIZE ) );
    FormData fdSearchIcon = new FormData();
    fdSearchIcon.left = new FormAttachment( 0 );
    fdSearchIcon.top = new FormAttachment( 17 );
    searchIcon.setLayoutData( fdSearchIcon );

    Text wSearch = new Text( searchComposite, SWT.NONE );
    wSearch.setMessage( BaseMessages.getString( PKG, "quicksearch.dialog.search.text" )  );

    FontData[] fontData = wSearch.getFont().getFontData();
    for ( int i = 0; i < fontData.length; i++ ) {
      fontData[i].setHeight( 30 );
    }

    final Font newFont = new Font( display, fontData );
    wSearch.setFont( newFont );
    wSearch.addDisposeListener( e -> newFont.dispose() );

    FormData fdwSearch = new FormData();
    fdwSearch.width = DIALOG_WIDTH;
    fdwSearch.left = new FormAttachment( searchIcon, 10 );
    fdwSearch.right = new FormAttachment( 100 );
    wSearch.setLayoutData( fdwSearch );

    FormData fdwSearchComponent = new FormData();
    fdwSearchComponent.width = DIALOG_WIDTH;
    fdwSearchComponent.left = new FormAttachment( 0 );
    fdwSearchComponent.right = new FormAttachment( 100 );
    searchComposite.setLayoutData( fdwSearchComponent );

    FormLayout listLayout = new FormLayout();
    listLayout.marginWidth = 0;
    listLayout.marginHeight = 0;

    fdHidden = new FormData();
    fdHidden.height = 0;
    fdHidden.width = DIALOG_WIDTH;

    results = new Composite( dialog, SWT.NONE );
    results.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    results.setLayout( listLayout );

    list = new SearchResultList( results, SWT.NONE );
    FormData fdList = new FormData();
    fdList.top = new FormAttachment( 0 );
    fdList.left = new FormAttachment( 0 );
    fdList.right = new FormAttachment( 100 );
    fdList.bottom = new FormAttachment( 100 );
    list.setLayoutData( fdList );

    fdResults = new FormData();
    fdResults.top = new FormAttachment( searchComposite );
    fdResults.right = new FormAttachment( 100 );
    fdResults.bottom = new FormAttachment( 100 );
    fdResults.left = new FormAttachment( 0 );
    fdResults.height = 340;
    fdResults.width = DIALOG_WIDTH;
    results.setLayoutData( fdHidden );

    wSearch.addKeyListener( new KeyAdapter() {
      @Override
      public void keyPressed( KeyEvent keyEvent ) {
        if ( keyEvent.keyCode == 13 ) {
          if ( list.getSelected() != null ) {
            final SearchResult searchResult = list.getSelected().getSearchResult();
            close();
            searchResult.execute();
          }
        }
        if ( keyEvent.keyCode == KEY_UP ) {
          if ( list.getSelectedIndex() != -1 && list.getSelectedIndex() > 0 ) {
            highlight( list.getSelectedIndex() - 1 );
          }
          wSearch.getDisplay().asyncExec( () -> wSearch.setSelection( wSearch.getText().length() ) );
        }
        if ( keyEvent.keyCode == KEY_DOWN ) {
          highlight( list.getSelectedIndex() != -1 ? list.getSelectedIndex() + 1 : 0 );
          wSearch.getDisplay().asyncExec( () -> wSearch.setSelection( wSearch.getText().length() ) );
        }
      }
    } );
    wSearch.setFocus();

    dialog.pack();
    list.render();
    for ( Section section : sections ) {
      if ( section.isAvailable() ) {
        list.addSection( section.getTitle() );
      }
    }

    wSearch.addModifyListener( modifyEvent -> {
      String search = wSearch.getText();
      if ( !Utils.isEmpty( search.trim() ) ) {
        results.setLayoutData( fdResults );
        dialog.pack();
        for ( QuickSearchService service : services ) {
          if ( service.isAvailable() ) {
            service.search( search, new SearchOptions.Builder().extension( "ktr|kjb" ).build(), searchResults -> {
              dialog.getDisplay().asyncExec( () -> list.addSectionContent( service.getLabel(), searchResults ) );
              render();
            } );
          } else {
            render();
          }
        }
        list.doUpdate();
      } else {
        clearResults();
      }
    } );

    list.addSelectionAdapter( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent selectionEvent ) {
        final SearchResult searchResult = list.getSelected().getSearchResult();
        close();
        searchResult.execute();
      }
    } );
  }

  public void close() {
    dialog.close();
    dialog.dispose();
  }

  private void render() {
    dialog.getDisplay().asyncExec( () -> {
      if ( list.size() > 0 ) {
        list.doUpdate();
        list.first();
      } else {
        clearResults();
      }
    });
  }

  private void clearResults() {
    results.setLayoutData( fdHidden );
    dialog.pack();
  }

  private void highlight( int index ) {
    list.highlight( index );
  }

  protected Image getImage( String name, int size ) {
    return SwtSvgImageUtil.getImage( getParent().getDisplay(), getClass().getClassLoader(), name, size, size );
  }

  public boolean isDisposed() {
    return dialog.isDisposed();
  }
}
