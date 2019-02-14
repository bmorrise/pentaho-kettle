package org.pentaho.repo.controller;

import org.pentaho.repo.model.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bmorrise on 2/13/19.
 */
public class CombinedBrowserController {

  List<BrowserController> browserControllers = new ArrayList<>();

  public CombinedBrowserController() {
    this.browserControllers = Arrays.asList(
      new RepositoryBrowserController(),
      new VFSBrowserController()
    );
  }

  public List<Tree> load() {
    List<Tree> trees = new ArrayList<>();
    for ( BrowserController browserController : browserControllers ) {
      trees.add( browserController.loadDirectoryTree() );
    }
    return trees;
  }
}
