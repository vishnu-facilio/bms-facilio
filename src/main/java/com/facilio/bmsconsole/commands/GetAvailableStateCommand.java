package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class GetAvailableStateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleData != null) {
			StateContext currentState = moduleData.getModuleState();
			if (currentState != null) {
				List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(moduleData.getStateFlowId(), currentState.getId(), moduleName, moduleData, (FacilioContext) context);
				
				context.put("availableStates", availableState);
				System.out.println(availableState);
			}
		}
		return false;
	}

}
