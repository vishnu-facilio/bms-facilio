package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PreferenceRuleUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class DeletePreferenceRulesOnRecordDeletionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		String preferenceName = (String) context.get(FacilioConstants.ContextNames.PREFERENCE_NAMES);
		long moduleId;
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		moduleId = module.getModuleId();
		for (long recordId: recordIds) {
			
			PreferenceRuleUtil.disablePreferenceRule(moduleId, recordId, preferenceName);
		}
		
		return false;
	}

}
