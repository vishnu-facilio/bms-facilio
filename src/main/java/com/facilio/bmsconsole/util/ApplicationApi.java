package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.beans.ModuleBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.GetApplicationDetails;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.ValueGenerator;
import com.facilio.modules.fields.FacilioField;

public class ApplicationApi {
    public static long addApplicationApi(ApplicationContext application) throws Exception {
        long appId = 0;
        if (application != null) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getApplicationModule().getTableName())
                    .fields(FieldFactory.getApplicationFields());
            appId = builder.insert(FieldUtil.getAsProperties(application));
        }
        return appId;
    }

    public static ApplicationContext getDefaultApplication() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("Application.IS_DEFAULT", "isDefault", String.valueOf(true), BooleanOperators.IS));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if (applications != null && !applications.isEmpty()) {
            return applications.get(0);
        }
        return null;
    }

    public static ApplicationContext getApplicationForId(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getIdCondition(appId, ModuleFactory.getApplicationModule()));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if (applications != null && !applications.isEmpty()) {
            return applications.get(0);
        }
        return null;
    }
    
    public static ApplicationContext getApplicationForLinkName(String appLinkName) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", appLinkName, StringOperators.IS));
        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
                ApplicationContext.class);
        if (applications != null && !applications.isEmpty()) {
            return applications.get(0);
        }
        return null;
    }

    
    public static long getApplicationIdForLinkName(String appLinkName) throws Exception {
    	if(StringUtils.isNotEmpty(appLinkName)) {
	        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
	                .table(ModuleFactory.getApplicationModule().getTableName()).select(FieldFactory.getApplicationFields())
	                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", appLinkName, StringOperators.IS));
	        List<ApplicationContext> applications = FieldUtil.getAsBeanListFromMapList(builder.get(),
	                ApplicationContext.class);
	        if (applications != null && !applications.isEmpty()) {
	            return applications.get(0).getId();
	        }
    	}
        return -1;
    }

    public static List<WebTabGroupContext> getWebTabgroups() throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields());
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class);
        return webTabGroups;
    }

    public static void updateWebTabGroups(WebTabGroupContext webTabGroup) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).fields(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getIdCondition(webTabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
        builder.update(FieldUtil.getAsProperties(webTabGroup));
    }

    public static List<WebTabGroupContext> getWebTabGroupsForAppId(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName()).select(FieldFactory.getWebTabGroupFields())
                .andCondition(CriteriaAPI.getCondition("WebTab_Group.APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        List<WebTabGroupContext> webTabGroups = FieldUtil.getAsBeanListFromMapList(builder.get(),
                WebTabGroupContext.class);
        return webTabGroups;
    }

    public static List<WebTabContext> getWebTabsForWebGroup(long webTabGroupId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getCondition("WebTab.GROUP_ID", "groupId", String.valueOf(webTabGroupId), NumberOperators.EQUALS));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        return webTabs;
    }

    public static List<NewPermission> getPermissionsForWebTab(long webTabId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .select(FieldFactory.getNewPermissionFields())
                .andCondition(CriteriaAPI.getCondition("NewPermission.TAB_ID", "tabId", String.valueOf(webTabId), NumberOperators.EQUALS));
        List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
        return permissions;
    }

    public static List<TabIdAppIdMappingContext> getTabIdModules(long tabId) throws Exception{
        List<TabIdAppIdMappingContext> tabidMappings = null;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
                .select(FieldFactory.getTabIdAppIdMappingFields())
                .andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
         tabidMappings = FieldUtil.getAsBeanListFromMapList(builder.get(), TabIdAppIdMappingContext.class);
         return tabidMappings;
    }

    public static long getRolesPermissionValForTab(long tabId, long roleId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewPermissionModule().getTableName())
                .select(FieldFactory.getNewPermissionFields())
                .andCondition(CriteriaAPI.getCondition("NewPermission.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("NewPermission.ROLE_ID", "roleId", String.valueOf(roleId), NumberOperators.EQUALS));
        List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
        if (permissions != null && !permissions.isEmpty()) {
            return permissions.get(0).getPermission();
        }
        return -1;
    }

    public static WebTabContext getWebTab(long tabId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabModule().getTableName()).select(FieldFactory.getWebTabFields())
                .andCondition(CriteriaAPI.getIdCondition(tabId, ModuleFactory.getWebTabModule()));
        List<WebTabContext> webTabs = FieldUtil.getAsBeanListFromMapList(builder.get(), WebTabContext.class);
        if (webTabs != null && !webTabs.isEmpty()) {
            return webTabs.get(0);
        }
        return null;
    }

    public static List<String> getModulesForTab(long tabId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<TabIdAppIdMappingContext> tabIdAppIdMappingContextList = ApplicationApi.getTabIdModules(tabId);
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
        List<String> moduleNames = new ArrayList<>();
        if (!moduleIds.isEmpty()) {
            for (long moduleId : moduleIds) {
                moduleNames.add(modBean.getModule(moduleId).getName());
            }
        }
        if(!specialTypes.isEmpty()) {
            moduleNames.addAll(specialTypes);
        }
        return moduleNames;
    }
	
	public static List<Long> getModuleIdsForTab(long tabId) throws Exception {
		List<Long> ids = new ArrayList<Long>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getTabIdAppIdMappingModule().getTableName())
				.select(FieldFactory.getTabIdAppIdMappingFields())
				.andCondition(CriteriaAPI.getCondition("TABID_MODULEID_APPID_MAPPING.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS));
		List<TabIdAppIdMappingContext> tabidMappings = FieldUtil.getAsBeanListFromMapList(builder.get(), TabIdAppIdMappingContext.class);
		if(tabidMappings!=null && !tabidMappings.isEmpty()) {
			for(TabIdAppIdMappingContext tabIdAppIdMappingContext :tabidMappings ) {
				ids.add(tabIdAppIdMappingContext.getModuleId());
			}
		}
		return ids;
	}
	
	public static long addUserInApp(User user, boolean shouldThrowError) throws Exception {
		long userExisitsInApp = checkIfUserAlreadyPresentInApp(user.getUid(), user.getApplicationId(), user.getOrgId());
		if (userExisitsInApp <= 0) {
		   return AccountUtil.getUserBean().addToORGUsersApps(user, true);
		}
		else { 
			if(shouldThrowError) {
				throw new IllegalArgumentException("User already exists in app");
			}
		}
		return userExisitsInApp;
	}
		
	public static int deleteUserFromApp(User user, long appId) throws Exception {
	   return AccountUtil.getUserBean().deleteUserFromApps(user, appId);
	}
	
	public static AppDomain getAppDomainForApplication(long applicationId) throws Exception {
		List<FacilioField> fields = FieldFactory.getApplicationFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
									.select(fields)
									.table(ModuleFactory.getApplicationModule().getTableName())
									;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(applicationId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			long appDomainId = (long)list.get(0).get("appDomainId");
			return IAMAppUtil.getAppDomain(appDomainId);
		}
		return null;
	}
	
	public static void addDefaultApps(long orgId) throws Exception {
		List<FacilioField> fields = FieldFactory.getApplicationFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getApplicationModule().getTableName())
				.fields(fields);

		Organization org = AccountUtil.getOrgBean(orgId).getOrg(orgId);
		AppDomain facilioApp = IAMAppUtil.getAppDomain(AccountUtil.getDefaultAppDomain());
		ApplicationContext facilioApplication = new ApplicationContext(orgId, "Facilio", true, facilioApp.getId(), FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, ApplicationContext.AppLayoutType.DUAL.getIndex());

		
		AppDomain servicePortalApp = IAMAppUtil.getAppDomain(org.getDomain()+ (FacilioProperties.isProduction() ? ".facilioportal.com" : ".facilstack.com" ));
		ApplicationContext servicePortalapplication = new ApplicationContext(orgId, "OCCUPANT PORTAL", false, servicePortalApp.getId(), FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex());
		
		AppDomain tenantPortalApp = IAMAppUtil.getAppDomain(org.getDomain()+ ".faciliotenants.com");
		ApplicationContext tenantPortalapplication = new ApplicationContext(orgId, "TENANT PORTAL", false, tenantPortalApp.getId(), FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex());
		
		AppDomain vendorPortalApp = IAMAppUtil.getAppDomain(org.getDomain()+ ".faciliovendors.com");
		ApplicationContext vendorPortalapplication = new ApplicationContext(orgId, "VENDOR PORTAL", false, vendorPortalApp.getId(), FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex());
		
		AppDomain clientPortalApp = IAMAppUtil.getAppDomain(org.getDomain()+ ".facilioclients.com");
		ApplicationContext clientPortalapplication = new ApplicationContext(orgId, "CLIENT PORTAL", false, clientPortalApp.getId(), FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex());
		
		ApplicationContext facilioAgentApplication = new ApplicationContext(orgId, "Agent", false, facilioApp.getId(), FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP, ApplicationContext.AppLayoutType.SINGLE.getIndex());

		List<ApplicationContext> applicationsDefault = new ArrayList<ApplicationContext>();
		applicationsDefault.add(facilioApplication);
		applicationsDefault.add(servicePortalapplication);
		applicationsDefault.add(tenantPortalapplication);
		applicationsDefault.add(vendorPortalapplication);
		applicationsDefault.add(clientPortalapplication);
		applicationsDefault.add(facilioAgentApplication);
			
		List<Map<String, Object>> props = FieldUtil.getAsMapList(applicationsDefault, ApplicationContext.class);

		insertBuilder.addRecords(props);
		insertBuilder.save();
	}
	
	public static void addDefaultAppDomains(long orgId) throws Exception {
		Organization org = AccountUtil.getOrgBean().getOrg(orgId);
		AppDomain servicePortalAppDomain = new AppDomain(org.getDomain() + (FacilioProperties.isProduction() ? ".facilioportal.com" : ".facilstack.com" ), AppDomainType.SERVICE_PORTAL.getIndex(), GroupType.TENANT_OCCUPANT_PORTAL.getIndex(), orgId);
		AppDomain vendorPortalAppDomain = new AppDomain(org.getDomain() + ".faciliovendors.com", AppDomainType.VENDOR_PORTAL.getIndex(), GroupType.VENDOR_PORTAL.getIndex(), orgId);
		AppDomain tenantPortalAppDomain = new AppDomain(org.getDomain() + ".faciliotenants.com", AppDomainType.TENANT_PORTAL.getIndex(), GroupType.TENANT_OCCUPANT_PORTAL.getIndex(), orgId);
		AppDomain clientPortalAppDomain = new AppDomain(org.getDomain() + ".facilioclients.com", AppDomainType.CLIENT_PORTAL.getIndex(), GroupType.CLIENT_PORTAL.getIndex(), orgId);
		
		List<AppDomain> appDomains = new ArrayList<AppDomain>();
		appDomains.add(servicePortalAppDomain);
		appDomains.add(vendorPortalAppDomain);
		appDomains.add(tenantPortalAppDomain);
		appDomains.add(clientPortalAppDomain);
		
		IAMAppUtil.addAppDomains(appDomains);
	}
	
	public static long checkIfUserAlreadyPresentInApp(long userId, long applicationId, long orgId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getOrgUserAppsFields());
		fields.addAll(AccountConstants.getAppOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_User_Apps")
				.innerJoin("ORG_Users")
				.on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID","applicationId" , String.valueOf(applicationId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID","orgId" , String.valueOf(orgId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.USERID","userId" , String.valueOf(userId), NumberOperators.EQUALS));
				
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			IAMUserUtil.setIAMUserPropsv3(props, orgId, false);
			if(CollectionUtils.isNotEmpty(props)) {
				Map<String, Object> map = props.get(0);
				return (long)map.get("id");
			}
		}
		return -1;
	
	}
	
	public static String getApplicationName(long appId) throws Exception {
		
		String appDomainName = "Facilio";
		if(appId > 0) {
			ApplicationContext app = getApplicationForId(appId);
			if(app != null) {
				return app.getName();
			}
		}
		return appDomainName;
	}

	public static String getPushNotificationKeyForApp(int appType) throws Exception {
		if(appType <= 0 || appType == 1) {
			return FacilioProperties.getPushNotificationKey();
		}
		else if(appType == 4) {
			return FacilioProperties.getPortalPushNotificationKey();
		}
		else {
			return FacilioProperties.getPortalPushNotificationKey();
		}
	}
	
	public static Map<Long, Map<String, Object>> getScopingMapForApp(long appId, long orgId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScopingConfigFields())
				.table("Scoping_Config")
				.innerJoin("Scoping")
				.on("Scoping.ID = Scoping_Config.SCOPING_ID")
				.innerJoin("Application")
				.on("Application.SCOPING_ID = Scoping.ID")
				.andCondition(CriteriaAPI.getCondition("Application.ID","appId" , String.valueOf(appId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Map<String, Object>> moduleScoping = new HashMap<Long, Map<String,Object>>();
		if(CollectionUtils.isNotEmpty(props)) {
			List<ScopingConfigContext> list = FieldUtil.getAsBeanListFromMapList(props, ScopingConfigContext.class);
			for(ScopingConfigContext scopingConfig : list) {
				Map<String, Object> scopingfields = null; 
				computeValueForScopingField(scopingConfig);
				 if(moduleScoping.containsKey(scopingConfig.getModuleId())) {
					 scopingfields = moduleScoping.get(scopingConfig.getModuleId());
				 }
				 else {
					 scopingfields = new HashMap<String, Object>();
				 }
				 scopingfields.put(scopingConfig.getFieldName(), scopingConfig);
				 moduleScoping.put(scopingConfig.getModuleId(), scopingfields);
			}
			return moduleScoping;
		}
		return null;
	}
	
	private static void computeValueForScopingField(ScopingConfigContext sc) throws Exception {
		if(sc != null) {
			if(StringUtils.isNotEmpty(sc.getValue())) {
				return;
			}
			if(StringUtils.isEmpty(sc.getFieldValueGenerator())) {
				throw new IllegalArgumentException("Scoping field --> "+ sc.getFieldName() +" must either have avlue or value generator associated");
			}
			Class<? extends ValueGenerator> classObject = (Class<? extends ValueGenerator>) Class.forName(sc.getFieldValueGenerator());
       		ValueGenerator valueGenerator = classObject.newInstance();
       		 if(AccountUtil.getCurrentUser().getAppDomain() != null) {
       			sc.setValue(valueGenerator.generateValueForCondition(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType()));
       		 }
       	 	
		}
	}
	
	public static long getApplicationIdForApp(AppDomain appDomain) throws Exception {
		List<FacilioField> fields = FieldFactory.getApplicationFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
									.select(fields)
									.table(ModuleFactory.getApplicationModule().getTableName())
									;
		selectBuilder.andCondition(CriteriaAPI.getCondition("APP_DOMAIN_ID", "appDomainId", String.valueOf(appDomain.getId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return (long)list.get(0).get("id");
		}
		return -1;
	}
	
	public static long getApplicationIdForAppDomain(String appDomain) throws Exception {
		
		AppDomain appDomainObj = IAMAppUtil.getAppDomain(appDomain);
		if(appDomainObj == null) {
			throw new IllegalArgumentException("Invalid app domain");
		}
		long appId = ApplicationApi.getApplicationIdForApp(appDomainObj);
		return appId;
	}
	

}
