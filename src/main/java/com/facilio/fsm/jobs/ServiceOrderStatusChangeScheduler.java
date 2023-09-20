package com.facilio.fsm.jobs;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.jobs.ScheduleWOStatusChange;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ServiceOrderStatusChangeScheduler extends FacilioJob {
    private static final Logger LOGGER = LogManager.getLogger(ScheduleWOStatusChange.class.getName());
    @Override
    public void execute(JobContext jc) throws Exception {
        try{
            Long maxTime = jc.getNextExecutionTime()*1000;
            FacilioChain chain = FsmTransactionChainFactoryV3.getServiceOrderStatusChangeChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.END_TIME, maxTime);

            chain.execute();
        }
        catch(Exception e){
            CommonCommandUtil.emailException("ServiceOrderStatusChangeScheduler", ""+jc.getJobId(), e);
            LOGGER.error("#" + jc.getJobId() + " Service PM Execution failed",e);
        }
    }
}
