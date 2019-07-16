package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetAvailableStateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleData != null) {
			FacilioStatus currentState = moduleData.getModuleState();
			context.put("currentState", currentState);
			if (currentState != null) {
				List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(moduleData.getStateFlowId(), currentState.getId(), moduleName, moduleData, (FacilioContext) context);
				
				context.put("availableStates", availableState);
			}
		} else {
			throw new IllegalArgumentException("Record not found");
		}
		return false;
	}

}
