package com.facilio.bmsconsole.commands;


import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class LoadModuleNameCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioModule moduleObj = modBean.getModule(moduleName);
			
			String moduleDisplayName =  moduleObj.getDisplayName();
			context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, moduleDisplayName);
		}
		
		return false;
	}
}
