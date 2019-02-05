package org.pentaho.di.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.vfs.utils.VFSConnectionUtils;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.pentaho.metastore.util.PentahoDefaults.NAMESPACE;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManager {

  private IMetaStore metaStore;
  private ConcurrentHashMap<String, VFSConnectionProvider> vfsConnectionProviders = new ConcurrentHashMap<>();

  public VFSConnectionManager( IMetaStore metaStore ) {
    this.metaStore = metaStore;
  }

  public boolean addVFSConnectionProvider( String schema, VFSConnectionProvider vfsConnectionProvider) {
    return vfsConnectionProviders.putIfAbsent( schema, vfsConnectionProvider ) != null;
  }

  @SuppressWarnings( "unchecked" )
  public FileSystemOptions getFileSystemOpts( String path, String name ) {
    String schema = VFSConnectionUtils.getSchema( path );
    if (schema != null ) {
      VFSConnectionDetails vfsConnectionDetails = getConnectionDetails( schema, name );
      VFSConnectionProvider vfsConnectionProvider = vfsConnectionProviders.get( schema );
      if ( vfsConnectionProvider != null ) {
        return vfsConnectionProvider.getOpts( vfsConnectionDetails );
      }
    }

    return null;
  }

  @SuppressWarnings( "unchecked" )
  public <T extends VFSConnectionDetails> VFSConnectionDetails getConnectionDetails( String schema, String name ) {
    VFSConnectionProvider vfsConnectionProvider = vfsConnectionProviders.get( schema );
    if ( vfsConnectionProvider != null ) {
      Class<T> clazz = vfsConnectionProviders.get( schema ).getType();
      try {
        return getMetaStoreFactory( clazz ).loadElement( name );
      } catch ( MetaStoreException mse ) {
        return null;
      }
    }

    return null;
  }

  @SuppressWarnings( "unchecked" )
  public <T extends VFSConnectionDetails> boolean save( T connectionDetails ) {
    try {
      getMetaStoreFactory( (Class<T>) connectionDetails.getClass() ).saveElement( connectionDetails );
      return true;
    } catch ( MetaStoreException mse ) {
      return false;
    }
  }

  @SuppressWarnings( "unchecked" )
  public List<String> getNames( VFSConnectionProvider provider ) {
    try {
      return getMetaStoreFactory( provider.getType() ).getElementNames();
    } catch ( MetaStoreException mse ) {
      return Collections.emptyList();
    }
  }

  private <T extends VFSConnectionDetails> MetaStoreFactory<T> getMetaStoreFactory( Class<T> clazz ) {
    return new MetaStoreFactory<>( clazz, metaStore, NAMESPACE );
  }

  public List<String> getNames() {
    List<String> detailNames = new ArrayList<>();
    List<VFSConnectionProvider> providers = Collections.list( vfsConnectionProviders.elements() );
    for ( VFSConnectionProvider provider : providers ) {
      detailNames.addAll( getNames( provider ) );
    }
    return detailNames;
  }
}
