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
 * Cache abstraction over various cache implementations, such as local in-memory caches
 * such as {@code Caffeine} and remote server-based caches such as {@code memcached}.
 * <p>
 * This interface is borrowing heavily from {@link com.github.benmanes.caffeine.cache.Cache}.
 * 
 * @author Lars Helge Overland
 */
public interface Cache<V>
{
    /**
     * Associates {@code value} with {@code key} in this cache. If the cache previously contained a
     * value associated with {@code key}, the old value is replaced by {@code value}.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     */
    void put( @Nonnull String key, @Nonnull V value );
    
    /**
     * Returns the value associated with {@code key} in this cache, or {@code null} if there is no
     * cached value for {@code key}.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no
     *         mapping for the key.
     */
    V getIfPresent( @Nonnull String key );

    /**
     * Returns the value associated with {@code key} in this cache, obtaining that value from
     * {@code mappingFunction} if necessary.
     * 
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the specified key, or null if
     *         the computed value is null
     */  
    V get( @Nonnull String key, @Nonnull Function<String, ? extends V> mappingFunction );

    /**
     * Discards any cached value for key {@code key}. The behavior of this operation is undefined for
     * an entry that is being loaded and is otherwise not present.
     *
     * @param key key whose mapping is to be removed from the cache.
     */
    void invalidate( @Nonnull String key );

    /**
     * Discards all entries in the cache. The behavior of this operation is undefined for an entry
     * that is being loaded and is otherwise not present.
     */
    void invalidateAll();
}
