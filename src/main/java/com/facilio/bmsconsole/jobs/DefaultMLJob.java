package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class DefaultMLJob extends FacilioJob 
{
	private static final Logger LOGGER = Logger.getLogger(DefaultMLJob.class.getName());
	@Override
	public void execute(JobContext jc) throws Exception 
	{	
		try{
			LOGGER.info("Inside DefaultMLJob, JOB ID :"+jc.getJobId());
			List<MLContext> mlContextList = MLUtil.getMlContext(jc);
			List<MLContext> filteredmlContextList = mlContextList.stream().filter(ml->ml!=null).collect(Collectors.toList());
			
			for(MLContext mlContext:filteredmlContextList)
			{
				LOGGER.info("Starting "+ mlContext.getModelPath() + " ML ID : "+mlContext.getId());
				LOGGER.info("mlContext"+mlContext.getId());
				mlContext.setPredictionTime(jc.getExecutionTime()*1000);
				FacilioChain chain = FacilioChainFactory.getMLModelBuildingChain();
				FacilioContext context = chain.getContext();
				context.put(FacilioConstants.ContextNames.ML, mlContext);
				chain.execute();
				LOGGER.info("Completing "+ mlContext.getModelPath() + " ML ID : "+mlContext.getId());
			}
			LOGGER.info("Finished DefaultMLJob, JOB ID :"+jc.getJobId());
		}catch(Exception e){
			LOGGER.fatal("Error_in DefaultMLJob"+e);
		}
	}

}
