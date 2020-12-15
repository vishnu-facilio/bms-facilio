package com.facilio.bmsconsole.instant.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.tasker.job.InstantJob;

public class ParallelModuleBasedWorkflowRuleExecutionJob extends InstantJob{

	private static final Logger LOGGER = Logger.getLogger(ParallelModuleBasedWorkflowRuleExecutionJob.class.getName());
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		try {
			if(context != null && !context.isEmpty()) {
				WorkflowRuleContext.RuleType[] ruleTypes = (WorkflowRuleContext.RuleType[]) context.get(FacilioConstants.ContextNames.RULE_TYPES);
				FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeSpecifcRuleTypeWorkflowsForReadingChain(ruleTypes);
				executeWorkflowChain.execute(context);
						
				LOGGER.info("ParallelModuleBasedWorkflowRuleExecutionJob executed for ModuleName -- " + (String)context.get(FacilioConstants.ContextNames.MODULE_NAME));		
			}
		}
		catch (Exception e) {
			LOGGER.severe("Error occurred while ParallelModuleBasedWorkflowRuleExecutionJob of context -- "+ context + " ModuleName -- " + context == null ? "null" : (String)context.get(FacilioConstants.ContextNames.MODULE_NAME) +" Exception -- " + e);
		}
	}


}
