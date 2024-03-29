package com.facilio.bmsconsole.instant.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

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
import com.facilio.taskengine.job.InstantJob;

public class ParallelRecordBasedWorkflowRuleExecutionJob extends InstantJob{
	
	private static final Logger LOGGER = Logger.getLogger(ParallelRecordBasedWorkflowRuleExecutionJob.class.getName());
	@Override
	public void execute(FacilioContext context) throws Exception {
		
		HashMap<String, Object> workflowRuleExecutionMap = null;
		try {
			workflowRuleExecutionMap = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.WORKFLOW_PARALLEL_RULE_EXECUTION_MAP);	
			if(workflowRuleExecutionMap != null && !workflowRuleExecutionMap.isEmpty()) {
				
				boolean executeReadingRuleCommand = true;
	        	Map<String, String> orgInfoMap = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_COMMAND);
				if(orgInfoMap != null && MapUtils.isNotEmpty(orgInfoMap)) {
					String executeReadingRuleCommandProp = orgInfoMap.get(FacilioConstants.OrgInfoKeys.EXECUTE_READING_RULE_COMMAND);
					if (executeReadingRuleCommandProp != null && !executeReadingRuleCommandProp.isEmpty() && StringUtils.isNotEmpty(executeReadingRuleCommandProp) && Boolean.valueOf(executeReadingRuleCommandProp) != null && !Boolean.parseBoolean(executeReadingRuleCommandProp)){
						executeReadingRuleCommand = false;
					}	
	        	}
				
				WorkflowRuleContext.RuleType[] ruleTypes = (WorkflowRuleContext.RuleType[]) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RULE_TYPES);
				FacilioChain executeWorkflowChain = ReadOnlyChainFactory.executeSpecifcRuleTypeWorkflowsForReadingChain(ruleTypes, executeReadingRuleCommand);

				FacilioContext filteredContextForRecordSpecificRulesExecution = executeWorkflowChain.getContext();

				FacilioContext recordContextForRuleExecution = (FacilioContext) context.get(FacilioConstants.ContextNames.RECORD_CONTEXT_FOR_RULE_EXECUTION);
				if(recordContextForRuleExecution != null && !recordContextForRuleExecution.isEmpty()) {
					filteredContextForRecordSpecificRulesExecution.putAll(recordContextForRuleExecution);
				}
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.MODULE_NAME, (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.RECORD, (Object) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RECORD));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.CHANGE_SET, (Map<Long, List<UpdateChangeSet>>)  workflowRuleExecutionMap.get(FacilioConstants.ContextNames.CHANGE_SET));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, (List<EventType>) workflowRuleExecutionMap.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST));
				filteredContextForRecordSpecificRulesExecution.put(FacilioConstants.ContextNames.IS_PARALLEL_RULE_EXECUTION, Boolean.FALSE);
				executeWorkflowChain.execute();
				
				LOGGER.info("ParallelRecordBasedWorkflowRuleExecutionJob executed for record: "+(Object)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.RECORD) +" ModuleName -- " + (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME));		
			}
		}
		catch (Exception e) {
			LOGGER.severe("Error occurred while ParallelRecordBasedWorkflowRuleExecutionJob of context -- "+ context + " ModuleName -- " + workflowRuleExecutionMap == null ? "null" : (String)workflowRuleExecutionMap.get(FacilioConstants.ContextNames.MODULE_NAME) +" Exception -- " + e);
		}
	}
}
