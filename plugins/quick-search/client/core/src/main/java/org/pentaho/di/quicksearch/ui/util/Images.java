package org.pentaho.di.quicksearch.ui.util;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.pentaho.di.quicksearch.result.SearchResult;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

/**
 * Created by bmorrise on 4/2/18.
 */
public class Images {

  public static Image getImage( Class clazz, String name, int size ) {
    return getImage( clazz.getClassLoader(), name, size );
  }

  public static Image getImage( ClassLoader classLoader, String name, int size ) {
    return SwtSvgImageUtil.getImage( Display.getCurrent(), classLoader, name, size, size );
  }
}
