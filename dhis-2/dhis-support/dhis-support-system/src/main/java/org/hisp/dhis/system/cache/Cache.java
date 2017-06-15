package org.hisp.dhis.system.cache;

import java.util.function.Function;

import javax.annotation.Nonnull;

public interface Cache<V>
{
    void put( String key, @Nonnull V value);
    
    V get( @Nonnull String key, @Nonnull Function<String, ? extends V> mappingFunction );

    void invalidate( @Nonnull String key );

    void invalidateAll();
}
