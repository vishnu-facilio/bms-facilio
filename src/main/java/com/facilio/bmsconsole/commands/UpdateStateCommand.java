package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class UpdateStateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		ModuleBaseWithCustomFields moduleData = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		Boolean defaultState = (Boolean) context.get(FacilioConstants.ContextNames.DEFAULT_STATE);
		
		if (defaultState == null) {
			defaultState = false;
		}
		long defaultStateId = -1;
		long defaultStateFlowId = -1;
		if (defaultState) {
			defaultStateId = (long) context.get(FacilioConstants.ContextNames.DEFAULT_STATE_ID);
			defaultStateFlowId = (long) context.get(FacilioConstants.ContextNames.DEFAULT_STATE_FLOW_ID);
		}
		
		if (moduleData == null) {
			throw new Exception("Not valid id");
		}
		
		Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, moduleData, WorkflowRuleAPI.getOrgPlaceHolders());
		
		if (defaultState) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);

			TicketStatusContext state = StateFlowRulesAPI.getStateContext(defaultStateId);
			moduleData.setStateFlowId(defaultStateFlowId);
			StateFlowRulesAPI.updateState(moduleData, module, state, true, context);
		} 
		else {
			if (currentTransitionId != null && currentTransitionId > 0) {
				StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId, true);
				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransition, moduleName, moduleData, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, moduleData.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					TicketStatusContext newState = StateFlowRulesAPI.getStateContext(stateflowTransition.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					stateflowTransition.executeTrueActions(moduleData, context, recordPlaceHolders);
				} 
				else {
					throw new Exception("Cannot update the state: " + stateflowTransition.getId());
				}
			}
		}
		return false;
	}

}
