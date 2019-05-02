package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.StateFlowContext;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetStateTransitionListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long stateFlowId = (long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		if (stateFlowId > 0) {
			StateFlowContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
			List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext);
			
			context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, allStateTransitionList);
		}
		return false;
	}

}
