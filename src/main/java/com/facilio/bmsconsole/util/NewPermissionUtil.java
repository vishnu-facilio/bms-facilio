package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext.Type;

public class NewPermissionUtil {

	private static Map<String, Integer> moduleTabType = Collections.unmodifiableMap(initModuleMap());
	private static Map<String, Integer> approvalTabType = Collections.unmodifiableMap(initApprovalMap());
	private static Map<String, Integer> calendarTabType = Collections.unmodifiableMap(initCalendarMap());
	private static Map<String, Integer> reportTabType = Collections.unmodifiableMap(initReportMap());
	private static Map<String, Integer> analyticsTabType = Collections.unmodifiableMap(initAnalyticsMap());
	private static Map<String, Integer> kpiTabType = Collections.unmodifiableMap(initKpiMap());
	private static Map<String, Integer> dashboardTabType = Collections.unmodifiableMap(initDashboardMap());
	private static Map<String, Integer> customTabType = Collections.unmodifiableMap(initCustomMap());

	private static Map<String, Integer> initModuleMap() {
		moduleTabType = new HashMap<>();
		moduleTabType.put("CREATE", 1);
		moduleTabType.put("IMPORT", 2);
		moduleTabType.put("READ", 4);
		moduleTabType.put("UPDATE", 8);
		moduleTabType.put("DELETE", 16);
		moduleTabType.put("EXPORT", 32);
		moduleTabType.put("READ_TEAM", 64);
		moduleTabType.put("READ_OWN", 128);
		moduleTabType.put("UPDATE_TEAM", 256);
		moduleTabType.put("UPDATE_OWN", 512);
		moduleTabType.put("UPDATE_CHANGE_OWNERSHIP", 1024);
		moduleTabType.put("UPDATE_CLOSE_WORKORDER", 2048);
		moduleTabType.put("UPDATE_TASK", 4096);
		moduleTabType.put("DELETE_TEAM", 8192);
		moduleTabType.put("DELETE_OWN", 16384);
		return moduleTabType;
	}

	private static Map<String, Integer> initApprovalMap() {
		approvalTabType = new HashMap<>();
		approvalTabType.put("CAN_APPROVE", 1);
		return approvalTabType;
	}

	private static Map<String, Integer> initCalendarMap() {
		calendarTabType = new HashMap<>();
		calendarTabType.put("CALENDAR", 1);
		calendarTabType.put("PLANNER", 2);
		return calendarTabType;
	}

	private static Map<String, Integer> initReportMap() {
		reportTabType = new HashMap<>();
		reportTabType.put("VIEW", 1);
		reportTabType.put("CREATE_EDIT", 2);
		reportTabType.put("EXPORT", 4);
		return reportTabType;
	}

	private static Map<String, Integer> initAnalyticsMap() {
		analyticsTabType = new HashMap<>();
		analyticsTabType.put("SAVE_AS_REPORT", 1);
		analyticsTabType.put("VIEW", 2);
		return analyticsTabType;
	}

	private static Map<String, Integer> initKpiMap() {
		kpiTabType = new HashMap<>();
		kpiTabType.put("CREATE", 1);
		kpiTabType.put("READ", 2);
		kpiTabType.put("UPDATE", 4);
		kpiTabType.put("DELETE", 8);
		return kpiTabType;
	}

	private static Map<String, Integer> initDashboardMap() {
		dashboardTabType = new HashMap<>();
		dashboardTabType.put("CREATE", 1);
		dashboardTabType.put("VIEW", 2);
		dashboardTabType.put("EDIT", 4);
		dashboardTabType.put("DELETE", 8);
		dashboardTabType.put("SHARE", 16);
		return dashboardTabType;
	}
	
	private static Map<String, Integer> initCustomMap() {
		customTabType = new HashMap<>();
		customTabType.put("CREATE", 1);
		customTabType.put("READ", 2);
		customTabType.put("UPDATE", 4);
		customTabType.put("DELETE", 8);
		return customTabType;
	}

	static Map<Integer, Map<String, List<Permission>>> permissionList = new HashMap();

