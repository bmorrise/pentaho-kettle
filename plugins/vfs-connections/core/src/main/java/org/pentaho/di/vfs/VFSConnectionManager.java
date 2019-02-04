package org.pentaho.di.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

import static org.pentaho.metastore.util.PentahoDefaults.NAMESPACE;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManager {

  private IMetaStore metaStore;
  private ConcurrentLinkedQueue<VFSConnectionProvider> vfsConnectionProviders = new ConcurrentLinkedQueue<>();

  public VFSConnectionManager( IMetaStore metaStore ) {
    this.metaStore = metaStore;
  }

  public void addVFSConnectionProvider(VFSConnectionProvider vfsConnectionProvider) {
    vfsConnectionProviders.add( vfsConnectionProvider );
  }

  @SuppressWarnings( "unchecked" )
  public FileSystemOptions getFileSystemOpts( Class<? extends VFSConnectionDetails> clazz, String name ) {
    VFSConnectionDetails vfsConnectionDetails = getConnectionDetails( clazz, name );
    VFSConnectionProvider vfsConnectionProvider = vfsConnectionProviders
            .stream()
            .filter( provider -> provider.getType().equals( clazz ) )
            .findFirst()
            .orElse( null );

    return vfsConnectionProvider.getOpts( vfsConnectionDetails );
  }

  public <T extends VFSConnectionDetails> VFSConnectionDetails getConnectionDetails( Class<T> clazz, String name ) {
    try {
      return getMetaStoreFactory( clazz ).loadElement( name );
    } catch ( MetaStoreException mse ) {
      return null;
    }
  }

  @SuppressWarnings( "unchecked" )
  public <T extends VFSConnectionDetails> void save( T connectionDetails ) throws MetaStoreException {
    getMetaStoreFactory( (Class<T>) connectionDetails.getClass() ).saveElement( connectionDetails );
  }

  private <T extends VFSConnectionDetails> MetaStoreFactory<T> getMetaStoreFactory( Class<T> clazz ) {
    return new MetaStoreFactory<>( clazz, metaStore, NAMESPACE );
  }
}
