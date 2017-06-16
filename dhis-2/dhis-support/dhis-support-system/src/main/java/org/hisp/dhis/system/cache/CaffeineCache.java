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

import java.util.function.Function;

import javax.annotation.Nonnull;

/**
 * @author Lars Helge Overland
 */
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
    public V getIfPresent( @Nonnull String key )
    {
        return cache.getIfPresent( key );
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
