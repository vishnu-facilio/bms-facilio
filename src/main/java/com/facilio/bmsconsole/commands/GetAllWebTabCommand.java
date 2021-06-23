package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAllWebTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long tabGroupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
		if (tabGroupId != null && tabGroupId > 0) {

			List<WebTabContext> tabs = ApplicationApi.getWebTabsForWebGroup(tabGroupId);
			if (tabs != null && !tabs.isEmpty()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				for (WebTabContext tab : tabs) {
					List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(tab.getId());
					List<Long> moduleIds = new ArrayList<>();
					List<String> specialTypes = new ArrayList<>();
					if(tabIdAppIdMappingContextList!=null && !tabIdAppIdMappingContextList.isEmpty()) {
						for(TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
							if(tabIdAppIdMappingContext.getModuleId()>0) {
								moduleIds.add(tabIdAppIdMappingContext.getTabId());
							}
							if(tabIdAppIdMappingContext.getSpecialType()!=null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")){
								specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
							}
						}
					}
					tab.setModuleIds(moduleIds);
					tab.setSpecialTypeModules(specialTypes);
					List<FacilioModule> modules = new ArrayList<>();
					if (CollectionUtils.isNotEmpty(tab.getModuleIds())) {
						for (Long moduleId : tab.getModuleIds()) {
							modules.add(modBean.getModule(moduleId));
						}
					}
					if (CollectionUtils.isNotEmpty(tab.getSpecialTypeModules())) {
						for (String specialType : tab.getSpecialTypeModules()) {
							modules.add(modBean.getModule(specialType));
						}
					}
					if (CollectionUtils.isNotEmpty(modules)) {
						tab.setModules(modules);
					}
				}
			}
			context.put(FacilioConstants.ContextNames.WEB_TABS, tabs);
		}
		return false;
	}
}
