package com.facilio.bmsconsole.instant.jobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.forms.FormRuleContext.RuleType;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.InstantJob;

public class ParallelWorkflowRuleExecutionJob extends InstantJob{
	
	private static final Logger LOGGER = Logger.getLogger(ParallelWorkflowRuleExecutionJob.class.getName());
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		HashMap<String, Object> workflowRuleExecutionMap = new HashMap<String, Object>();
		try {
			workflowRuleExecutionMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.WORKFLOW_PARALLEL_RULE_EXECUTION_MAP);
			FacilioContext recordContextForRuleExecution = (FacilioContext) context.get(FacilioConstants.ContextNames.RECORD_CONTEXT_FOR_RULE_EXECUTION);
			
			WorkflowRuleContext workflowRule = (WorkflowRuleContext) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
			FacilioModule module = (FacilioModule) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE);
			Object record = workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RECORD);
			List<UpdateChangeSet> changeSet = (List<UpdateChangeSet>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.CHANGE_SET);
			Map<String, Object> recordPlaceHolders = (Map<String, Object>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.PLACE_HOLDER);
			boolean propagateError = (boolean) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.PROPAGATE_ERROR);
			FacilioField parentRuleField = (FacilioField) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.PARENT_RULE_FIELD);
			FacilioField onSuccessField = (FacilioField) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.ON_SUCCESS_FIELD);
			Map<String, List<WorkflowRuleContext>> workflowRuleCacheMap = (Map<String, List<WorkflowRuleContext>>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.WORKFLOW_RULE_CACHE_MAP);
			List<EventType> eventTypes = (List<EventType>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.EVENT_TYPES);
			WorkflowRuleContext.RuleType[] ruleTypes = (WorkflowRuleContext.RuleType[]) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RULE_TYPES);		

			workflowRule.executeRuleAndChildren(workflowRule, module, record, changeSet, recordPlaceHolders, recordContextForRuleExecution, propagateError, parentRuleField, onSuccessField, workflowRuleCacheMap, false, eventTypes, ruleTypes);		
			LOGGER.info("ParallelWorkflowRuleExecutionJob executed for "+workflowRule.getId());
		}
		catch (Exception e) {
			LOGGER.severe("Error occurred while parallel execution of rule -- "+ ((WorkflowRuleContext) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.WORKFLOW_RULE)).getId()+ " Exception -- " + e);
		}
	}
}
