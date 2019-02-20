package org.pentaho.repo.controller;

import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.repo.provider.Tree;
import org.pentaho.repo.provider.vfs.model.VFSTree;

import java.util.function.Supplier;

/**
 * Created by bmorrise on 2/13/19.
 */
public class VFSBrowserController implements BrowserController {

  private Supplier<ConnectionManager> connectionManagerSupplier = ConnectionManager::getInstance;

  @Override public Tree loadDirectoryTree() {
    return null;
  }
}
