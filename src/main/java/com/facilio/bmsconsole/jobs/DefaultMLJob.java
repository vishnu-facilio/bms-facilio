package com.facilio.bmsconsole.jobs;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DefaultMLJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(DefaultMLJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		LOGGER.info("Inside DefaultMLJob");
		List<MLContext> mlContextList = MLUtil.getMlContext(jc);
		for(MLContext mlContext:mlContextList)
		{
			LOGGER.info("mlContext"+mlContext.getId());
			if(mlContext==null)
			{
				continue;
			}
			mlContext.setPredictionTime(jc.getExecutionTime());
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ML, mlContext);
			
			FacilioChain c = FacilioChainFactory.getMLModelBuildingChain();
			c.execute(context);
		}
	}

}
