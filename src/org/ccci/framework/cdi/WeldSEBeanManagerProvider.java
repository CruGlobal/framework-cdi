package org.ccci.framework.cdi;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.solder.beanManager.BeanManagerProvider;

public class WeldSEBeanManagerProvider implements BeanManagerProvider
{

    @Override
    public int getPrecedence()
    {
        return 5;
    }

    @Override
    public BeanManager getBeanManager()
    {
        try
        {
            return CdiContainerManager.getInstance().getBeanManager();
        }
        catch (IllegalStateException e)
        {
            return null;
        }
      
    }

}
