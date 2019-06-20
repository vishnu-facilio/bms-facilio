package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class NotificationConfigurationTrigger extends FacilioJob{

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
    	Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.NOTIFICATION_JOB_ID, jc.getJobId());
		
		Chain chain = TransactionChainFactory.triggerNotificationChain();
		chain.execute(context);
	}

}
