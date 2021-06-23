package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllEnabledPreferencesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long recordId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		String moduleName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(moduleName == null) {
			throw new IllegalArgumentException("Module name is mandatory");
		}
		
		List<PreferenceMetaContext> prefs = null;
		if(recordId != null && recordId > 0) {
			prefs = PreferenceAPI.getEnabledPreference(recordId, moduleName);
		}
		else {
			prefs = PreferenceAPI.getEnabledPreference(moduleName);
		}
		context.put(FacilioConstants.ContextNames.PREFERENCE_LIST, prefs);
		return false;
	}

}
