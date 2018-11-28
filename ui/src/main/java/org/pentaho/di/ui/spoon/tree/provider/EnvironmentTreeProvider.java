package org.pentaho.di.ui.spoon.tree.provider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.tree.TreeNode;
import org.pentaho.di.ui.spoon.tree.PopupProvider;
import org.pentaho.di.ui.spoon.tree.TreeFolderProvider;

/**
 * Created by bmorrise on 11/14/18.
 */
public class EnvironmentTreeProvider extends TreeFolderProvider {

  private static final String KEY = "ENVIRONMENTS";
  private Menu menu;

  private PluginRegistry pluginRegistry = PluginRegistry.getInstance();

  @Override
  public void refresh( AbstractMeta meta, TreeNode treeNode, String filter ) {

  }

  @Override
  public String getTitle() {
    return "Environments";
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  public Menu getPopupMenu( Tree tree ) {
    if ( menu == null ) {
      menu = new Menu( tree );
      MenuItem menuItem = new MenuItem( menu, SWT.NONE );
      menuItem.setText( "Test" );
      menuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent selectionEvent ) {
          System.out.println( "Selected" );
        }
      } );
    }
    return menu;
  }
}
