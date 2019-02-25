package org.pentaho.repo.providers;

import java.util.List;

/**
 * Created by bmorrise on 3/5/19.
 */
public class Snipes {
  private List<String> succeeded;
  private List<String> failed;
  private List<String> skipped;

  public Snipes( List<String> succeeded, List<String> skipped, List<String> failed ) {
    this.succeeded = succeeded;
    this.failed = failed;
    this.skipped = skipped;
  }

  public List<String> getSucceeded() {
    return succeeded;
  }

  public void setSucceeded( List<String> succeeded ) {
    this.succeeded = succeeded;
  }

  public List<String> getFailed() {
    return failed;
  }

  public void setFailed( List<String> failed ) {
    this.failed = failed;
  }

  public List<String> getSkipped() {
    return skipped;
  }

  public void setSkipped( List<String> skipped ) {
    this.skipped = skipped;
  }
}
