package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class GetApplicationDetails extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		ApplicationContext application = null;
		if (appId == -1) {
			application = ApplicationApi.getDefaultApplication();
		} else if (appId > 0) {
			application = ApplicationApi.getApplicationForId(appId);
		} else {
			throw new IllegalArgumentException("Invalid Application Id");
		}
		if (application != null) {
			List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabGroupsForAppId(application.getId());
			if (webTabGroups != null && !webTabGroups.isEmpty()) {
				for (WebTabGroupContext webTabGroup : webTabGroups) {
					List<WebTabContext> webTabs = ApplicationApi.getWebTabsForWebGroup(webTabGroup.getId());
					if (webTabs != null && !webTabs.isEmpty()) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						for (WebTabContext webtab : webTabs) {
							webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
							List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(webtab.getId());
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
							webtab.setModuleIds(moduleIds);
							webtab.setSpecialTypeModules(specialTypes);
							String moduleName = "*";
							if(webtab.getTypeEnum() == Type.MODULE) {
								if (webtab.getModuleIds() != null && !webtab.getModuleIds().isEmpty()) {
									moduleName = modBean.getModule(webtab.getModuleIds().get(0)).getName();
								} else if(webtab.getConfigJSON()!=null){
									moduleName = (String) webtab.getConfigJSON().get("type");
								}
							}
							webtab.setPermission(NewPermissionUtil.getPermissions(webtab.getType(), moduleName));
							if(webtab.getTypeEnum() == Type.SETTINGS) {
								webtab.setPermission(NewPermissionUtil.getPermissionFromConfig(webtab.getType(), webtab.getConfigJSON()));
							}
							if (AccountUtil.getCurrentUser() != null) {
								webtab.setPermissionVal(ApplicationApi.getRolesPermissionValForTab(webtab.getId(),
										AccountUtil.getCurrentUser().getRoleId()));
							}
							if (CollectionUtils.isNotEmpty(webtab.getModuleIds())) {
								List<FacilioModule> modules = new ArrayList<>();
								for (Long moduleId : webtab.getModuleIds()) {
									modules.add(modBean.getModule(moduleId));
								}
								webtab.setModules(modules);
							}
						}
					}
					webTabGroup.setWebTabs(webTabs);
				}
			}
			application.setWebTabGroups(webTabGroups);
			context.put(FacilioConstants.ContextNames.APPLICATION, application);
		} else {
			throw new IllegalArgumentException("Application Not Found");
		}
		return false;
	}
}
