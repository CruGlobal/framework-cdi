package org.ccci.framework.cdi;

import javax.inject.Inject;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.Unbound;

public class CdiContextManager
{

    @Inject @Unbound RequestContext requestContext;
    
    public void beginThreadScopedRequestContext()
    {
        requestContext.activate();
    }
    
    public void endThreadScopedRequestContext()
    {
        requestContext.invalidate();
        requestContext.deactivate();
    }
}
