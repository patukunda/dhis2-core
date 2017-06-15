package org.hisp.dhis.system.cache;

public class CacheConfig
{
    private int expirationSeconds = 600;
        
    private int initialCapacity = 0;

    private long maximumSize = 10000;
    
    public CacheConfig withExpiration( int expirationSeconds )
    {
        this.expirationSeconds = expirationSeconds;
        return this;
    }
    
    public CacheConfig withInitialCapacity( int initialCapacity )
    {
        this.initialCapacity = initialCapacity;
        return this;
    }
    
    public CacheConfig withMaximumSize( long maximumSize )
    {
        this.maximumSize = maximumSize;
        return this;
    }

    public int getExpirationSeconds()
    {
        return expirationSeconds;
    }

    public int getInitialCapacity()
    {
        return initialCapacity;
    }

    public long getMaximumSize()
    {
        return maximumSize;
    }
}
