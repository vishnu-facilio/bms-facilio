package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetStateflowsForModuleDataListCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ModuleBaseWithCustomFields> records = (List<ModuleBaseWithCustomFields>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records)) {
			Map<String, List<WorkflowRuleContext>> stateFlows = StateFlowRulesAPI.getAvailableStates(records);
			for(ModuleBaseWithCustomFields record: records) {
				if (record.getModuleState() == null) {
					continue;
				}
				String key = record.getStateFlowId() + "_" + record.getModuleState().getId();
			}
			context.put("stateFlows", stateFlows);
		}
		return false;
	}

}
