package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteReadingAlarmRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long ruleId = (long) context.get(FacilioConstants.ContextNames.RULE_ID);
		
		ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(ruleId));
		WorkflowRuleAPI.deleteWorkflowRule(ruleId);
		return false;
	}

}