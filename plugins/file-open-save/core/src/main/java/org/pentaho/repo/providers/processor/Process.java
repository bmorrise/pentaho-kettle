package org.pentaho.repo.providers.processor;

import org.pentaho.repo.providers.Properties;
import org.pentaho.repo.providers.Result;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * Created by bmorrise on 3/5/19.
 */
public abstract class Process implements Runnable {

  public enum State {
    RUNNING,
    PAUSED
  }

  private String id;
  private Future future;
  private Result status;
  protected State state;
  private Properties properties = new Properties();
  private CountDownLatch countDownLatch;

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public Future getFuture() {
    return future;
  }

  public void setFuture( Future future ) {
    this.future = future;
  }

  public boolean cancel() {
    return future.cancel( true );
  }

  public Result getStatus() {
    return status;
  }

  public void setStatus( Result status ) {
    this.status = status;
  }

  public State getState() {
    return state;
  }

  public void setState( State state ) {
    synchronized ( this ) {
      this.state = state;
      this.notifyAll();
    }
  }

  public boolean isState( State state ) {
    return this.state.equals( state );
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties( Properties properties ) {
    this.properties = properties;
  }

  public void setProperty( String key, Object value ) {
    properties.put( key, value );
  }
}
