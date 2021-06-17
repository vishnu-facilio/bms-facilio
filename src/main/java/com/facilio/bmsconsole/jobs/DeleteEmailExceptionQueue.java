package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.queue.FacilioQueueException;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class DeleteEmailExceptionQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteEmailExceptionQueue.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
    	
    	long startTime = (System.currentTimeMillis() -(1 * 24 * 60 * 60 * 1000));
        try{
        	FacilioQueueException.deleteExceptionQueue(startTime);
        }catch(Exception e){
        	LOGGER.error("Exception occurred in FacilioExceptionQueue deletedJob  :  ",e);
        	CommonCommandUtil.emailException("DeleteEmailExceptionQueue", "DeleteEmailExceptionQueue Failed - orgid -- "+jc.getJobId(), e);
        }

    }

}
