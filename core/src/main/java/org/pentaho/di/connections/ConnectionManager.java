package org.pentaho.di.connections;

import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.persist.MetaStoreFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.pentaho.metastore.util.PentahoDefaults.NAMESPACE;

/**
 * Created by bmorrise on 2/3/19.
 */
public class ConnectionManager {

  private static ConnectionManager instance;

  private List<LookupFilter> lookupFilters = new ArrayList<>();
  private Supplier<IMetaStore> metaStoreSupplier;
  private ConcurrentHashMap<String, ConnectionProvider> connectionProviders = new ConcurrentHashMap<>();

  public static ConnectionManager getInstance() {
    if ( instance == null ) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  private <T extends ConnectionDetails> MetaStoreFactory<T> getMetaStoreFactory( Class<T> clazz ) {
    return new MetaStoreFactory<>( clazz, metaStoreSupplier.get(), NAMESPACE );
  }

  public void setMetastoreSupplier( Supplier<IMetaStore> metaStoreSupplier ) {
    this.metaStoreSupplier = metaStoreSupplier;
  }

  public void addLookupFilter( LookupFilter lookupFilter ) {
    lookupFilters.add( lookupFilter );
  }

  public void addConnectionProvider( String key, ConnectionProvider connectionProvider ) {
    connectionProviders.putIfAbsent( key, connectionProvider );
  }

  public ConnectionProvider getConnectionProvider( String key ) {
    return connectionProviders.get( getLookupKey( key ) );
  }

  private String getLookupKey( String value ) {
    for ( LookupFilter lookupFilter : lookupFilters ) {
      String filterValue = lookupFilter.filter( value );
      if ( filterValue != null ) {
        return filterValue;
      }
    }
    return value;
  }

  public ConnectionDetails getConnectionDetails( String key, String name ) {
    ConnectionProvider connectionProvider = getConnectionProvider( key );
    if ( connectionProvider != null ) {
      Class<? extends ConnectionDetails> clazz = connectionProvider.getClassType();
      try {
        return getMetaStoreFactory( clazz ).loadElement( name );
      } catch ( MetaStoreException mse ) {
        return null;
      }
    }

    return null;
  }

  @SuppressWarnings( "unchecked" )
  public <T extends ConnectionDetails> boolean save( T connectionDetails ) {
    try {
      getMetaStoreFactory( (Class<T>) connectionDetails.getClass() ).saveElement( connectionDetails );
      return true;
    } catch ( MetaStoreException mse ) {
      return false;
    }
  }

  public void delete( String name ) {
    List<ConnectionProvider> providers = Collections.list( connectionProviders.elements() );
    for ( ConnectionProvider provider : providers ) {
      try {
        ConnectionDetails connectionDetails = getMetaStoreFactory( provider.getClassType() ).loadElement( name );
        if ( connectionDetails != null ) {
          getMetaStoreFactory( provider.getClassType() ).deleteElement( name );
        }
      } catch ( MetaStoreException ignored ) {
        // Isn't in that metastore
      }
    }
  }

  public List<ConnectionProvider> getProviders() {
    return Collections.list( this.connectionProviders.elements() );
  }

  public List<ConnectionProvider> getProvidersByType( Class<? extends ConnectionProvider> clazz ) {
    return Collections.list( connectionProviders.elements() ).stream().filter(
      connectionProvider -> clazz.isAssignableFrom( connectionProvider.getClass() )
    ).collect( Collectors.toList() );
  }

  private List<String> getNames( ConnectionProvider provider ) {
    try {
      return getMetaStoreFactory( provider.getClassType() ).getElementNames();
    } catch ( MetaStoreException mse ) {
      return Collections.emptyList();
    }
  }

  public List<String> getNames() {
    List<String> detailNames = new ArrayList<>();
    List<ConnectionProvider> providers = Collections.list( connectionProviders.elements() );
    for ( ConnectionProvider provider : providers ) {
      detailNames.addAll( getNames( provider ) );
    }
    return detailNames;
  }

  public List<String> getNamesByType( Class<? extends ConnectionProvider> clazz ) {
    List<String> detailNames = new ArrayList<>();
    List<ConnectionProvider> providers = Collections.list( connectionProviders.elements() ).stream().filter(
            connectionProvider -> clazz.isAssignableFrom( connectionProvider.getClass() )
    ).collect( Collectors.toList() );
    for ( ConnectionProvider provider : providers ) {
      detailNames.addAll( getNames( provider ) );
    }
    return detailNames;
  }

  public ConnectionDetails loadConnectionDetails( String name ) {
    List<ConnectionProvider> providers = Collections.list( connectionProviders.elements() );
    for ( ConnectionProvider provider : providers ) {
      try {
        ConnectionDetails connectionDetails = getMetaStoreFactory( provider.getClassType() ).loadElement( name );
        if ( connectionDetails != null ) {
          return connectionDetails;
        }
      } catch ( MetaStoreException ignored ) {
        // Isn't in that metastore
      }
    }
    return null;
  }

  public ConnectionDetails getConnectionDetails( String scheme ) {
    try {
      ConnectionProvider provider = connectionProviders.get( scheme );
      return provider.getClassType().newInstance();
    } catch ( InstantiationException | IllegalAccessException e ) {
      return null;
    }
  }

  public List<? extends ConnectionDetails> getConnectionDetailsByScheme( String scheme ) {
    ConnectionProvider provider = connectionProviders.get( scheme );
    try {
      return getMetaStoreFactory( provider.getClassType() ).getElements();
    } catch ( MetaStoreException mse ) {
      return Collections.emptyList();
    }
  }

  public List<Type> getItems() {
    List<Type> types = new ArrayList<>();
    List<ConnectionProvider> providers = Collections.list( connectionProviders.elements() );
    for ( ConnectionProvider provider : providers ) {
      types.add( new ConnectionManager.Type( provider.getKey(), provider.getName() ) );
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
