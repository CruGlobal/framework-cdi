package org.ccci.framework.cdi.quartz;

import org.ccci.framework.cdi.CdiContextManager;
import org.ccci.framework.cdi.CdiHelper;
import org.ccci.framework.cdi.NonManagedInstance;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.impl.StdSchedulerFactory;

public class CdiJobListener implements JobListener
{
    
    CdiHelper helper = new CdiHelper();
    private String name;

    @Override
    public String getName()
    {
        return name;
    }
    
    /** called by {@link StdSchedulerFactory}, with the name configured in the quartz properties file */
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context)
    {
        destroyJob(context);
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context)
    {
        beginRequestContext();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException executionException)
    {
        endRequestContext();
        destroyJob(context);
    }

    private void beginRequestContext()
    {
        NonManagedInstance<CdiContextManager> contextManager = helper.createNonManagedInstance(CdiContextManager.class);
        try
        {
            contextManager.get().beginThreadScopedRequestContext();
        }
        finally
        {
            contextManager.destroy();
        }
    }

    private void endRequestContext()
    {
        NonManagedInstance<CdiContextManager> contextManager = helper.createNonManagedInstance(CdiContextManager.class);
        try
        {
            contextManager.get().endThreadScopedRequestContext();
        }
        finally
        {
            contextManager.destroy();
        }
    }

    private void destroyJob(JobExecutionContext context)
    {
        CdiCreationalContextContainer container = CdiCreationalContextContainer.retrieve(context.getJobDetail());
        container.releaseContext(context.getJobInstance());
    }
}
