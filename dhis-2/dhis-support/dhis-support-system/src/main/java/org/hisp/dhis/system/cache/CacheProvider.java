package org.hisp.dhis.system.cache;

public interface CacheProvider
{
    <V> Cache<V> getCache( CacheConfig config );
}
