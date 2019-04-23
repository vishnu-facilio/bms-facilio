package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.constants.FacilioConstants;

public class DeleteStateTransitionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		StateflowTransistionContext stateFlowRuleContext = (StateflowTransistionContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		return false;
	}

}
