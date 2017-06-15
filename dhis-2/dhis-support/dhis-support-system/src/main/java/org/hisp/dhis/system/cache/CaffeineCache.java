package org.hisp.dhis.system.cache;

import java.util.function.Function;

import javax.annotation.Nonnull;

public class CaffeineCache<V>
    implements Cache<V>
{
    private com.github.benmanes.caffeine.cache.Cache<String, V> cache = null;

    public CaffeineCache( com.github.benmanes.caffeine.cache.Cache<String, V> cache )
    {
        this.cache = cache;
    }
    
    public void put( String key, @Nonnull V value )
    {
        cache.put( key, value );
    }
    
    @Override
    public V get( String key, Function<String, ? extends V> mappingFunction )
    {
        return cache.get( key, mappingFunction );
    }

    @Override
    public void invalidate( String key )
    {
        cache.invalidate( key );        
    }

    @Override
    public void invalidateAll()
    {
        cache.invalidateAll();        
    }
}
