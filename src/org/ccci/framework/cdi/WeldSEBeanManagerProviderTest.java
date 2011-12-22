package org.ccci.framework.cdi;

import java.util.ServiceLoader;

import junit.framework.TestCase;

import org.jboss.seam.solder.beanManager.BeanManagerProvider;

public class WeldSEBeanManagerProviderTest extends TestCase
{
    
    public void testWeldSEBeanManagerProvider()
    {
        ServiceLoader<BeanManagerProvider> services = ServiceLoader.load(BeanManagerProvider.class);
        boolean found = false;
        for (BeanManagerProvider provider : services)
        {
            if (provider instanceof WeldSEBeanManagerProvider)
            {
                found = true;
            }
        }
        
        assertTrue("did not find WeldSEBeanManagerProvider", found);
    }

}
