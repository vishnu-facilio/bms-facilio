package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetStateTransitionListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long stateFlowId = (long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
		if (stateFlowId > 0) {
			StateFlowRuleContext stateFlowContext = StateFlowRulesAPI.getStateFlowContext(stateFlowId);
			List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(stateFlowContext);
			
			context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, allStateTransitionList);
		}
		return false;
	}

}
