package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.lang3.StringUtils;

public class EnablePreferenceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, Object> map = (Map<String, Object>) context.get(FacilioConstants.ContextNames.PREFERENCE_VALUE_LIST);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		String name = (String) context.get(FacilioConstants.ContextNames.PREFERENCE_NAME);
		Long enabledPrefId = (Long)context.get(FacilioConstants.ContextNames.PREFERENCE_ID);

		if(name == null) {
			throw new IllegalArgumentException("Preference name is mandatory");
		}
		Preference preference = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = StringUtils.isNotEmpty(moduleName) ? modBean.getModule(moduleName) : null;

		if(recordId > 0) {
			if(module != null) {
				preference = PreferenceFactory.getModuleRecordPreference(module.getName(), name);
			}
			else {
				throw new IllegalArgumentException("Module name is mandatory for record level preferences");
			}
		}
		else if(recordId <= 0 && module != null){
			preference = PreferenceFactory.getModulePreference(module.getName(), name);
		}
		else {
			preference = PreferenceFactory.getOrgPreference(name);
		}

		if(enabledPrefId != null && enabledPrefId > 0) {
			PreferenceMetaContext pref = PreferenceAPI.checkForEnabledPreference(module != null ? module.getModuleId() : -1, name, recordId);
			if (pref != null) {
				preference.disable(pref.getRecordId(), pref.getModuleId());
			}
			else{
				throw new IllegalArgumentException("Invalid pref id");
			}
		}

		if (preference != null) {
			preference.subsituteAndEnable(map, recordId > 0 ? recordId : -1, module != null ? module.getModuleId() : -1);
		}
		else {
			throw new IllegalArgumentException("Invalid preference configuration");
		}
		return false;
	}

}
