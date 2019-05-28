package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;

import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AttendanceAbsentSchedulerJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		
		Chain chain = TransactionChainFactory.markAbsentChain();
	}

}
