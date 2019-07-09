package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class DisablePreferenceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long prefId = (Long)context.get(FacilioConstants.ContextNames.PREFERENCE_ID);
		PreferenceMetaContext pref = PreferenceAPI.getEnabledPreference(prefId);
		if(pref == null) {
			throw new IllegalArgumentException("No valid preference found");
		}
		int rows_updated = PreferenceAPI.deleteEnabledPreference(pref.getId());
		
		if(pref.getRuleId() > 0) {
			WorkflowRuleAPI.deleteWorkflowRule(pref.getRuleId());
		}
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rows_updated);
		return false;
	}

}
