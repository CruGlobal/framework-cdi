package org.ccci.framework.cdi.resource;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ccci.framework.cdi.annotations.FactoryFor;
import org.jboss.seam.solder.bean.BeanBuilder;
import org.jboss.seam.solder.bean.Beans;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class ResourceExtension implements Extension
{

    Set<ResourceIdentifier<?>> resourceIdentifiers = Sets.newHashSet();
    
    Logger log = Logger.getLogger(getClass());
    {
        log.setLevel(Level.DEBUG);
    }
    
    <X> void registerGenericBeanProducerMethod(@Observes ProcessBean<X> event, BeanManager beanManager)
    {
        PooledResource pooledResourceAnnotation = event.getAnnotated().getAnnotation(PooledResource.class);
        if (pooledResourceAnnotation != null)
        {
            FactoryFor factoryAnnotation = event.getAnnotated().getAnnotation(FactoryFor.class);
            Preconditions.checkArgument(factoryAnnotation != null, 
                "Bean %s is annotated %s, but is not annotated %s",
                event.getBean(),
                PooledResource.class,
                FactoryFor.class);
            Set<Annotation> qualifiers = Beans.getQualifiers(beanManager, event.getAnnotated().getAnnotations());
            resourceIdentifiers.add(makeIdentifier(qualifiers, factoryAnnotation.value()));
        }
    }

    private <R> ResourceIdentifier<R> makeIdentifier(Set<Annotation> qualifiers, Class<R> value)
    {
        return new ResourceIdentifier<R>(value, qualifiers);
    }
    
    public void addResourceBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager)
    {
        for (ResourceIdentifier<?> resourceIdentifier : resourceIdentifiers)
        {
            Bean<?> resourceBean = createResourceBean(resourceIdentifier, beanManager);
            log.debug("adding resource: " + resourceBean);
            event.addBean(resourceBean);
        }
    }

    private <R> Bean<R> createResourceBean(ResourceIdentifier<R> resourceIdentifier, BeanManager beanManager)
    {
        return new BeanBuilder<R>(beanManager)
            .readFromType(beanManager.createAnnotatedType(resourceIdentifier.getResourceType()))
            .qualifiers(resourceIdentifier.getQualifiers())
            .scope(RequestScoped.class)
            .beanLifecycle(new ResourceBeanLifecycle<R>(resourceIdentifier, beanManager))
            .toString(
                "Resource bean of type " + resourceIdentifier.getResourceType() 
                + " with qualifiers " + resourceIdentifier.getQualifiers())
            .create();
    }
    
}
