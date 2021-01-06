package com.facilio.bmsconsole.instant.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
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
				
				boolean executeReadingRuleCommand = true;
	        	Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_COMMAND);
				if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
					String executeReadingRuleCommandProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_COMMAND);
					if (executeReadingRuleCommandProp != null && !executeReadingRuleCommandProp.isEmpty() && StringUtils.isNotEmpty(executeReadingRuleCommandProp) && Boolean.valueOf(executeReadingRuleCommandProp) != null && !Boolean.parseBoolean(executeReadingRuleCommandProp)){
						executeReadingRuleCommand = false;
					}	
	        	}
				
				WorkflowRuleContext.RuleType[] ruleTypes = (WorkflowRuleContext.RuleType[]) context.get(FacilioConstants.ContextNames.RULE_TYPES);
				context.put(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION, Boolean.FALSE);
				FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeSpecifcRuleTypeWorkflowsForReadingChain(ruleTypes, executeReadingRuleCommand);
				executeWorkflowChain.execute(context);
						
				LOGGER.info("ParallelModuleBasedWorkflowRuleExecutionJob executed for ModuleName -- " + (String)context.get(FacilioConstants.ContextNames.MODULE_NAME));		
			}
		}
		catch (Exception e) {
			LOGGER.severe("Error occurred while ParallelModuleBasedWorkflowRuleExecutionJob of context -- "+ context + " ModuleName -- " + context == null ? "null" : (String)context.get(FacilioConstants.ContextNames.MODULE_NAME) +" Exception -- " + e);
		}
	}


}
