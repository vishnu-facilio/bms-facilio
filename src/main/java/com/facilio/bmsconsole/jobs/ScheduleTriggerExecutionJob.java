package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import com.facilio.trigger.context.TriggerFieldRelContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class ScheduleTriggerExecutionJob extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(ScheduleTriggerExecutionJob.class.getName());
    @Override
    public void execute(JobContext jobContext) throws Exception {
        try {
            TriggerFieldRelContext trigger = TriggerUtil.getFieldRel(jobContext.getJobId());

            if (trigger == null || !trigger.isActive()) {
                return;
            }

            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.TRIGGER, trigger);
            context.put(FacilioConstants.Job.JOB_CONTEXT, jobContext);
            FacilioChain executeTrigger = TransactionChainFactory.getScheduleTriggerExecutionChain();
            executeTrigger.execute(context);

        } catch (Exception e) {
            LOGGER.fatal("Error occurred during scheduled trigger execution for job : " + jobContext.getJobId(), e);
            CommonCommandUtil.emailException("ScheduledTriggerExecutionJob", "Error occurred during scheduled trigger execution for job : " + jobContext.getJobId(), e);
        }
    }
}
