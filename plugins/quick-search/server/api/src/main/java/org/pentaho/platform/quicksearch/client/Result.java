package org.pentaho.platform.quicksearch.client;

import org.pentaho.platform.quicksearch.client.model.File;

import java.util.List;

/**
 * Created by bmorrise on 4/2/18.
 */
public class Result {

  private String id;
  private List<File> fileList;

  public List<File> getFileList() {
    return fileList;
  }

  public void setFileList( List<File> fileList ) {
    this.fileList = fileList;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }
}
