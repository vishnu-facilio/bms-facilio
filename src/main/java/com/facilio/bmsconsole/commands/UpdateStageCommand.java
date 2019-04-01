package com.facilio.bmsconsole.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.StateRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class UpdateStageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		long currentTransitionId = (long) context.get("transistion_id");
		
		if (moduleData == null) {
			throw new Exception("Not valid id");
		}
		
		if (currentTransitionId > 0) {
			StateflowRuleContext stateFlowContext = (StateflowRuleContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
			if (stateFlowContext.getFromStateId() != moduleData.getStateFlow().getId()) {
				throw new Exception("State mismatch");
			}
			StateContext newState = StateRulesAPI.getStateContext(stateFlowContext.getToStateId());
			if (newState == null) {
				throw new Exception("Invalid state");
			}
			moduleData.setStateFlow(newState);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));
		}
		return false;
	}

}
