package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import lombok.SneakyThrows;

public class GetApplicationDetails extends FacilioCommand {
	@SneakyThrows
	private boolean hasLicense(WebTabContext tab) {
		AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.getFeatureLicense(tab.getFeatureLicense());
		boolean isEnabled = true;
		if (license != null) {
			isEnabled = AccountUtil.isFeatureEnabled(license);
		}
		return isEnabled;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		String  appName  = (String)context.get(FacilioConstants.ContextNames.LAYOUT_APP_TYPE);
		Boolean fetchAllLayouts  = (Boolean)context.get(FacilioConstants.ContextNames.FETCH_ALL_LAYOUTS);
		Boolean considerRole  = (Boolean) context.get(FacilioConstants.ContextNames.CONSIDER_ROLE);
		Long roleId  = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

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
					List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getWebTabGroupForLayoutID(layout),WebTabGroupCacheContext.class),WebTabGroupContext.class);
					if (webTabGroups != null && !webTabGroups.isEmpty()) {
						for (WebTabGroupContext webTabGroup : webTabGroups) {
							List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getWebTabsForWebGroup(webTabGroup.getId()),WebTabCacheContext.class),WebTabContext.class);
							if (webTabs != null && !webTabs.isEmpty()) {
								ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
								for (WebTabContext webtab : webTabs) {
									webtab.setPermissions(ApplicationApi.getPermissionsForWebTab(webtab.getId()));
									List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getTabIdModules(webtab.getId()),TabIdAppIdMappingCacheContext.class),TabIdAppIdMappingContext.class);
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
									if(V3PermissionUtil.isFeatureEnabled()){
										webtab.setPermission(V3PermissionUtil.getPermissionValue(webtab,roleId));
									}else{
										webtab.setPermission(NewPermissionUtil.getPermissions(webtab.getType(), moduleName));
									}
									if (webtab.getTypeEnum() == Type.SETTINGS) {
										if(V3PermissionUtil.isFeatureEnabled()){
											webtab.setPermission(V3PermissionUtil.getPermissionValue(webtab,roleId));
										}else{
											webtab.setPermission(NewPermissionUtil.getPermissionFromConfig(webtab.getType(), webtab.getConfigJSON()));
										}
									}
									if (AccountUtil.getCurrentUser() != null) {
										if(V3PermissionUtil.isFeatureEnabled()){
											NewPermission permission = ApplicationApi.getRolesPermissionForTab(webtab.getId(),
													AccountUtil.getCurrentUser().getRoleId());
											if (permission != null) {
												webtab.setPermissionVal(permission.getPermission());
												webtab.setPermissionVal2(permission.getPermission2());
											}
										}
										else {
											webtab.setPermissionVal(ApplicationApi.getRolesPermissionValForTab(webtab.getId(),
													AccountUtil.getCurrentUser().getRoleId()));
										}
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
								if (webTabGroup.getRoute().equals("general")) {
									webTabs.removeIf(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
											? (webTabContext -> webTabContext.getType() == Type.COMPANY_PROFILE.getIndex() || (webTabContext.getType() == Type.TAX.getIndex()))
											: (webTabContext -> webTabContext.getType() == Type.ORGANIZATION_SETTINGS.getIndex()));
								}
							}
							if(layout != null && layout.getLayoutDeviceTypeEnum() != null && layout.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.SETUP) {
								webTabs.removeIf(t -> !hasLicense(t));
							}
							if(!fetchAllLayouts && CollectionUtils.isNotEmpty(webTabs)) {
								webTabs.removeIf(t -> !hasLicense(t));
							}
							if(CollectionUtils.isNotEmpty(webTabs) && considerRole != null && considerRole && AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null && !AccountUtil.getCurrentUser().getRole().isPrevileged()) {
								webTabs.removeIf(t -> (t.getPermissionVal() <= 0 && t.getPermissionVal2() <= 0));
							}
							webTabGroup.setWebTabs(webTabs);
							layout.setWebTabGroupList(webTabGroups);
						}
					}

					//to be removed once new resp structure is changed in client
					if(CollectionUtils.isNotEmpty(webTabGroups)) {
						webTabGroups.removeIf(group -> CollectionUtils.isEmpty(group.getWebTabs()));
					}
					application.setWebTabGroups(webTabGroups);
					application.setLayoutType(appLayouts.get(0).getAppLayoutType());

					application.setLayouts(appLayouts);
					application.setHasSetupPermission(NewPermissionUtil.hasSetupPermission());
					context.put(FacilioConstants.ContextNames.APPLICATION, application);
				}
			}
		}
		else {
			throw new IllegalArgumentException("Application Not Found");
		}
		return false;
	}
}
