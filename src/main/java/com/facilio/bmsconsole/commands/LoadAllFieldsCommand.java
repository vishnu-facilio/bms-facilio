package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class LoadAllFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(moduleName);
			List<FacilioField> restrictedFields = new ArrayList<FacilioField>();
			for(int i=0;i<fields.size();i++) {
				if(fields.get(i).getName().equals("tenant")) {
					if(!AccountUtil.isFeatureEnabled(AccountUtil.FEATURE_TENANTS)) {
					  continue;
					}
						
				}
				restrictedFields.add(fields.get(i));
			}
			
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, restrictedFields);
		}
		return false;
	}
}
