package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> ruleIds = (List<Long>) context.get(FacilioConstants.ContextNames.ID);
		WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		context.put(FacilioConstants.ContextNames.RESULT, true);
		return false;
	}

}
