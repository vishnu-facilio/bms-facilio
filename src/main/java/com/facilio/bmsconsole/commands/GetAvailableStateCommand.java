package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetAvailableStateCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(GetAvailableStateCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if (moduleData != null) {
			FacilioStatus currentState = moduleData.getModuleState();
			context.put("currentState", currentState);
			if (currentState != null) {
				long currentTime = System.currentTimeMillis();
				List<WorkflowRuleContext> availableState = StateFlowRulesAPI.getAvailableState(moduleData.getStateFlowId(), currentState.getId(), moduleName, moduleData, (FacilioContext) context);
//				System.out.println("################### time taken: " + (System.currentTimeMillis() - currentTime));
				LOGGER.debug("### time taken: " + this.getClass().getSimpleName() + ": " + (System.currentTimeMillis() - currentTime));
				context.put("availableStates", availableState);
			}
		} else {
			throw new IllegalArgumentException("Record not found");
		}
		return false;
	}

}
