package org.ccci.framework.cdi.resource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.pool.PoolableObjectFactory;
import org.ccci.framework.cdi.CdiContextManager;
import org.ccci.framework.cdi.annotations.FactoryFor;
import org.ccci.framework.cdi.quartz.CdiJobListener;
import org.jboss.seam.solder.bean.generic.GenericType;
import org.quartz.Job;


/**
 * Indicates that the annotated {@link PoolableObjectFactory} bean produces resource objects that
 * should be pooled.  
 * 
 * The factory bean should also be annotated with a &#64{@link FactoryFor} qualifier annotation, whose 
 * {@link FactoryFor#value() value} attribute indicates the type (i.e. class) of the resource that the factory
 * produces.  Note the documented restrictions for the resource type.
 * 
 * When this annotation is present on such a factory bean, two other beans will automatically be registered
 * with the CDI container.  Both will have the same qualifiers as the factory (including the {@link FactoryFor}
 * qualifier).  One bean will be an application-scoped {@link ResourcePool}, and the other bean will be a 
 * request-scoped bean with the resource type identified by the {@link FactoryFor} annotation.  Both beans
 * can be injected into any injection-capable component (including {@link Page}s, {@link FwContext} subclasses,
 * {@link AuthServlet} subclasses, and of course, all CDI-managed beans).  Note that even if the resource 
 * implementation is not threadsafe (which will be the norm, otherwise pooling is pointless), the resource
 * bean can be injected into a multithreaded object (such as a servlet), without concurrency problems, because
 * the injected bean is only a proxy to the appropriate (request-scoped) resource.
 * 
 * One implication of this is resources can only be used when a request context is active.  Currently this 
 * includes:
 * <ul>
 *  <li>the lifecyle of an {@link HttpServletRequest}</li>
 *  <li>
 *   the firing of a quartz {@link Job#execute(org.quartz.JobExecutionContext) job execution} 
 *   (as long as {@link CdiJobListener} is registered with the container)
 *  </li>
 * </ul>
 * If you need a request context active in some other scenario, see {@link CdiContextManager}.
 * 
 * Certain pool attributes can be configured in servlets.properties for this resource pool.  See 
 * {@link ResourcePoolImpl#init()} for more specifics.
 * 
 * 
 * @author Matt Drees
 */
@Retention(RetentionPolicy.RUNTIME)
@GenericType(PoolableObjectFactory.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface PooledResource
{
    /**
     * The name of the pool.  Used in configuration properties in servlets.properties.  As an example,
     * if the value of this attribute is 'foo', you can configure the max pool size with the property
     * {@code default.pool.foo.maxActive}.
     */
    String value();
}
