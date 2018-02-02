package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class LoadViewCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(viewName != null && !viewName.isEmpty()) {
			FacilioView view = null;
			boolean isCVEnabled = true;
			if(isCVEnabled) {
				long moduleId = modBean.getModule(moduleName).getModuleId();
				view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId());
			}
			
			if(view == null) {
				view = ViewFactory.getView(moduleName, viewName);
			} else {
				view.setDefaultModuleFields(moduleName);
				if (view.getFields() == null || view.getFields().isEmpty()) {
					view.setFields(ColumnFactory.getColumns(moduleName, viewName));
				}
			}
			
			if(view != null) {
				Map<String, Object> lookupFields = new HashMap<>();
				ArrayList<FacilioField> fieldList = modBean.getAllFields(moduleName);
				for(FacilioField field: fieldList) {
					if(field instanceof LookupField && ((LookupField)field).getLookupModule() != null) {
						String name = ((LookupField)field).getLookupModule().getName();
						if(!lookupFields.containsKey(name)) {
							JSONObject object = new JSONObject();
							object.put("displayName", field.getDisplayName());
							object.put("fields", modBean.getAllFields(name));
							lookupFields.put(field.getName(),  object);							
						}
					}
				}
				view.setLookupFields(lookupFields);
				context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
			}
		}
		
		return false;
	}

}
