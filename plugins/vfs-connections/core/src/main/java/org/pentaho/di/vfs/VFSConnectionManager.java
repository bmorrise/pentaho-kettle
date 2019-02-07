package org.pentaho.di.vfs;

import org.apache.commons.vfs2.FileSystemOptions;
import org.pentaho.di.vfs.model.Data;
import org.pentaho.di.vfs.utils.VFSConnectionUtils;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static org.pentaho.metastore.util.PentahoDefaults.NAMESPACE;

/**
 * Created by bmorrise on 2/3/19.
 */
public class VFSConnectionManager {

  private static VFSConnectionManager instance;

  private Supplier<IMetaStore> metaStoreSupplier;
  private ConcurrentHashMap<String, VFSConnectionProvider> vfsConnectionProviders = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, String> schemaLookup = new ConcurrentHashMap<>();

  public static VFSConnectionManager getInstance() {
    if ( instance == null ) {
      instance = new VFSConnectionManager();
    }

    return instance;
  }

  public void setMetastoreSupplier( Supplier<IMetaStore> metaStoreSupplier ) {
    this.metaStoreSupplier = metaStoreSupplier;
  }

  public boolean addVFSConnectionProvider( String schema, VFSConnectionProvider vfsConnectionProvider ) {
    return vfsConnectionProviders.putIfAbsent( schema, vfsConnectionProvider ) != null;
  }

  public void addSchemaLookup( String from, String to ) {
    schemaLookup.putIfAbsent( from, to );
  }

  @SuppressWarnings( "unchecked" )
  public FileSystemOptions getFileSystemOpts( String path, String name ) {
    String schema = VFSConnectionUtils.getSchema( path );
    if ( schema != null ) {
      if ( schemaLookup.get( schema ) != null ) {
        schema = schemaLookup.get( schema );
      }
      VFSConnectionDetails vfsConnectionDetails = getConnectionDetails( schema, name );
      VFSConnectionProvider vfsConnectionProvider = vfsConnectionProviders.get( schema );
      if ( vfsConnectionDetails != null && vfsConnectionProvider != null ) {
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
  public Data loadData( String name ) {
    List<VFSConnectionProvider> providers = Collections.list( vfsConnectionProviders.elements() );
    for ( VFSConnectionProvider provider : providers ) {
      try {
        VFSConnectionDetails vfsConnectionDetails = (VFSConnectionDetails) getMetaStoreFactory( provider.getType() )
                .loadElement( name );
        if ( vfsConnectionDetails != null ) {
          return new Data( vfsConnectionDetails, provider.getTemplate(), provider.getFields(), provider.getSchema() );
        }
      } catch ( MetaStoreException ignored ) {
        // Isn't in that metastore
      }
    }
    return null;
  }

  @SuppressWarnings( "unchecked" )
  public void delete( String name ) {
    List<VFSConnectionProvider> providers = Collections.list( vfsConnectionProviders.elements() );
    for ( VFSConnectionProvider provider : providers ) {
      try {
        VFSConnectionDetails vfsConnectionDetails = (VFSConnectionDetails) getMetaStoreFactory( provider.getType() )
                .loadElement( name );
        if ( vfsConnectionDetails != null ) {
          getMetaStoreFactory( provider.getType() ).deleteElement( name );
        }
      } catch ( MetaStoreException ignored ) {
        // Isn't in that metastore
      }
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
    return new MetaStoreFactory<>( clazz, metaStoreSupplier.get(), NAMESPACE );
  }

  public List<String> getNames() {
    List<String> detailNames = new ArrayList<>();
    List<VFSConnectionProvider> providers = Collections.list( vfsConnectionProviders.elements() );
    for ( VFSConnectionProvider provider : providers ) {
      detailNames.addAll( getNames( provider ) );
    }
    return detailNames;
  }

  public Data getData( String type ) {
    VFSConnectionDetails vfsConnectionDetails = null;
    try {
      VFSConnectionProvider provider = vfsConnectionProviders.get( type );
      vfsConnectionDetails = (VFSConnectionDetails) provider.getType().newInstance();
      return new Data( vfsConnectionDetails, provider.getTemplate(), provider.getFields(), type );
    } catch ( InstantiationException | IllegalAccessException e ) {
      return null;
    }
  }

  public List<Type> getItems() {
    List<Type> types = new ArrayList<>();
    List<VFSConnectionProvider> providers = Collections.list( vfsConnectionProviders.elements() );
    for ( VFSConnectionProvider provider : providers ) {
      types.add( new Type( provider.getSchema(), provider.getName() ) );
    }
    return types;
  }

  public static class Type {

    private String value;
    private String label;

    public Type( String value, String label ) {
      this.value = value;
      this.label = label;
    }

    public String getValue() {
      return value;
    }

    public void setValue( String value ) {
      this.value = value;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel( String label ) {
      this.label = label;
    }
  }
}
