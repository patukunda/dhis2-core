package org.hisp.dhis.system.cache;

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.hisp.dhis.common.CodeGenerator;

import net.spy.memcached.MemcachedClient;

public class MemcachedCache<V>
    implements Cache<V>
{
    private static final String SEP = ":";
    
    private MemcachedClient cache;
    private int expiration;
    private String namespace;
    
    public MemcachedCache( MemcachedClient cache, int expiration )
    {
        this.cache = cache;
        this.expiration = expiration;
        this.namespace = CodeGenerator.generateCode( 5 );
    }

    @Override
    public void put( String key, @Nonnull V value )
    {        
        cache.set( getKey( key ), expiration, value );
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get( String key, Function<String, ? extends V> mappingFunction )
    {
        String nsKey = getKey( key );
        
        V value = (V) cache.get( nsKey );
        
        if ( value != null )
        {            
            return value;
        }
        else
        {
            value = mappingFunction.apply( nsKey );
            
            if ( value != null )
            {
                cache.set( nsKey, expiration, value );
            }
            
            return value;
        }
    }

    @Override
    public void invalidate( String key )
    {
        cache.delete( getKey( key ) );        
    }

    @Override
    public void invalidateAll()
    {
        throw new UnsupportedOperationException();
    }

    private String getKey( String key )
    {
        return namespace + SEP + key;
    }    
}
