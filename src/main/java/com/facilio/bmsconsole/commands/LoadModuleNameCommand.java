package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class LoadModuleNameCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", conn);
			
			FacilioModule moduleObj = modBean.getModule(moduleName);
			
			String moduleDisplayName =  moduleObj.getDisplayName();
			context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, moduleDisplayName);
		}
		
		return false;
	}
}
