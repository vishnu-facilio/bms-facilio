package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class EnablePreferenceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
	
		if(recordId > 0) {
			preference = PreferenceFactory.getModuleRecordPreference(moduleName, name);
		}
		else {
			preference = PreferenceFactory.getModulePreference(moduleName, name);
		}
		if (preference != null) {
			preference.subsituteAndEnable(map, recordId, module.getModuleId());
		}
		else {
			throw new IllegalArgumentException("Invalid preference configuration");
		}
		return false;
	}

}
