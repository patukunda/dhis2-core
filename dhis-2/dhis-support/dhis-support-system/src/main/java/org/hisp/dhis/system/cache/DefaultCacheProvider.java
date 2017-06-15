package org.hisp.dhis.system.cache;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

import org.hisp.dhis.commons.util.SystemUtils;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.benmanes.caffeine.cache.Caffeine;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class DefaultCacheProvider
    implements CacheProvider
{
    @Autowired
    private DhisConfigurationProvider dhisConfig;
    
    public <V> Cache<V> getCache( CacheConfig config )
    {        
        if ( dhisConfig.isMemcachedCacheProviderEnabled() )
        {
            return getMemcachedCache( config );
        }
        else
        {
            return getCaffeineCache( config );
        }
    }
    
    private <V> Cache<V> getMemcachedCache( CacheConfig config )
    {
        String servers = dhisConfig.getProperty( ConfigurationKey.CACHE_SERVERS );
        
        try
        {
            MemcachedClient client = new MemcachedClient( AddrUtil.getAddresses( servers ) );
            
            return new MemcachedCache<V>( client, config.getExpirationSeconds() );
        }
        catch ( IOException ex )
        {
            throw new UncheckedIOException( ex );
        }
    }
    
    private <V> Cache<V> getCaffeineCache( CacheConfig config )
    {
        com.github.benmanes.caffeine.cache.Cache<String, V> cache = Caffeine.newBuilder()
            .expireAfterAccess( config.getExpirationSeconds(), TimeUnit.SECONDS )
            .initialCapacity( config.getInitialCapacity() )
            .maximumSize( SystemUtils.isTestRun() ? 0 : config.getMaximumSize() )
            .build();
        
        return new CaffeineCache<V>( cache );
    }
}
