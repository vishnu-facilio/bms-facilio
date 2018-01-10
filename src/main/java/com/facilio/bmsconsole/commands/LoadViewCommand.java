package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
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
		
		if(viewName != null && !viewName.isEmpty()) {
			FacilioView view = null;
			boolean isCVEnabled = true;
			if(isCVEnabled) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				long moduleId = modBean.getModule(moduleName).getModuleId();
				view = ViewAPI.getView(viewName, moduleId, AccountUtil.getCurrentOrg().getOrgId());
			}
			
			if(view == null) {
				view = ViewFactory.getView(moduleName + "-" +viewName);
			} else if(view.getFields() == null || view.getFields().isEmpty()) {
				view.setFields(ColumnFactory.getColumns(moduleName + "-" +viewName));
			}
			
			if(view != null) {
				context.put(FacilioConstants.ContextNames.CUSTOM_VIEW, view);
			}
		}
		
		return false;
	}

}
