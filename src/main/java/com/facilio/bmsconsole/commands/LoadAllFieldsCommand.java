package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class LoadAllFieldsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		context.put(FacilioConstants.ContextNames.WORKORDER_ACTIVITY, moduleName);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			List<FacilioField> restrictedFields = new ArrayList<FacilioField>();
			boolean handleStateField = (boolean) context.getOrDefault("handleStateField", false);
			FacilioModule mod = modBean.getModule(moduleName);
			for(int i=0;i<fields.size();i++) {
				if(fields.get(i).getName().equals("tenant")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
					  continue;
					}
						
				} 
				if(moduleName.equals("workorder") && fields.get(i).getName().equals("safetyPlan")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
					  continue;
					}
						
				}
				else if (handleStateField) {
					fields.removeIf(field -> field.getName().equals("stateFlowId") || (field.getName().equals("moduleState") && !mod.isStateFlowEnabled()));
				}
				restrictedFields.add(fields.get(i));
			}
			
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, restrictedFields);
		}
		return false;
	}
}
