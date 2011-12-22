package org.ccci.framework.cdi.weld;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.jsp.JspFactory;

import org.jboss.weld.servlet.api.ServletListener;

public class WeldWorkaroundJspFactoryRestorationListener implements ServletListener
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
        JspFactory defaultFactory = (JspFactory) servletContext.getAttribute(DEFAULT_JSP_FACTORY_LOCATION);
        if (defaultFactory != null)
        {
            JspFactory.setDefaultFactory(defaultFactory);
        }
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
