package org.pentaho.repo.providers.repository.model;

import org.pentaho.di.repository.ObjectId;

/**
 * Created by bmorrise on 2/25/19.
 */
public class RepositoryObjectId implements ObjectId {

  public RepositoryObjectId( String id ) {
    this.id = id;
  }

  private String id;

  @Override public String getId() {
    return id;
  }
}
