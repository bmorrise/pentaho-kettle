package org.pentaho.repo.providers.processor;

import org.pentaho.repo.providers.Properties;
import org.pentaho.repo.providers.Result;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bmorrise on 3/5/19.
 */
public class Processor {
  private ConcurrentMap<String, Process> processes = new ConcurrentHashMap<>();
  private ExecutorService executorService;

  public Processor() {
    this.executorService = Executors.newCachedThreadPool();
  }

  public String submit( Process process ) {
    String uuid = UUID.randomUUID().toString();
    process.setId( uuid );
    Future future = executorService.submit( process );
    process.setFuture( future );
    processes.put( uuid, process );

    return uuid;
  }

  public boolean cancel( String uuid ) {
    Process process = processes.get( uuid );
    return process.cancel();
  }

  public Result getStatus( String uuid ) {
    Process process = processes.get( uuid );
    if ( process != null ) {
      return process.getStatus();
    }
    return null;
  }

  public boolean proceed( String uuid, Properties properties ) {
    Process process = processes.get( uuid );
    if ( process != null ) {
      process.setState( Process.State.RUNNING );
      process.setProperties( properties );
    }
    return true;
  }
}
