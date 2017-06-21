package org.hisp.dhis.system.cache;

/*
 * Copyright (c) 2004-2017, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.hisp.dhis.commons.util.SystemUtils;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.benmanes.caffeine.cache.Caffeine;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * @author Lars Helge Overland
 */
public class DefaultCacheProvider
    implements CacheProvider
{
    @Autowired
    private DhisConfigurationProvider dhisConfig;
    
    public <V extends Serializable> Cache<V> getCache( CacheConfig config )
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
    
    private <V extends Serializable> Cache<V> getMemcachedCache( CacheConfig config )
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
    
    public <V extends Serializable> Cache<V> getCaffeineCache( CacheConfig config )
    {
        com.github.benmanes.caffeine.cache.Cache<String, Optional<V>> cache = Caffeine.newBuilder()
            .expireAfterAccess( config.getExpirationSeconds(), TimeUnit.SECONDS )
            .initialCapacity( config.getInitialCapacity() )
            .maximumSize( SystemUtils.isTestRun() ? 0 : config.getMaximumSize() )
            .build();
        
        return new CaffeineCache<V>( cache );
    }
}
