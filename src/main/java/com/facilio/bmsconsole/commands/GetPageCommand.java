package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.FacilioModule;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetPageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Page page = PageFactory.getPage(moduleName);
		if (page == null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			if (module.getExtendModule() != null) {
				page = PageFactory.getPage(module.getExtendModule().getName());
			}
		}
		context.put(FacilioConstants.ContextNames.PAGE, page);
		
		return false;
	}
	
	

}
