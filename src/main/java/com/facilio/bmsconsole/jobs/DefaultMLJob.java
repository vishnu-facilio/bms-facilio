package com.facilio.bmsconsole.jobs;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import java.util.List;

public class DefaultMLJob extends FacilioJob 
{

	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		List<MLContext> mlContextList = MLUtil.getMlContext(jc);
		for(MLContext mlContext:mlContextList)
		{
			if(mlContext==null)
			{
				continue;
			}
			mlContext.setPredictionTime(jc.getExecutionTime());
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ML, mlContext);
			
			Chain c = FacilioChainFactory.getMLModelBuildingChain();
			c.execute(context);
		}
	}

}
