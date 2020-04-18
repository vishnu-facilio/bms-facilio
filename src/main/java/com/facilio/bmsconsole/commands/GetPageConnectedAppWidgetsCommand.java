package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetPageConnectedAppWidgetsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		if (module != null) {
			List<Long> moduleIds = module.getExtendedModuleIds();
			
			Page page = (Page) context.get(FacilioConstants.ContextNames.PAGE);
			Object record = context.get(FacilioConstants.ContextNames.RECORD);
			
			long recordId = ((ModuleBaseWithCustomFields) record).getId();
			
			List<ConnectedAppWidgetContext> widgets = ConnectedAppAPI.getSummaryPageWidgets(moduleIds, recordId);
			if (widgets != null && !widgets.isEmpty()) {
				
				for (ConnectedAppWidgetContext widget : widgets) {
					Page.Tab tab = page.new Tab(widget.getId()+"", "connectedapp_widget");
					tab.setDisplayName(widget.getWidgetName());
					page.addTab(tab);
				}
			}
		}
		
		return false;
	}
	
	

}
