package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetStateTransitionListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long stateFlowId = (long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		if (stateFlowId > 0) {
			StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
			List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext);
			
			context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, allStateTransitionList);
		}
		return false;
	}

}
