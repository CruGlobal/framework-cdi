package org.ccci.framework.cdi.quartz;


import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.ccci.framework.cdi.CdiHelper;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class CdiJobFactory implements JobFactory
{

    CdiHelper helper = new CdiHelper();
    
    @Override
    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException
    {
        
        JobDetail jobDetail = bundle.getJobDetail();
        
        @SuppressWarnings("unchecked") //it'll be an implementation class of Job
        Class<? extends Job> jobClass = jobDetail.getJobClass();
        try 
        {
            Bean<?> bean = helper.resolveBean(jobClass);
            CreationalContext<?> jobCreationalContext = helper.getBeanManager().createCreationalContext(bean);
            
            Job job = helper.lookup(jobClass, jobCreationalContext);
            
            CdiCreationalContextContainer container = CdiCreationalContextContainer.retrieve(jobDetail);
            container.associateContext(job, jobCreationalContext);
            return job;
            
        } catch (Exception e) {
            SchedulerException se = new SchedulerException(
                    "Problem instantiating class '"
                            + jobDetail.getJobClass().getName() + "'", e);
            throw se;
        }
    }


}
