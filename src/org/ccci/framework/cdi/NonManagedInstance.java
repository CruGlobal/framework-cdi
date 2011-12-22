package org.ccci.framework.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Represents an instance obtained from CDI but not managed by CDI.  It needs
 * to be manually {@link #destroy() destroyed } when it is no longer needed.
 * 
 * Created via {@link CdiHelper#createNonManagedInstance(Class)}.
 * 
 * @param <T> the type of the instance
 * 
 * @author Matt Drees
 */
public class NonManagedInstance<T>
{

    private final InjectionTarget<T> injectionTarget;
    private final T instance;
    private final CreationalContext<T> creationalContext;

    NonManagedInstance(T instance, InjectionTarget<T> injectionTarget, CreationalContext<T> creationalContext)
    {
        this.instance = instance;
        this.injectionTarget = injectionTarget;
        this.creationalContext = creationalContext;
    }

    public T get()
    {
        return instance;
    }

    public void destroy()
    {
        try
        {
            injectionTarget.dispose(instance);
        }
        finally
        {
            creationalContext.release();
        }
    }

}
