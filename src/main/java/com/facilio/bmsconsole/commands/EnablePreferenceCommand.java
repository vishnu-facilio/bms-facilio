package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class EnablePreferenceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		Map<String, Object> map = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PREFERENCE_VALUE_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		String name = (String) context.get(FacilioConstants.ContextNames.PREFERENCE_NAME);
		if(name == null) {
			throw new IllegalArgumentException("Preference name is mandatory");
		}
		if(moduleName == null) {
			throw new IllegalArgumentException("Module name is mandatory");
		}
		Preference preference = null;
		if(recordId > 0) {
			PreferenceFactory.getModuleRecordPreference(moduleName, name);
		}
		else {
			PreferenceFactory.getModulePreference(moduleName, name);
		}
		if (preference != null) {
			WorkflowRuleContext rule = preference.subsituteAndEnable(map, recordId);
			if(rule != null) {
				context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_ID, rule.getId());
			}
		}
		
		return false;
	}

}
