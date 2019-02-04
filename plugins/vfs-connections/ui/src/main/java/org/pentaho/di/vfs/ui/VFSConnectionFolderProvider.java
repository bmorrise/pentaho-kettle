/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.vfs.ui;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.widget.tree.TreeNode;
import org.pentaho.di.ui.spoon.tree.TreeFolderProvider;

/**
 * Created by bmorrise on 7/6/18.
 */
public class VFSConnectionFolderProvider extends TreeFolderProvider {

  private static final Class<?> PKG = VFSConnectionFolderProvider.class;
  public static final String STRING_VFS_CONNECTIONS = BaseMessages.getString( PKG, "VFSConnectionsTree.Title" );

  public VFSConnectionFolderProvider() {
  }

  @Override
  public void refresh( AbstractMeta meta, TreeNode treeNode, String filter ) {
//    GUIResource guiResource = GUIResource.getInstance();
//    for ( RunConfiguration runConfiguration : runConfigurationDelegate.load() ) {
//      if ( !filterMatch( runConfiguration.getName(), filter ) ) {
//        continue;
//      }
//      String imageFile = runConfiguration.isReadOnly() ? "images/run_tree_disabled.svg" : "images/run_tree.svg";
//      TreeNode childTreeNode = createChildTreeNode( treeNode, runConfiguration.getName(), getRunConfigurationImage(
//              guiResource, imageFile ) );
//      if ( runConfiguration.isReadOnly() ) {
//        childTreeNode.setForeground( getDisabledColor() );
//      }
//    }
  }

//  private Image getRunConfigurationImage( GUIResource guiResource, String file ) {
//    return guiResource
//            .getImage( file, getClass().getClassLoader(), ConstUI.MEDIUM_ICON_SIZE, ConstUI.MEDIUM_ICON_SIZE );
//  }

//  private Color getDisabledColor() {
//    Device device = Display.getCurrent();
//    return new Color( device, 188, 188, 188 );
//  }

  @Override
  public String getTitle() {
    return STRING_VFS_CONNECTIONS;
  }

  private TreeNode createChildTreeNode( TreeNode parent, String text, Image image ) {
    return super.createTreeNode( parent, text, image );
  }

  @Override
  public TreeNode createTreeNode( TreeNode parent, String text, Image image ) {
    TreeNode treeNode = super.createTreeNode( parent, text, image );
    treeNode.setIndex( 0 );
    return treeNode;
  }
}
