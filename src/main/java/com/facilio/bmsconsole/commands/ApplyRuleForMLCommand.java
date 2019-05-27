package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.job.JobContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class ApplyRuleForMLCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(ApplyRuleForMLCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception 
	{
		
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		executeAnotherJob(mlContext);
		
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(mlContext.getRuleID(),true,true,true);
		if (rule != null && rule.isActive()) 
		{
			FacilioContext ruleContext = new FacilioContext();
			WorkflowRuleAPI.executeScheduledRule(rule, mlContext.getPredictionTime() * 1000 , ruleContext);
		}
		
		return false;
		
	}
	
	private void executeAnotherJob(MLContext mlContext)
	{
		String jobid = mlContext.getMLModelVariable("jobid");
		try
		{
			if(jobid!=null)
			{
				LOGGER.info("Executing Job "+jobid);
				JobContext jobContext = FacilioTimer.getJob(FacilioUtil.parseLong(jobid), "DefaultMLJob");
				if(jobContext!=null)
				{
					FacilioTimer.deleteJob(FacilioUtil.parseLong(jobid), "DefaultMLJob");
				}
				
				FacilioTimer.scheduleOneTimeJob(FacilioUtil.parseLong(jobid), "DefaultMLJob", mlContext.getPredictionTime(), "ml");
				/*
				jobContext = new JobContext();
				jobContext.setJobId(FacilioUtil.parseLong(jobid));
				jobContext.setOrgId(mlContext.getOrgId());
				jobContext.setJobName("DefaultMLJob");
				jobContext.setActive(true);
				jobContext.setExecutionTime(mlContext.getPredictionTime());
				jobContext.setExecutorName("ml");
				JobStore.addJob(jobContext);
				*/
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Error while executing job "+jobid,e);
		}
	}

}
