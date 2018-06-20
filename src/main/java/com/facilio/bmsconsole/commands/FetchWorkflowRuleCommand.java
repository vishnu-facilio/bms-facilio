package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class FetchWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (id == null || id == -1) {
			throw new IllegalArgumentException("Invalid ID to fetch workflow");
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, WorkflowRuleAPI.getWorkflowRule(id));
		return false;
	}

}
