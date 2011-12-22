package org.ccci.framework.cdi.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Default;
import javax.inject.Qualifier;

import org.apache.commons.pool.PoolableObjectFactory;
import org.ccci.framework.cdi.resource.PooledResource;
import org.jboss.seam.solder.core.Veto;
  
/**
 * A qualifier for {@link PoolableObjectFactory}s that is needed for pooling resources.
 * See {@link PooledResource} for more information. 
 * 
 * @author Nathan Kopp
 * @author Matt Drees
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target({FIELD,METHOD,PARAMETER,TYPE})  
@Qualifier  
@Documented
public @interface FactoryFor {
    
    /**
     * Indicates what type of resource this factory produces.  This type should be either:
     * <ul>
     *  <li>an interface</li>
     *  <li>a concrete class that is proxyable (as far as CDI is concerned), and has either:
     *   <ul>
     *    <li>a {@link Veto} annotation, or</li>
     *    <li>a non-{@link Default} qualifier</li>
     *   </ul>
     *  </li>
     */
    Class<?> value();
}
