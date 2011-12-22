package org.ccci.framework.cdi;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.seam.solder.beanManager.BeanManagerLocator;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class CdiHelper
{

    /**
     * Performs CDI Injection into the given instance.  
     * @param instance to have its dependencies injected
     * @return a {@link CreationalContext} instance that should be {@link CreationalContext#release() released}
     * when the instance is no longer being used.
     */
    public <T> CreationalContext<T> inject(T instance)
    {
        @SuppressWarnings("unchecked") //cast from Class<? extends T> down to Class<T> because we know 
        // that instance *is* a T, not a subclass of T
        Class<T> clazz = (Class<T>) instance.getClass();
        
        BeanManager beanManager = getBeanManager();
        InjectionTarget<T> it = getInjectionTarget(clazz);
        CreationalContext<T> cc = beanManager.createCreationalContext(null);
        it.inject(instance, cc);
        
        return cc;
    }

    public <T> NonManagedInstance<T> createNonManagedInstance(Class<T> class1)
    {
        BeanManager beanManager = getBeanManager();
        InjectionTarget<T> it =  getInjectionTarget(class1);
        CreationalContext<T> cc = beanManager.createCreationalContext(null);
        T instance = it.produce(cc);
        it.inject(instance, cc);
        it.postConstruct(instance);
        return new NonManagedInstance<T>(instance, it, cc);
    }
    

    public <T> T lookup(Class<? extends T> clazz, CreationalContext<?> context, Annotation... annotations)
    {
        return lookup(getBeanManager(), clazz, context, annotations);
    }

    public <T> T lookup(BeanManager bm, Class<? extends T> clazz, CreationalContext<?> context,
                         Annotation... annotations)
    {
        if(clazz==null)
        {
            throw new NullPointerException("clazz is required");
        }
        if(context==null)
        {
            throw new NullPointerException("creational context is required");
        }
        
        
        Bean<T> bean = resolveBean(bm, clazz, annotations);
        if(bean==null)
        {
            return null;
        }
        
        @SuppressWarnings("unchecked") //getReference is sadly generics-unfriendly
        T object = (T) bm.getReference(bean, clazz, context);
        
        return object;
    }

    private <T> Bean<T> resolveBean(BeanManager bm, Class<? extends T> clazz, Annotation... annotations)
    {
        Set<Bean<?>> beans = bm.getBeans(clazz, annotations);
        if(beans.size()==0)
        {
            return null;
        }
        @SuppressWarnings("unchecked")
        Bean<T> bean = (Bean<T>) bm.resolve(beans);
        return bean;
    }


    public <T> Bean<T> resolveBean(Class<T> type, Annotation... annotations)
    {
        return resolveBean(getBeanManager(), type, annotations);
    }
    
    
    private BeanManager beanManager;
    
    public synchronized BeanManager getBeanManager()
    {
        if (beanManager == null)
        {
            beanManager = new BeanManagerLocator().getBeanManager();
            if (beanManager == null)
            {
                throw new IllegalStateException("Cannot find BeanManager!");
            }
        }
        return beanManager;
    }

    @SuppressWarnings("unchecked")
    private <T> InjectionTarget<T> getInjectionTarget(Class<T> clazz)
    {
        return (InjectionTarget<T>) cache.get(clazz);
    }

    Map<Class<?>, InjectionTarget<?>> cache = new MapMaker()
        .makeComputingMap(new Function<Class<?>, InjectionTarget<?>>()
        {
            @Override
            public InjectionTarget<?> apply(Class<?> clazz)
            {
                return getActualInjectionTarget(clazz);
            }
        });
    
    private <T> InjectionTarget<T> getActualInjectionTarget(Class<T> clazz)
    {
        BeanManager manager = getBeanManager();
        return manager.createInjectionTarget(manager.createAnnotatedType(clazz));
    }

}
