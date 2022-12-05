package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class BaseSchedulerSingleInstanceJob extends FacilioJob {

	@Override
	public void execute(JobContext jc) throws Exception {
		FacilioChain baseScheculeJobChain = TransactionChainFactoryV3.BaseSchedulerSingleInstanceJobChain();
		FacilioContext context = baseScheculeJobChain.getContext();
		context.put(FacilioConstants.ContextNames.JOB_ID, jc.getJobId());
		context.put(FacilioConstants.ContextNames.JOB_NAME, jc.getJobName());
		baseScheculeJobChain.execute();
	}
}
