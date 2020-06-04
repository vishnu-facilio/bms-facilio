package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

public class GetPageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		boolean isApproval = (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_APPROVAL, false);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		Page page = PageFactory.getPage(module, record, isApproval);
		if (page == null) {
			FacilioModule extendedModule = module.getExtendModule();
			while(extendedModule != null && page == null) {
				page = PageFactory.getPage(extendedModule, record, isApproval);
				extendedModule = extendedModule.getExtendModule();
			}
		}
		context.put(FacilioConstants.ContextNames.PAGE, page);
		return false;
	}
	
	

}
