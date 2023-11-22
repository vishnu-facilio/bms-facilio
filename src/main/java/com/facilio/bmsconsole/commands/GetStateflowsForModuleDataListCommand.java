package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioStatus;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetStateflowsForModuleDataListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(CollectionUtils.isNotEmpty(records)) {
			Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(records);
			if (MapUtils.isNotEmpty(stateFlows)) {
				for(ModuleBaseWithCustomFields record: records) {
					FacilioStatus currentState = record.getApprovalStatus();
					if (currentState == null) {
						currentState = record.getModuleState();
					}
					if (currentState == null) {
						continue;
					}

					long stateFlowId = record.getApprovalFlowId();
					if (stateFlowId < 0) {
						stateFlowId = record.getStateFlowId();
					}

					String key = stateFlowId + "_" + currentState.getId();
					if(stateFlows.containsKey(key)) {
						ArrayList<WorkflowRuleContext> list = new ArrayList<>(stateFlows.get(key));
						StateFlowRulesAPI.removeUnwantedTranstions(list);
						List<WorkflowRuleContext> evaluateStateFlowAndExecuteActions = StateFlowRulesAPI.getExecutableStateTransitions(list, moduleName, record, context);
						if (CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions)) {
							record.setEvaluatedTransitionIds(evaluateStateFlowAndExecuteActions.stream().map(WorkflowRuleContext::getId).collect(Collectors.toList()));
						}
						record.setCanCurrentUserApprove(CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions));
					}
				}
			}
			context.put("stateFlows", stateFlows);
		}
		return false;
	}

}
