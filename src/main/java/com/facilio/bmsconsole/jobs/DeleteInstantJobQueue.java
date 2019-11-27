package com.facilio.bmsconsole.jobs;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.DeleteMessageQueueJobsCommand;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DeleteInstantJobQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteInstantJobQueue.class.getName());
    private static final String TABLE_NAME = "FacilioInstantJobQueue";
    @Override
    public void execute(JobContext jc) throws Exception {

    	long startTime = (System.currentTimeMillis() -(1 * 24 * 60 * 60 * 1000));
        String deleteCondition = String.valueOf(startTime);
        try{
            int count =  DeleteMessageQueueJobsCommand.deleteQueue(TABLE_NAME, deleteCondition);
            LOGGER.info("FacilioInstantJobQueue deleted queue count is  : "+count);
        }catch(Exception e){
            LOGGER.info("Exception occurred in FacilioInstantJob deletedJob  :  ",e);
        }

    }



}
