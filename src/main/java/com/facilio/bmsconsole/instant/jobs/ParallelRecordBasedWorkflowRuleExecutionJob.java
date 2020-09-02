package com.facilio.bmsconsole.instant.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.job.InstantJob;

public class ParallelRecordBasedWorkflowRuleExecutionJob extends InstantJob{
	
	private static final Logger LOGGER = Logger.getLogger(ParallelRecordBasedWorkflowRuleExecutionJob.class.getName());
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		HashMap<String, Object> workflowRuleExecutionMap = new HashMap<String, Object>();
		try {
			workflowRuleExecutionMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.WORKFLOW_PARALLEL_RULE_EXECUTION_MAP);	
			if(workflowRuleExecutionMap != null && !workflowRuleExecutionMap.isEmpty()) {
				
				FacilioContext filteredContextForRecordSpecificRulesExecution = new FacilioContext();

				FacilioContext recordContextForRuleExecution = (FacilioContext) context.get(FacilioConstants.ContextNames.RECORD_CONTEXT_FOR_RULE_EXECUTION);
				if(recordContextForRuleExecution != null && !recordContextForRuleExecution.isEmpty()) {
					filteredContextForRecordSpecificRulesExecution.putAll(recordContextForRuleExecution);
				}
				
				WorkflowRuleContext.RuleType[] ruleTypes = (WorkflowRuleContext.RuleType[]) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RULE_TYPES);		
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.MODULE_NAME, (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.RECORD, (Object) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RECORD));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.CHANGE_SET, (Map<Long, List<UpdateChangeSet>>)  workflowRuleExecutionMap.get(FacilioConstants.ContextNames.CHANGE_SET));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, (List<EventType>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.STOP_PARALLEL_RULE_EXECUTION, true);
						
				FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeSpecifcRuleTypeWorkflowsForReadingChain(ruleTypes);
				executeWorkflowChain.execute(filteredContextForRecordSpecificRulesExecution);
				
				LOGGER.info("ParallelRecordBasedWorkflowRuleExecutionJob executed for record: "+(Object)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RECORD) +" ModuleName -- " + (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME));		
			}
		}
		catch (Exception e) {
			LOGGER.severe("Error occurred while ParallelRecordBasedWorkflowRuleExecutionJob of context -- "+ context + " ModuleName -- " + (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME) +" Exception -- " + e);
		}
	}
}
