package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AttendanceAbsentSchedulerJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		long jobId = jc.getJobId();
		
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.SHIFT_ID, jc.getJobId());
		
		Chain chain = TransactionChainFactory.markAbsentChain();
		chain.execute(context);
	}

}
