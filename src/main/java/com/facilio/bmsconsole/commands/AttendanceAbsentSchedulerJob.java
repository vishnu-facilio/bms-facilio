package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class AttendanceAbsentSchedulerJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT_ID, jc.getJobId());
		FacilioChain chain = TransactionChainFactory.markAbsentChain();
		chain.execute(context);
	}

}
