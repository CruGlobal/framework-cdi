package org.ccci.framework.cdi.resource;

public interface ResourcePool
{

    public Object borrowResource();

    public void returnResource(Object resource);

    public int getNumActive();

}
