package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.DeleteMessageQueueJobsCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.tasker.FacilioInstantJobScheduler;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class DeleteInstantJobQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteInstantJobQueue.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {

        try{
            FacilioInstantJobScheduler.deleteExecutorsInstantJobQueueTable();
        }catch(Exception e){
        	CommonCommandUtil.emailException("FacilioInstantJobDeletion", "DeletionJob Failed - jobid -- "+jc.getJobId(), e);
        	LOGGER.info("Exception occurred in FacilioInstantJobDeletion  :  ",e);
        }

    }

}
