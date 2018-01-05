package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long pmId = jc.getRecordId();
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.RECORD_ID, pmId);
			context.put(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME, jc.getExecutionTime());
			context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIME, jc.getNextExecutionTime());
			
			Chain executePm = FacilioChainFactory.getExecutePreventiveMaintenanceChain();
			executePm.execute(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
