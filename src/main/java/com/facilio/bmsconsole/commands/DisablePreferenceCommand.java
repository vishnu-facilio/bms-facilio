package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Preference;
import com.facilio.bmsconsole.context.PreferenceMetaContext;
import com.facilio.bmsconsole.modules.PreferenceFactory;
import com.facilio.bmsconsole.util.PreferenceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DisablePreferenceCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long prefId = (Long)context.get(FacilioConstants.ContextNames.PREFERENCE_ID);
		PreferenceMetaContext pref = PreferenceAPI.getEnabledPreference(prefId);
		if(pref == null) {
			throw new IllegalArgumentException("No valid preference found");
		}
		Preference preference = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(pref.getModuleId());
		if(pref.getRecordId() > 0) {
			preference = PreferenceFactory.getModuleRecordPreference(module.getName(), pref.getPreferenceName());
		}
		else {
			preference = PreferenceFactory.getModulePreference(module.getName(), pref.getPreferenceName());
		}
		preference.disable(pref.getRecordId(), pref.getModuleId());
		int rows_updated = PreferenceAPI.deleteEnabledPreference(pref.getId());
		
		
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rows_updated);
		return false;
	}

}
