package org.ccci.framework.cdi.resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ccci.framework.cdi.services.ApplicationProperties;
import org.jboss.seam.solder.bean.generic.Generic;
import org.jboss.seam.solder.bean.generic.GenericConfiguration;

import com.google.common.base.Throwables;

@GenericConfiguration(PooledResource.class)
@ApplicationScoped
public class ResourcePoolImpl implements ResourcePool
{


    @Inject 
    @Generic 
    PooledResource configurationAnnotation;
    
    @Inject 
    @Generic 
    PoolableObjectFactory factory; 
    
    @Inject 
    ApplicationProperties props;
    
    
    private static final String DEFAULT_IDLE_TIMEOUT = "300000";
    private static final String DEFAULT_MIN_IDLE = "2";
    private static final String DEFAULT_MAX_IDLE = "5";
    private static final String DEFAULT_MAX_ACTIVE = "15";
    private static final String DEFAULT_WHEN_EXHAUSTED = "FAIL";
    private static final String DEFAULT_VALIDATE_ON_BORROW = "true";
    private static final String DEFAULT_EVICTION_PERIOD = String.valueOf(TimeUnit.SECONDS.toMillis(5));


    private GenericObjectPool pool;
    
    // this is an IdentityHashMap b/c regular HashMaps/HashTables don't handle proxied objects.
    private Map<Object, AllocationRecord> allActiveObjects = new IdentityHashMap<Object, AllocationRecord>();

    private Logger log = Logger.getLogger(getClass());
    
    
    private static class AllocationRecord
    {
        final Throwable stacktrace = new Throwable("object allocated here");
        
        @Override
        public String toString()
        {
            StringWriter sw = new StringWriter();
            stacktrace.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }
    
    
    @PostConstruct
    void init()
    {
        log.setLevel(Level.DEBUG);
        
        pool = new GenericObjectPool(factory);
        
        int maxActive = Integer.parseInt(getProperty("maxActive", DEFAULT_MAX_ACTIVE)); 
        int maxIdle = Integer.parseInt(getProperty("maxIdle",DEFAULT_MAX_IDLE));
        int minIdle = Integer.parseInt(getProperty("minIdle",DEFAULT_MIN_IDLE));
        int idleTimeout = Integer.parseInt(getProperty("idleTimeout",DEFAULT_IDLE_TIMEOUT));
        String whenExhausted = getProperty("whenExhausted",DEFAULT_WHEN_EXHAUSTED);
        boolean testOnBorrow = Boolean.parseBoolean(getProperty("testOnBorrow",DEFAULT_VALIDATE_ON_BORROW));
        long evictionPeriod = Long.parseLong(getProperty("evictionPeriod",DEFAULT_EVICTION_PERIOD));
        pool.setMaxActive(maxActive);
        pool.setMaxIdle(maxIdle);
        pool.setMinIdle(minIdle);
        pool.setMinEvictableIdleTimeMillis(idleTimeout);
        pool.setTestOnBorrow(testOnBorrow);
        pool.setTimeBetweenEvictionRunsMillis(evictionPeriod);
        // decode the "whenExhausted" setting
        if(whenExhausted.equals("FAIL"))
            pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        else if (whenExhausted.equals("GROW"))
            pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
        else if (whenExhausted.equals("BLOCK"))
            pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        else
            throw new IllegalArgumentException("Setting for " + getPoolName() +".pool.whenExhausted"+" in application properties must be one of: GROW, FAIL, BLOCK");
    }
    
    @PreDestroy
    public void shutdown()
    {
        try
        {
            pool.close();
        }
        catch (Exception e)
        {
            log.warn("exception closing pool " + getPoolName() + "; ignoring", e);
        }
    }
    
    private String getProperty(String propertyName, String defaultValue)
    {
        //TODO: maybe read from 'application' instead of default.  It's not overrideable, as 'default' implies.
        String module = "default";
        return props.get(module, "pool."+ getPoolName() +"." + propertyName, defaultValue);
    }


    @Override
    public Object borrowResource()
    {
        try
        {
            return getObjectFromPool();
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
    }
    
    private Object getObjectFromPool() throws Exception
    {
        //Note: this message may not be accurate if two threads concurrently access this method.  
        //It's a debug output, so it's not a biggie.
        log.debug("In " + getPoolName() + ", just allocated #" + pool.getNumActive());      
        Object instance = pool.borrowObject();
        recordActiveObject(instance);
        return instance;
    }

    private String getPoolName()
    {
        return configurationAnnotation.value();
    }

    private void recordActiveObject(Object instance)
    {
        allActiveObjects.put(instance, new AllocationRecord());
    }

    

    @Override
    public void returnResource(Object resource)
    {
        // we have to be careful about only calling pool.returnObject() for objects that
        // have not already been returned.
        if(allActiveObjects.remove(resource) == null) 
        {
            throw new IllegalStateException("Resource " + resource + " is not active.  It was probably already checked in.");
        }

        try
        {
            pool.returnObject(resource);
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }
        log.debug("In " + getPoolName() + ", just returned #" + pool.getNumActive());    

    }


    @Override
    public int getNumActive()
    {
        return pool.getNumActive();
    }

}
