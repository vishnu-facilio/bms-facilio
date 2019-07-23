package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.constants.FacilioConstants;

public class GetAllPreferencesCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String modName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		boolean moduleSpecific = (boolean)context.get(FacilioConstants.ContextNames.MODULE_SPECIFIC);
		if(modName == null) {
			throw new IllegalArgumentException("Module name is mandatory");
		}
		List<Preference> prefs = moduleSpecific ? PreferenceFactory.getAllPreferencesForModule(modName) : PreferenceFactory.getAllPreferencesForModuleRecord(modName);
		context.put(FacilioConstants.ContextNames.PREFERENCE_LIST, prefs);
		return false;
	}

}
