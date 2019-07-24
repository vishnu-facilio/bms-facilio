package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleteWorkflowRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ruleIds = (List<Long>) context.get(FacilioConstants.ContextNames.ID);
		
		List<Long> readingAlarmRuleIds = new ArrayList<>(); 
		for(Long ruleId :ruleIds) {
			List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(ruleId);
			if(readingAlarmRules != null && !readingAlarmRules.isEmpty()) {
				readingAlarmRuleIds.addAll(readingAlarmRules.stream().map(ReadingAlarmRuleContext::getId).collect(Collectors.toList()));
			}
		}
		WorkflowRuleAPI.deleteWorkFlowRules(readingAlarmRuleIds);
		
		WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		context.put(FacilioConstants.ContextNames.RESULT, true);
		return false;
	}

}
