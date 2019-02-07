package org.pentaho.di.vfs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by bmorrise on 2/3/19.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface VFSConnectionDetails {
  String getName();
  void setName( String name );
}
