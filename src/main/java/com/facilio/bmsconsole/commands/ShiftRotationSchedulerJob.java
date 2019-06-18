package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class ShiftRotationSchedulerJob extends FacilioJob{

	@Override
	public void execute(JobContext jc) throws Exception {
		
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION, jc.getJobId());
		
		Chain chain = TransactionChainFactory.getExecuteShiftRotationCommand();
		chain.execute(context);
		
	}

}
