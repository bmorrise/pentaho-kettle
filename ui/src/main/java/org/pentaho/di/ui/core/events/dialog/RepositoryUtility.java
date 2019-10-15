package org.pentaho.di.ui.core.events.dialog;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.function.Supplier;

/**
 * Utility class for repository related functions.
 */
public class RepositoryUtility {

  private Supplier<Spoon> spoonSupplier;

  /**
   * Default constructor.
   */
  public RepositoryUtility() {
    // empty constructor
  }

  /**
   * Determine if connected to repository.
   * @return true if connected, false otherwise.
   */
  public boolean isConnectedToRepository() {
    // NOTE: can dynamically switch between rep and non-rep
    return StringUtils.isNotBlank( getSpoon().getUsername() );
  }

  /**
   * Get spoon instance.
   * @return
   */
  protected Spoon getSpoon() {
    if ( spoonSupplier == null ) {
      spoonSupplier = Spoon::getInstance;
    }
    return spoonSupplier.get();
  }
}
