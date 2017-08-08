package com.facilio.bmsconsole.commands;

import java.sql.Connection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class SetModuleIdCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty or null");
		}
		
		Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", conn);
		
		FacilioModule moduleObj = modBean.getModule(moduleName);
		
		context.put(FacilioConstants.ContextNames.MODULE_ID, moduleObj.getModuleId());
		
		return false;
	}

}
