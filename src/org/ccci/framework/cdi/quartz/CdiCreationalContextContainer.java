package org.ccci.framework.cdi.quartz;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

public class CdiCreationalContextContainer
{
    Map<Job, CreationalContext<?>> contexts = new IdentityHashMap<Job, CreationalContext<?>>();

    public void associateContext(Job job, CreationalContext<?> jobCreationalContext)
    {
        contexts.put(job, jobCreationalContext);
    }
    

    public static CdiCreationalContextContainer retrieve(JobDetail jobDetail)
    {
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String key = CdiCreationalContextContainer.class.getName();
        CdiCreationalContextContainer container = (CdiCreationalContextContainer) jobDataMap.get(key);
        if (container == null)
        {
            container = new CdiCreationalContextContainer();
            jobDataMap.put(key, container);
        }
        return container;
    }


    public void releaseContext(Job jobInstance)
    {
        CreationalContext<?> creationalContext = contexts.get(jobInstance);
        if (creationalContext == null)
        {
            throw new IllegalStateException("There is no creational context associated with job " + jobInstance);
        }
        contexts.remove(jobInstance);
        creationalContext.release();
    }
    
}