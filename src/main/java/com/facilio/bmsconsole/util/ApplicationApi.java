package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.accounts.dto.NewPermission;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.TabIdAppIdMappingContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

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

	public static ApplicationContext getDefaultApplication() throws Exception{
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
	
	public static ApplicationContext getApplicationForId(long appId) throws Exception{
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
	
	public static long getRolesPermissionValForTab(long tabId, long roleId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.select(FieldFactory.getNewPermissionFields())
				.andCondition(CriteriaAPI.getCondition("NewPermission.TAB_ID", "tabId", String.valueOf(tabId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("NewPermission.ROLE_ID", "roleId", String.valueOf(roleId), NumberOperators.EQUALS));
		List<NewPermission> permissions = FieldUtil.getAsBeanListFromMapList(builder.get(), NewPermission.class);
		if(permissions!=null && !permissions.isEmpty()) {
			return permissions.get(0).getPermission();
		}
		return -1;
	}
}
