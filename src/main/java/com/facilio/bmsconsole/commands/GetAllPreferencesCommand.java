package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.constants.FacilioConstants;

public class GetAllPreferencesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String modName = (String)context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		boolean moduleSpecific = (boolean)context.get(FacilioConstants.ContextNames.MODULE_SPECIFIC);
		if(modName == null) {
			throw new IllegalArgumentException("Module name is mandatory");
		}
		List<Preference> prefs = moduleSpecific ? PreferenceFactory.getAllPreferencesForModule(modName) : PreferenceFactory.getAllPreferencesForModuleRecord(modName);
//		List<PreferenceMetaContext> enabledPreferences = new ArrayList<PreferenceMetaContext>();
//		if(moduleSpecific) {
//			enabledPreferences = PreferenceAPI.getEnabledPreference(modName);
//		}
//		else {
//			enabledPreferences = PreferenceAPI.getEnabledPreference(recordId, modName);
//		}
//		if(CollectionUtils.isNotEmpty(enabledPreferences) && CollectionUtils.isNotEmpty(prefs)) {
//			for(PreferenceMetaContext enabledPref : enabledPreferences) {
//				List<Preference> pref = prefs.stream().filter(a -> a.getName().equals(enabledPref.getPreferenceName())).collect(Collectors.toList());
//				if(CollectionUtils.isNotEmpty(pref)) {
//					pref.get(0).setEnabled(true);
//					pref.get(0).setPrefMeta(enabledPref);
//				}
//			}
//		}
		context.put(FacilioConstants.ContextNames.PREFERENCE_LIST, prefs);
		return false;
	}

}
