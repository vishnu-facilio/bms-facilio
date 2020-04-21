package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class GetPageConnectedAppWidgetsCommand extends FacilioCommand {

	private static Logger LOGGER = LogManager.getLogger(GetPageConnectedAppWidgetsCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		try {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			if (module != null) {
				List<Long> moduleIds = module.getExtendedModuleIds();
				
				Page page = (Page) context.get(FacilioConstants.ContextNames.PAGE);
				Object record = context.get(FacilioConstants.ContextNames.RECORD);
				
				if (page != null && record != null && record instanceof ModuleBaseWithCustomFields) {
				
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
			}
		}
		catch (Exception e) {
			LOGGER.error("Exception while adding connected app widgets to page factory", e);
		}
		
		return false;
	}
	
	

}
