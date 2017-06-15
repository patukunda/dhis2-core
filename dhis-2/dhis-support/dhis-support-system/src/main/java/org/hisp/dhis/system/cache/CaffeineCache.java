package org.hisp.dhis.system.cache;

import java.util.function.Function;

public class CaffeineCache<V>
    implements Cache<V>
{
    private com.github.benmanes.caffeine.cache.Cache<String, V> cache;

    public CaffeineCache( com.github.benmanes.caffeine.cache.Cache<String, V> cache )
    {
        this.cache = cache;
    }

    @Override
    public void put( String key, V value )
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
