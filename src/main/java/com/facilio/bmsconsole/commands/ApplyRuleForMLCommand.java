package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ApplyRuleForMLCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(ApplyRuleForMLCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception 
	{
		
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(mlContext.getRuleID(),true,true,true);
		LOGGER.info("MLContext for Rule id is "+mlContext.getRuleID()+":::"+mlContext.getId()+":::"+rule);
		if (rule != null && rule.isActive()) 
		{
			LOGGER.info("Rule is active ");
			String jobid = mlContext.getMLModelVariable("jobid");
			FacilioContext ruleContext = new FacilioContext();
			ruleContext.put("jobid", jobid);
			LOGGER.info("Calling jobid "+jobid);
			WorkflowRuleAPI.executeScheduledRule(rule, System.currentTimeMillis()+1000, ruleContext);
			LOGGER.info("After scheduling rule "+jobid);
		}
		return false;
		
	}

}
