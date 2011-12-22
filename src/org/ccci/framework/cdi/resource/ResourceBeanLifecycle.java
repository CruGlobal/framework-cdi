package org.ccci.framework.cdi.resource;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.ccci.framework.cdi.CdiHelper;
import org.jboss.seam.solder.bean.ContextualLifecycle;

public class ResourceBeanLifecycle<X> implements ContextualLifecycle<X>
{

    private final BeanManager beanManager;
    private final ResourceIdentifier<X> resourceIdentifier;

    private final CdiHelper helper = new CdiHelper();
    
    public ResourceBeanLifecycle(ResourceIdentifier<X> resourceIdentifier, BeanManager beanManager)
    {
        this.resourceIdentifier = resourceIdentifier;
        this.beanManager = beanManager;
    }

    @Override
    public X create(Bean<X> bean, CreationalContext<X> creationalContext)
    {
        ResourcePool resourcePool = helper.lookup(beanManager, ResourcePoolImpl.class, creationalContext, resourceIdentifier.getQualifiers().toArray(new Annotation[]{}));
        return resourceIdentifier.getResourceType().cast(resourcePool.borrowResource());
    }

    @Override
    public void destroy(Bean<X> bean, X instance, CreationalContext<X> creationalContext)
    {
        ResourcePool resourcePool = helper.lookup(beanManager, ResourcePoolImpl.class, creationalContext, resourceIdentifier.getQualifiers().toArray(new Annotation[]{}));
        resourcePool.returnResource(instance);
        creationalContext.release();
    }

}
