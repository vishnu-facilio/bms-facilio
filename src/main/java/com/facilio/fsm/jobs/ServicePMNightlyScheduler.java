package com.facilio.fsm.jobs;

import com.facilio.chain.FacilioChain;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import lombok.extern.log4j.Log4j;

@Log4j
public class ServicePMNightlyScheduler extends FacilioJob {

    @Override
    public void execute(JobContext jc) throws Exception {
        try{
            FacilioChain chain = FsmTransactionChainFactoryV3.servicePMNightlySchedulerChain();
            chain.execute();
        }catch(Exception e){
            LOGGER.error("Error while running service pm night scheduler",e);
        }
    }
}
