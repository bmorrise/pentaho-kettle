package org.pentaho.repo.providers;

/**
 * Created by bmorrise on 3/4/19.
 */
public class Result {

  public enum Status {
    SUCCESS,
    PENDING,
    FILE_COLLISION,
    ERROR
  }

  private Status status;
  private String message;
  private Object data;

  public Result( Status status, String message, Object data ) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus( Status status ) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage( String message ) {
    this.message = message;
  }

  public Object getData() {
    return data;
  }

  public void setData( Object data ) {
    this.data = data;
  }
}
