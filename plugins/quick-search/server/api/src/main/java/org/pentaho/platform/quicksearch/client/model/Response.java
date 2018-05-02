package org.pentaho.platform.quicksearch.client.model;

import java.util.List;

/**
 * Created by bmorrise on 3/20/18.
 */
public class Response {
  private List<File> fileList;

  public List<File> getFileList() {
    return fileList;
  }

  public void setFileList( List<File> fileList ) {
    this.fileList = fileList;
  }
}
