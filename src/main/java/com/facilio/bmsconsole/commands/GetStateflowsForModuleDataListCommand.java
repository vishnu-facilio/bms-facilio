package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetStateflowsForModuleDataListCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(CollectionUtils.isNotEmpty(records)) {
			Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(records);
			if (MapUtils.isNotEmpty(stateFlows)) {
				if (moduleName.equals("workpermit")) {
					for(ModuleBaseWithCustomFields record: records) {
						if (record.getModuleState() == null) {
							continue;
						}
						String key = record.getStateFlowId() + "_" + record.getModuleState().getId();
						if(stateFlows.containsKey(key)) {
							List<WorkflowRuleContext> evaluateStateFlowAndExecuteActions = StateFlowRulesAPI.evaluateStateFlowAndExecuteActions(new ArrayList<>(stateFlows.get(key)), moduleName, record, context);
							record.setCanCurrentUserApprove(CollectionUtils.isNotEmpty(evaluateStateFlowAndExecuteActions));
						}
					}
				}
				else {
					for (String key : stateFlows.keySet()) {
						List<WorkflowRuleContext> list = stateFlows.get(key);
						GetAvailableStateCommand.removeUnwantedTranstions(list);
					}
				}
			}
		context.put("stateFlows", stateFlows);
		}
		return false;
	}

}
