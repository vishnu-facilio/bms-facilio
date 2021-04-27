package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;

public class GetApplicationDetails extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		String  appName  = (String)context.get(FacilioConstants.ContextNames.LAYOUT_APP_TYPE);
		Boolean fetchAllLayouts  = (Boolean)context.get(FacilioConstants.ContextNames.FETCH_ALL_LAYOUTS);

		ApplicationContext application = null;
		if (appId <= 0) {
			appId = AccountUtil.getCurrentUser().getApplicationId();
		}
		application = ApplicationApi.getApplicationForId(appId);

		if (application != null) {
			List<AppDomain> appDomainList = IAMAppUtil.getAppDomainForType(application.getDomainType(), AccountUtil.getCurrentOrg().getOrgId());
			if(CollectionUtils.isNotEmpty(appDomainList)) {
				application.setAppDomain(appDomainList.get(0));
				for (AppDomain domain : appDomainList) {
					//giving priority for custom domain
					if (domain.getDomainTypeEnum() == AppDomain.DomainType.CUSTOM) {
						application.setAppDomain(domain);
						break;
					}
				}
			}
		    List<ApplicationLayoutContext> appLayouts = null;
			if (fetchAllLayouts != null && fetchAllLayouts) {
				appLayouts = ApplicationApi.getLayoutsForAppId(application.getId());
			} else {
				appLayouts = ApplicationApi.getLayoutsForAppLayoutType(application.getId(), null, -1);
			}
			if (CollectionUtils.isNotEmpty(appLayouts)) {
				for (ApplicationLayoutContext layout : appLayouts) {
					List<WebTabGroupContext> webTabGroups = ApplicationApi.getWebTabGroupForLayoutID(layout);
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
									if (tabIdAppIdMappingContextList != null && !tabIdAppIdMappingContextList.isEmpty()) {
										for (TabIdAppIdMappingContext tabIdAppIdMappingContext : tabIdAppIdMappingContextList) {
											if (tabIdAppIdMappingContext.getModuleId() > 0) {
												moduleIds.add(tabIdAppIdMappingContext.getModuleId());
											}
											if (tabIdAppIdMappingContext.getSpecialType() != null && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("null") && !tabIdAppIdMappingContext.getSpecialType().equalsIgnoreCase("")) {
												specialTypes.add(tabIdAppIdMappingContext.getSpecialType());
											}
										}
									}
									webtab.setModuleIds(moduleIds);
									webtab.setSpecialTypeModules(specialTypes);
									String moduleName = "*";
									if (webtab.getTypeEnum() == Type.MODULE) {
										if (webtab.getModuleIds() != null && !webtab.getModuleIds().isEmpty()) {
											moduleName = modBean.getModule(webtab.getModuleIds().get(0)).getName();
										} else if (webtab.getConfigJSON() != null) {
											moduleName = (String) webtab.getConfigJSON().get("type");
										}
									}
									webtab.setPermission(NewPermissionUtil.getPermissions(webtab.getType(), moduleName));
									if (webtab.getTypeEnum() == Type.SETTINGS) {
										webtab.setPermission(NewPermissionUtil.getPermissionFromConfig(webtab.getType(), webtab.getConfigJSON()));
									}
									if (AccountUtil.getCurrentUser() != null) {
										webtab.setPermissionVal(ApplicationApi.getRolesPermissionValForTab(webtab.getId(),
												AccountUtil.getCurrentUser().getRoleId()));
									}
									List<FacilioModule> modules = new ArrayList<>();
									if (CollectionUtils.isNotEmpty(webtab.getModuleIds())) {
										for (Long moduleId : webtab.getModuleIds()) {
											modules.add(modBean.getModule(moduleId));
										}
									}
									if (CollectionUtils.isNotEmpty(webtab.getSpecialTypeModules())) {
										for (String specialType : webtab.getSpecialTypeModules()) {
											modules.add(modBean.getModule(specialType));
										}
									}
									if (CollectionUtils.isNotEmpty(modules)) {
										webtab.setModules(modules);
									}
								}
							}
							webTabGroup.setWebTabs(webTabs);
							layout.setWebTabGroupList(webTabGroups);
						}
					}

					//to be removed once new resp structure is changed in client
					application.setWebTabGroups(webTabGroups);
					application.setLayoutType(appLayouts.get(0).getAppLayoutType());
					//----

					application.setLayouts(appLayouts);
					context.put(FacilioConstants.ContextNames.APPLICATION, application);
				}
			}
		}
		else{
			throw new IllegalArgumentException("Application Not Found");
		}
		return false;
	}
}
