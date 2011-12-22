package org.ccci.framework.cdi;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.log4j.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Boots up a CDI environment in a JavaSE environment (e.g. tests, command line utilities).  
 * Not used when running as a webapp.
 * 
 * Currently uses Weld as its CDI implementation.
 * 
 * @author Nathan Kopp
 * @author Matt Drees
 */
public class CdiContainerManager
{
    private static CdiContainerManager instance = new CdiContainerManager(); 
    
    public static CdiContainerManager getInstance()
    {
        return instance;
    }
    
    private CdiContainerManager() { }
    
    private Weld weld;
    
    private Logger log = Logger.getLogger(getClass());

    private WeldContainer container;
    
    public synchronized void init()
    {
        if (weld != null)
	    {
		    log.warn("Weld is already initialized");
		    return;
	    }
		weld = new Weld();
		container = weld.initialize();
    }
    
    public synchronized void shutdown()
    {
        if(weld==null)
        {
            log.warn("Weld is not running");
            return;
        }
        weld.shutdown();
        weld = null;
    }
    
    public BeanManager getBeanManager()
    {
        if (weld == null)
        {
            throw new IllegalStateException("Weld is not running");
        }
        return container.getBeanManager();
    }
    
}
