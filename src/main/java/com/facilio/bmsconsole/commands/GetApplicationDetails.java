package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.enums.Version;
import com.facilio.bmsconsole.util.NewPermissionUtil;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
		if(roleId == null || roleId == 0){
			if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null) {
				roleId = AccountUtil.getCurrentUser().getRole().getRoleId();
			}
		}
		application = ApplicationApi.getApplicationForId(appId);
		if (application != null) {
			application = ApplicationApi.getApplicationForLinkName(application.getLinkName());
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
			Boolean hasSetupPermission = NewPermissionUtil.hasSetupPermission();
			if (CollectionUtils.isNotEmpty(appLayouts)) {
				for (ApplicationLayoutContext layout : appLayouts) {
					List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getWebTabGroupForLayoutID(layout),WebTabGroupCacheContext.class),WebTabGroupContext.class);

					//handling this to remove groups based on Version
					Version currentVersion = Version.getCurrentVersion();
					if(currentVersion != null) {
						long currentVersionId = currentVersion.getVersionId();
						webTabGroups.removeIf(f -> f.getVersion() != null && (f.getVersion() & currentVersionId) != currentVersionId);
					}

					if (webTabGroups != null && !webTabGroups.isEmpty()) {
						for (WebTabGroupContext webTabGroup : webTabGroups) {
							List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(FieldUtil.getAsMapList(tabBean.getWebTabsForWebGroup(webTabGroup.getId()),WebTabCacheContext.class),WebTabContext.class);

							//handling this to remove webTabs based on Version
							if(currentVersion != null) {
								long currentVersionId = currentVersion.getVersionId();
								webTabs.removeIf(f -> f.getVersion() != null && (f.getVersion() & currentVersionId) != currentVersionId);
							}

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
									webtab.setPermission(NewPermissionUtil.getPermissions(webtab.getType(), moduleName));
									webtab.setPermission(NewPermissionUtil.getTabPermissions(webtab, roleId));
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
								WebTabContext organizationTab = webTabs.stream().filter(tab -> tab.getType() == Type.ORGANIZATION_SETTINGS.getIndex()).findFirst().orElse(null);
								if (webTabGroup.getRoute().equals("general") && organizationTab != null && organizationTab.getFeatureLicense() <= 0) {
									webTabs.removeIf(webTabContext -> webTabContext.getType() == Type.COMPANY_PROFILE.getIndex() || (webTabContext.getType() == Type.TAX.getIndex()));
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

							boolean fetchRoleSpecificTabs = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.FETCH_ROLE_SPECIFIC_WEB_TABS, Boolean.FALSE));
							if(fetchRoleSpecificTabs && CollectionUtils.isNotEmpty(webTabs) &&
									AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getRole() != null && !AccountUtil.getCurrentUser().getRole().isPrevileged()
									&& AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SETUP) && !hasSetupPermission) {
								webTabs.removeIf(t -> (t.getPermissionVal() <= 0 && t.getPermissionVal2() <= 0));
							}

							webTabGroup.setWebTabs(webTabs);
							layout.setWebTabGroupList(webTabGroups);
						}
					}

					//to be removed once new resp structure is changed in client
					if(layout != null && layout.getLayoutDeviceTypeEnum() != null && layout.getLayoutDeviceTypeEnum() == ApplicationLayoutContext.LayoutDeviceType.SETUP) {
						if (CollectionUtils.isNotEmpty(webTabGroups)) {
							webTabGroups.removeIf(group -> CollectionUtils.isEmpty(group.getWebTabs()));
						}
					}
					application.setWebTabGroups(webTabGroups);
					boolean hasMobileSupportedOldApp = Boolean.valueOf(CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.HAS_MOBILE_SUPPORTED_OLD_APP, Boolean.FALSE));
					if(Boolean.FALSE.equals(fetchAllLayouts) && application.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && !SignupUtil.maintenanceAppSignup() && !(ApplicationApi.isRequestFromMobile() && hasMobileSupportedOldApp)){
						if(layout!=null){
							layout.setWebTabGroupList(null);
						}
						application.setWebTabGroups(null);
					}
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
