package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.DeleteMessageQueueJobsCommand;
import com.facilio.service.FacilioService;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DeleteInstantJobQueue extends FacilioJob {

    private static final Logger LOGGER = LogManager.getLogger(DeleteInstantJobQueue.class.getName());
    private static final String TABLE_NAME = "FacilioInstantJobQueue";
    private static final String DELETE_CONDITION = "< NOW() - INTERVAL 7 DAY";
    @Override
    public void execute(JobContext jc) throws Exception {


        try{
            int count =  FacilioService.runAsServiceWihReturn(() -> DeleteMessageQueueJobsCommand.deleteQueue(TABLE_NAME, DELETE_CONDITION));
            LOGGER.info("FacilioInstantJobQueue deleted queue count is  : "+count);
        }catch(Exception e){
            LOGGER.info("Exception occurred in FacilioInstantJob deletedJob  :  ",e);
        }

    }



}