	static {
		Map<String, List<Permission>> permissionMap = new HashMap<>();

		List<Permission> permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
		permissions.add(new Permission("IMPORT", "Import", moduleTabType.get("IMPORT"), null));
		permissions.add(new Permission("READ", "Read", moduleTabType.get("READ"), null));
		permissions.add(new Permission("UPDATE", "Update", moduleTabType.get("UPDATE"), null));
		permissions.add(new Permission("DELETE", "Delete", moduleTabType.get("DELETE"), null));
		permissions.add(new Permission("EXPORT", "Export", moduleTabType.get("EXPORT"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.MODULE.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
		permissions.add(new Permission("IMPORT", "Import", moduleTabType.get("IMPORT"), null));
		List<Permission> readGroup = new ArrayList<>();
		readGroup.add(new Permission("READ", "All", moduleTabType.get("READ"), null));
		readGroup.add(new Permission("READ_TEAM", "Team", moduleTabType.get("READ_TEAM"), null));
		readGroup.add(new Permission("READ_OWN", "Own", moduleTabType.get("READ_OWN"), null));
		permissions.add(new PermissionGroup("Read", readGroup));
		List<Permission> updateGroup = new ArrayList<>();
		updateGroup.add(new Permission("UPDATE", "All", moduleTabType.get("UPDATE"), null));
		updateGroup.add(new Permission("UPDATE_TEAM", "Team", moduleTabType.get("UPDATE_TEAM"), null));
		updateGroup.add(new Permission("UPDATE_OWN", "Own", moduleTabType.get("UPDATE_OWN"), null));
		permissions.add(new PermissionGroup("Update", updateGroup));
		List<Permission> deleteGroup = new ArrayList<>();
		deleteGroup.add(new Permission("DELETE", "All", moduleTabType.get("DELETE"), null));
		deleteGroup.add(new Permission("DELETE_TEAM", "Team", moduleTabType.get("DELETE_TEAM"), null));
		deleteGroup.add(new Permission("DELETE_OWN", "Own", moduleTabType.get("DELETE_OWN"), null));
		permissions.add(new PermissionGroup("Delete", deleteGroup));
		permissions.add(new Permission("EXPORT", "Export", moduleTabType.get("EXPORT"), null));
		permissionMap.put("workorder", permissions);
		permissionList.put(Type.MODULE.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
		permissions.add(new Permission("IMPORT", "Import", moduleTabType.get("IMPORT"), null));
		List<Permission> read = new ArrayList<>();
		read.add(new Permission("READ", "All", moduleTabType.get("READ"), null));
		read.add(new Permission("READ_OWN", "Own", moduleTabType.get("READ_OWN"), null));
		permissions.add(new PermissionGroup("Read", read));
		List<Permission> update = new ArrayList<>();
		update.add(new Permission("UPDATE", "All", moduleTabType.get("UPDATE"), null));
		update.add(new Permission("UPDATE_OWN", "Own", moduleTabType.get("UPDATE_OWN"), null));
		permissions.add(new PermissionGroup("Update", update));
		List<Permission> delete = new ArrayList<>();
		delete.add(new Permission("DELETE", "All", moduleTabType.get("DELETE"), null));
		delete.add(new Permission("DELETE_OWN", "Own", moduleTabType.get("DELETE_OWN"), null));
		permissions.add(new PermissionGroup("Delete", delete));
		permissions.add(new Permission("EXPORT", "Export", moduleTabType.get("EXPORT"), null));
		permissionMap.put("inventory", permissions);
		permissionList.put(Type.MODULE.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissionMap.put("*",
				Arrays.asList(new Permission("CAN_APPROVE", "Can Approve", approvalTabType.get("CAN_APPROVE"), null)));
		permissionList.put(Type.APPROVAL.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CALENDAR", "Calendar", calendarTabType.get("CALENDAR"), null));
		permissions.add(new Permission("PLANNER", "Planner", calendarTabType.get("PLANNER"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.CALENDAR.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("VIEW", "View", reportTabType.get("VIEW"), null));
		permissions.add(new Permission("CREATE_EDIT", "Create/Edit", reportTabType.get("CREATE_EDIT"), null));
		permissions.add(new Permission("EXPORT", "Export", reportTabType.get("EXPORT"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.REPORT.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions
				.add(new Permission("SAVE_AS_REPORT", "Save As Report", analyticsTabType.get("SAVE_AS_REPORT"), null));
		permissions.add(new Permission("VIEW", "view", analyticsTabType.get("VIEW"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.ANALYTICS.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", kpiTabType.get("CREATE"), null));
		permissions.add(new Permission("READ", "Read", kpiTabType.get("READ"), null));
		permissions.add(new Permission("UPDATE", "Update", kpiTabType.get("UPDATE"), null));
		permissions.add(new Permission("DELETE", "Delete", kpiTabType.get("DELETE"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.KPI.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", dashboardTabType.get("CREATE"), null));
		permissions.add(new Permission("VIEW", "View", dashboardTabType.get("VIEW"), null));
		permissions.add(new Permission("EDIT", "Edit", dashboardTabType.get("EDIT"), null));
		permissions.add(new Permission("DELETE", "Delete", dashboardTabType.get("DELETE"), null));
		permissions.add(new Permission("SHARE", "Share", dashboardTabType.get("SHARE"), null));
		permissionList.put(Type.DASHBOARD.getIndex(), permissionMap);

		permissions = new ArrayList<>();
		permissions.add(new Permission("CREATE", "Create", customTabType.get("CREATE"), null));
		permissions.add(new Permission("READ", "Read", customTabType.get("READ"), null));
		permissions.add(new Permission("UPDATE", "Update", customTabType.get("UPDATE"), null));
		permissions.add(new Permission("DELETE", "Delete", customTabType.get("DELETE"), null));
		permissionMap.put("*", permissions);
		permissionList.put(Type.CUSTOM.getIndex(), permissionMap);

	}

	public static List<Permission> getPermissions(int tabId, String moduleName) {
		Map<String, List<Permission>> stringListMap = permissionList.get(tabId);
		if (stringListMap.containsKey(moduleName)) {
			return stringListMap.get(moduleName);
		} else {
			return stringListMap.get("*");
		}
	}

	public static PermissionGroup getPermissionGroup(int tabId, String module, String actionName) {
		List<Permission> permissions = getPermissions(tabId, module);
		if (permissions != null) {
			for (Permission p : permissions) {
				if (p instanceof PermissionGroup) {
					if (((PermissionGroup) p).getDisplayName().equals(actionName)) {
						return (PermissionGroup) p;
					}
				}
			}
		}
		return null;
	}

	public static long getPermissionValue(int tabType, String moduleName, String action) {
		List<Permission> permissions = permissionList.get(tabType).get(moduleName);
		for (Permission permission : permissions) {
			if (permission.getActionName().equals(action)) {
				return permission.getValue();
			}
		}
		return -1;
	}

	public static long getPermissionValue(int tabType, String action) {
		switch (tabType) {
		case 1:
			return moduleTabType.get(action);
		case 2:
			return approvalTabType.get(action);
		case 3:
			return calendarTabType.get(action);
		case 4:
			return reportTabType.get(action);
		case 5:
			return analyticsTabType.get(action);
		case 6:
			return kpiTabType.get(action);
		case 7:
			return dashboardTabType.get(action);
		case 8:
			return customTabType.get(action);
		default:
			return -1;
		}
	}
}
