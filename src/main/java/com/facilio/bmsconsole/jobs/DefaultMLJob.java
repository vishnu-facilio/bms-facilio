package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DefaultMLJob extends FacilioJob 
{

	@Override
	public void execute(JobContext jc) throws Exception 
	{
		long mlID = jc.getJobId();
		
		MLContext mlContext = MLUtil.getMlContext(mlID,jc.getOrgId());
		Context context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ML, mlContext);
		
		Chain c = FacilioChainFactory.getMLModelBuildingChain();
		c.execute(context);
	}

}
