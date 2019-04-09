package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransistionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class UpdateStateCommand implements Command {

	boolean stateChanged = false;
	
	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long currentTransitionId = (Long) context.get("transistion_id");
		Boolean defaultState = (Boolean) context.get("default_state");
		
		if (defaultState == null) {
			defaultState = false;
		}
		long defaultStateId = -1;
		long defaultStateFlowId = -1;
		if (defaultState) {
			defaultStateId = (long) context.get("default_state_id");
			defaultStateFlowId = (long) context.get("default_state_flow_id");
		}
		
		if (moduleData == null) {
			throw new Exception("Not valid id");
		}
		
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
		
		if (defaultState) {
			StateContext state = StateFlowRulesAPI.getStateContext(defaultStateId);
			changeState(moduleData, state);
			moduleData.setStateFlowId(defaultStateFlowId);
		} 
		else {
			if (currentTransitionId != null && currentTransitionId > 0) {
				StateflowTransistionContext stateflowTransistion = (StateflowTransistionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId, true);
				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransistion, moduleName, moduleData, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, moduleData.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					if (stateflowTransistion.getFromStateId() != moduleData.getModuleState().getId()) {
						throw new Exception("State mismatch");
					}
					StateContext newState = StateFlowRulesAPI.getStateContext(stateflowTransistion.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					changeState(moduleData, newState);
					stateflowTransistion.executeTrueActions(moduleData, context, recordPlaceHolders);
				}
			}
		}
		
		if (stateChanged) {
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(moduleData.getId()));
		}
		return false;
	}
	
	private void changeState(ModuleBaseWithCustomFields moduleData, StateContext newState) {
		moduleData.setModuleState(newState);
		stateChanged = true;
	}

}
