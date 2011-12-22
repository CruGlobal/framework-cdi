package org.ccci.framework.cdi.weld;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.jsp.JspFactory;

import org.jboss.weld.environment.servlet.Listener;
import org.jboss.weld.servlet.api.ServletListener;

/**
 * So, it seems some versions of tomcat 5.5 (apparently 5.5.20) have a default JspFactory available 
 * during context initialization, but others (5.5.23) do not.   If Weld finds a default JspFactory, 
 * it tries to call a method that doesn't exist in Tomcat 5.5, which causes a java.lang.NoSuchMethodError.
 * So, as a workaround, we just try to make sure the default JspFactory is unavailable during the Weld
 * {@link Listener listener's} initialization.  This listener removes the default JspFactory and
 * {@link WeldWorkaroundJspFactoryRestorationListener} restores it, if it was available in the first
 * place.
 * 
 * This is only a problem because weld doesn't officially support tomcat 5.5.
 * 
 * @author Matt Drees
 */
public class WeldWorkaroundJspFactoryRemovalListener implements ServletListener
{

    public static final String DEFAULT_JSP_FACTORY_LOCATION = "org.ccci.framework.cdi.weld.DefaultJspFactory";

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext servletContext = servletContextEvent.getServletContext();
        
        JspFactory defaultFactory = JspFactory.getDefaultFactory();
        servletContext.setAttribute(DEFAULT_JSP_FACTORY_LOCATION, defaultFactory);
        JspFactory.setDefaultFactory(null);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent)
    {
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent)
    {
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent)
    {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent)
    {
    }

}
