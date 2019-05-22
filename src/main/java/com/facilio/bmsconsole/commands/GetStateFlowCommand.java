package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetStateFlowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (stateFlowId != null && stateFlowId > 0) {
			StateFlowRuleContext stateFlowRuleContext = (StateFlowRuleContext) WorkflowRuleAPI.getWorkflowRule(stateFlowId);
			if (stateFlowRuleContext == null) {
				throw new IllegalArgumentException("Invalid id");
			}
			context.put(FacilioConstants.ContextNames.RECORD, stateFlowRuleContext);
		}
		else {
			throw new IllegalArgumentException("Invalid id");
		}
		return false;
	}

}
