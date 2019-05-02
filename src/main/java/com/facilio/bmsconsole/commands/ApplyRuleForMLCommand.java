package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ApplyRuleForMLCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception 
	{
		
		MLContext mlContext = (MLContext) context.get(FacilioConstants.ContextNames.ML);
		WorkflowRuleContext rule = WorkflowRuleAPI.getWorkflowRule(mlContext.getRuleID(),true,true,true);
		if (rule != null && rule.isActive()) 
		{
			
			FacilioContext ruleContext = new FacilioContext();
			ruleContext.put("jobid", "100");
			WorkflowRuleAPI.executeScheduledRule(rule, mlContext.getPredictionTime() * 1000, ruleContext);
		}
		return false;
		
	}

}
