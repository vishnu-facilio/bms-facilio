package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.WebTabContext.Type;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@Log4j
public class NewPermissionUtil {

    private static Map<String, Integer> moduleTabType = Collections.unmodifiableMap(initModuleMap());
    private static Map<String, Integer> approvalTabType = Collections.unmodifiableMap(initApprovalMap());
    private static Map<String, Integer> calendarTabType = Collections.unmodifiableMap(initCalendarMap());
    private static Map<String, Integer> reportTabType = Collections.unmodifiableMap(initReportMap());
    private static Map<String, Integer> analyticsTabType = Collections.unmodifiableMap(initAnalyticsMap());
    private static Map<String, Integer> kpiTabType = Collections.unmodifiableMap(initKpiMap());
    private static Map<String, Integer> newKpiTabType = Collections.unmodifiableMap(initNewKpiMap());

    private static Map<String, Integer> shiftPlannerTabType = Collections.unmodifiableMap(initShiftPlannerMap());
    private static Map<String, Integer> myAttendanceTabType = Collections.unmodifiableMap(initMyAttendanceMap());
    private static Map<String, Integer> attendanceTabType = Collections.unmodifiableMap(initAttendanceMap());
    private static Map<String, Integer> dashboardTabType = Collections.unmodifiableMap(initDashboardMap());
    private static Map<String, Integer> customTabType = Collections.unmodifiableMap(initCustomMap());
    private static Map<String, Integer> appTabType = Collections.unmodifiableMap(initConnectedAppMap());
    private static Map<String, Long> settingsTabType = Collections.unmodifiableMap(initSettingsMap());
    private static Map<String, Integer> portalOverviewType = Collections.unmodifiableMap(initportalOverviewMap());
    private static Map<String, Integer> notificationType = Collections.unmodifiableMap(initNotificationMap());
    private static Map<String, Integer> indoorFloorplanTabType = Collections.unmodifiableMap(initIndoorFloorplanMap());

    private static Map<String, Integer> homepageTabType = Collections.unmodifiableMap(initHomePageMap());

    private static Map<String, Integer> serviceCatalogTabType = Collections.unmodifiableMap(initServiceCatalogMap());

    private static Map<String, Integer> setupPermissionMap = Collections.unmodifiableMap(initSetupPermissionMap());


    private static Map<String, Integer> initModuleMap() {
        moduleTabType = new HashMap<>();
        moduleTabType.put("CREATE", 1);
        moduleTabType.put("IMPORT", 2); //Deprecated
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

    private static Map<String, Integer> initNewKpiMap() {
        newKpiTabType = new HashMap<>();
        newKpiTabType.put("VIEW", 1);
        return newKpiTabType;
    }


    private static Map<String, Integer> initShiftPlannerMap() {
        shiftPlannerTabType = new HashMap<>();
        shiftPlannerTabType.put("READ", 1);
        shiftPlannerTabType.put("UPDATE", 2);
        shiftPlannerTabType.put("EXPORT", 4);
        return shiftPlannerTabType;
    }

    private static Map<String, Integer> initMyAttendanceMap() {
        myAttendanceTabType = new HashMap<>();
        myAttendanceTabType.put("READ", 1);
        myAttendanceTabType.put("UPDATE", 2);
        return myAttendanceTabType;
    }

    private static Map<String, Integer> initAttendanceMap() {
        attendanceTabType = new HashMap<>();
        attendanceTabType.put("READ", 1);
        attendanceTabType.put("UPDATE", 2);
        return attendanceTabType;
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

    private static Map<String, Integer> initConnectedAppMap() {
        appTabType = new HashMap<>();
        appTabType.put("VIEW", 1);
        return appTabType;
    }

    private static Map<String, Long> initSettingsMap() {
        settingsTabType = new HashMap<>();
        settingsTabType.put("COMPANY_PROFILE", 1L);
        settingsTabType.put("SERVICE_PORTAL", 2L);
        settingsTabType.put("DEVICES", 4L);
        settingsTabType.put("VISITOR_SETTINGS", 8L);
        settingsTabType.put("SERVICE_CATALOGS", 16L);
        settingsTabType.put("USERS", 32L);
        settingsTabType.put("TEAMS", 64L);
        settingsTabType.put("ROLES", 128L);
        settingsTabType.put("REQUESTERS", 256L);
        settingsTabType.put("LABOUR", 512L);
        settingsTabType.put("ASSIGNMENT_RULES", 1024L);
        settingsTabType.put("EMAIL_SETTINGS", 2048L);
        settingsTabType.put("WORKORDER_CUSTOMIZATIONS", 4096L);
        settingsTabType.put("EVENT_FILTERING", 8192L);
        settingsTabType.put("ALARM_FIELDS", 16384L);
        settingsTabType.put("READINGS", 32768L);
        settingsTabType.put("SPACE_ASSET_CUSTOMIZATIONS", 65536L);
        settingsTabType.put("SPACE_CATEGORIES", 131072L);
        settingsTabType.put("OPERATING_HOURS", 262144L);
        settingsTabType.put("WORKFLOWS", 524288L);
        settingsTabType.put("NOTIFICATIONS", 1048576L);
        settingsTabType.put("STATEFLOWS", 2097152L);
        settingsTabType.put("SLA_POLICIES", 4194304L);
        settingsTabType.put("SCHEDULER", 8388608L);
        settingsTabType.put("MODULES", 16777216L);
        settingsTabType.put("CONNECTED_APPS", 33554432L);
        settingsTabType.put("CONNECTORS", 67108864L);
        settingsTabType.put("FUNCTIONS", 134217728L);
        settingsTabType.put("ENERGY_METERS", 268435456L);
        settingsTabType.put("BASELINE", 536870912L);
        settingsTabType.put("AGENTS", 1073741824L);
        settingsTabType.put("CONTROLLERS", 2147483648L);
        settingsTabType.put("LOGS", 4294967296L);
        settingsTabType.put("CONFIGURATION", 8589934592L);
        settingsTabType.put("COMMISSIONING", 17179869184L);
		settingsTabType.put("FEEDBACK_AND_COMPLAINTS", 34359738368L);
		settingsTabType.put("SMART_CONTROLS", 68719476736L);

        return settingsTabType;
    }

    private static Map<String, Integer> initportalOverviewMap() {
        portalOverviewType = new HashMap<>();
        portalOverviewType.put("READ", 1);
        return  portalOverviewType;
    }

    private static Map<String, Integer> initNotificationMap() {
        notificationType = new HashMap<>();
        notificationType.put("READ", 1);
        return notificationType;
    }
    private static Map<String, Integer> initIndoorFloorplanMap() {
        indoorFloorplanTabType = new HashMap<>();
        indoorFloorplanTabType.put("CREATE", 1);
        indoorFloorplanTabType.put("EDIT", 2);
        indoorFloorplanTabType.put("VIEW", 4);
        indoorFloorplanTabType.put("ASSIGN", 8);
        indoorFloorplanTabType.put("BOOKING", 16);
        indoorFloorplanTabType.put("VIEW_ASSIGNMENT", 32);
        indoorFloorplanTabType.put("VIEW_ASSIGNMENT_DEPARTMENT", 64);
        indoorFloorplanTabType.put("VIEW_ASSIGNMENT_OWN", 128);
        indoorFloorplanTabType.put("VIEW_BOOKING", 256);
        indoorFloorplanTabType.put("VIEW_BOOKING_DEPARTMENT",512) ;
        indoorFloorplanTabType.put("VIEW_BOOKING_OWN", 1024);


        return indoorFloorplanTabType;
    }

    private static Map<String, Integer> initHomePageMap() {
        homepageTabType = new HashMap<>();
        homepageTabType.put("CREATE", 1);
        homepageTabType.put("READ", 2);
        homepageTabType.put("UPDATE", 4);
        homepageTabType.put("DELETE", 8);

        return homepageTabType;
    }

    private static Map<String, Integer> initServiceCatalogMap() {
        serviceCatalogTabType = new HashMap<>();
        serviceCatalogTabType.put("CREATE", 1);
        serviceCatalogTabType.put("READ", 2);
        serviceCatalogTabType.put("UPDATE", 4);
        serviceCatalogTabType.put("DELETE", 8);

        return serviceCatalogTabType;
    }


    static Map<Integer, Map<String, List<Permission>>> permissionList = new HashMap<>();

    private static Map<String, Integer> initSetupPermissionMap() {
        setupPermissionMap = new HashMap<>();
        setupPermissionMap.put("ALL", 1);
        return setupPermissionMap;
    }

    static {
        Map<String, List<Permission>> permissionMap = new HashMap<>();

        List<Permission> permissions = new ArrayList<>();

        permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
        permissions.add(new Permission("READ", "Read", moduleTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", moduleTabType.get("UPDATE"), null));
        permissions.add(new Permission("DELETE", "Delete", moduleTabType.get("DELETE"), null));
        permissions.add(new Permission("EXPORT", "Export", moduleTabType.get("EXPORT"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.MODULE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
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
        permissions.add(new Permission("UPDATE_CHANGE_OWNERSHIP", "Change Ownership", moduleTabType.get("UPDATE_CHANGE_OWNERSHIP"), null));
        permissions.add(new Permission("UPDATE_CLOSE_WORKORDER", "Close Workorder", moduleTabType.get("UPDATE_CLOSE_WORKORDER"), null));
        permissions.add(new Permission("UPDATE_TASK", "Add Task", moduleTabType.get("UPDATE_TASK"), null));
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
        permissions.add(new Permission("CREATE", "Create", moduleTabType.get("CREATE"), null));
        permissions.add(new PermissionGroup("Read", readGroup));
        permissions.add(new PermissionGroup("Update", updateGroup));
        permissions.add(new PermissionGroup("Delete", deleteGroup));
        permissions.add(new Permission("EXPORT", "Export", moduleTabType.get("EXPORT"), null));
        permissionMap.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, permissions);
        permissionList.put(Type.MODULE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissionMap.put("*", Arrays.asList(new Permission("CAN_APPROVE", "Can Approve", approvalTabType.get("CAN_APPROVE"), null)));
        permissionList.put(Type.APPROVAL.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CALENDAR", "Calendar", calendarTabType.get("CALENDAR"), null));
        permissions.add(new Permission("PLANNER", "Planner", calendarTabType.get("PLANNER"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.CALENDAR.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("VIEW", "View", reportTabType.get("VIEW"), null));
        permissions.add(new Permission("CREATE_EDIT", "Create/Edit", reportTabType.get("CREATE_EDIT"), null));
        permissions.add(new Permission("EXPORT", "Export", reportTabType.get("EXPORT"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.REPORT.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("SAVE_AS_REPORT", "Save As Report", analyticsTabType.get("SAVE_AS_REPORT"), null));
        permissions.add(new Permission("VIEW", "view", analyticsTabType.get("VIEW"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.ANALYTICS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", kpiTabType.get("CREATE"), null));
        permissions.add(new Permission("READ", "Read", kpiTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", kpiTabType.get("UPDATE"), null));
        permissions.add(new Permission("DELETE", "Delete", kpiTabType.get("DELETE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.KPI.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("VIEW", "View", newKpiTabType.get("VIEW"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.NEW_KPI.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", shiftPlannerTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", shiftPlannerTabType.get("UPDATE"), null));
        permissions.add(new Permission("EXPORT", "Export", shiftPlannerTabType.get("EXPORT"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.SHIFT_PLANNER.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", myAttendanceTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", myAttendanceTabType.get("UPDATE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.MY_ATTENDANCE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", attendanceTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", attendanceTabType.get("UPDATE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.ATTENDANCE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", dashboardTabType.get("CREATE"), null));
        permissions.add(new Permission("VIEW", "View", dashboardTabType.get("VIEW"), null));
        permissions.add(new Permission("EDIT", "Edit", dashboardTabType.get("EDIT"), null));
        permissions.add(new Permission("DELETE", "Delete", dashboardTabType.get("DELETE"), null));
        permissions.add(new Permission("SHARE", "Share", dashboardTabType.get("SHARE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.DASHBOARD.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", customTabType.get("CREATE"), null));
        permissions.add(new Permission("READ", "Read", customTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", customTabType.get("UPDATE"), null));
        permissions.add(new Permission("DELETE", "Delete", customTabType.get("DELETE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.CUSTOM.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", customTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "Update", customTabType.get("UPDATE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.TIMELINE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("VIEW", "View", appTabType.get("VIEW"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.APPS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("COMPANY_PROFILE", "Company Profile", settingsTabType.get("COMPANY_PROFILE"), null));
        permissions.add(new Permission("SERVICE_PORTAL", "Service Portal", settingsTabType.get("SERVICE_PORTAL"), null));
        permissions.add(new Permission("DEVICES", "Devices", settingsTabType.get("DEVICES"), null));
        permissions.add(new Permission("VISITOR_SETTINGS", "Visitor Settings", settingsTabType.get("VISITOR_SETTINGS"), null));
        permissions.add(new Permission("FEEDBACK_AND_COMPLAINTS", "Feedback and Complaints", settingsTabType.get("FEEDBACK_AND_COMPLAINTS"), null));
        permissions.add(new Permission("SMART_CONTROLS", "Smart Controls", settingsTabType.get("SMART_CONTROLS"), null));

        permissionMap.put("general", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("USERS", "Users", settingsTabType.get("USERS"), null));
        permissions.add(new Permission("TEAMS", "Teams", settingsTabType.get("TEAMS"), null));
        permissions.add(new Permission("ROLES", "Roles", settingsTabType.get("ROLES"), null));
        permissions.add(new Permission("REQUESTERS", "Requesters", settingsTabType.get("REQUESTERS"), null));
        permissions.add(new Permission("LABOUR", "Labour", settingsTabType.get("LABOUR"), null));
        permissionMap.put("users_management", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("ASSIGNMENT_RULES", "Assignment Rules", settingsTabType.get("ASSIGNMENT_RULES"), null));
        permissions.add(new Permission("EMAIL_SETTINGS", "Email Settings", settingsTabType.get("EMAIL_SETTINGS"), null));
        permissions.add(new Permission("WORKORDER_CUSTOMIZATIONS", "Customizations", settingsTabType.get("WORKORDER_CUSTOMIZATIONS"), null));
        permissionMap.put("workorder_settings", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("EVENT_FILTERING", "Event Filtering", settingsTabType.get("EVENT_FILTERING"), null));
        permissions.add(new Permission("ALARM_FIELDS", "Alarm Fields", settingsTabType.get("ALARM_FIELDS"), null));
        permissionMap.put("alarm_settings", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("READINGS", "Readings", settingsTabType.get("READINGS"), null));
        permissions.add(new Permission("SPACE_ASSET_CUSTOMIZATIONS", "Customizations", settingsTabType.get("SPACE_ASSET_CUSTOMIZATIONS"), null));
        permissions.add(new Permission("SPACE_CATEGORIES", "Space Categories", settingsTabType.get("SPACE_CATEGORIES"), null));
        permissions.add(new Permission("OPERATING_HOURS", "Operating Hours", settingsTabType.get("OPERATING_HOURS"), null));
        permissionMap.put("space_asset_settings", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("WORKFLOWS", "Workflows", settingsTabType.get("WORKFLOWS"), null));
        permissions.add(new Permission("NOTIFICATIONS", "Notifications", settingsTabType.get("NOTIFICATIONS"), null));
        permissions.add(new Permission("STATEFLOWS", "Stateflows", settingsTabType.get("STATEFLOWS"), null));
        permissions.add(new Permission("SLA_POLICIES", "SLA Policies", settingsTabType.get("SLA_POLICIES"), null));
        permissions.add(new Permission("SCHEDULER", "Scheduler", settingsTabType.get("SCHEDULER"), null));
        permissionMap.put("automations", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("MODULES", "Modules", settingsTabType.get("MODULES"), null));
        permissions.add(new Permission("CONNECTED_APPS", "Connected Apps", settingsTabType.get("CONNECTED_APPS"), null));
        permissions.add(new Permission("CONNECTORS", "Connectors", settingsTabType.get("CONNECTORS"), null));
        permissions.add(new Permission("FUNCTIONS", "Functions", settingsTabType.get("FUNCTIONS"), null));
        permissionMap.put("customization", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("ENERGY_METERS", "Energy Meters", settingsTabType.get("ENERGY_METERS"), null));
        permissions.add(new Permission("BASELINE", "Baseline", settingsTabType.get("BASELINE"), null));
        permissionMap.put("energy_analytics", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissions.add(new Permission("AGENTS", "Agents", settingsTabType.get("AGENTS"), null));
        permissions.add(new Permission("CONTROLLERS", "Controllers", settingsTabType.get("CONTROLLERS"), null));
        permissions.add(new Permission("LOGS", "Logs", settingsTabType.get("LOGS"), null));
        permissions.add(new Permission("CONFIGURATION", "Configuration", settingsTabType.get("CONFIGURATION"), null));
        permissions.add(new Permission("COMMISSIONING", "Commissioning", settingsTabType.get("COMMISSIONING"), null));
        permissionMap.put("agent_configurations", permissions);
        permissionList.put(Type.SETTINGS.getIndex(), permissionMap);


        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", portalOverviewType.get("READ"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.PORTAL_OVERVIEW.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("READ", "Read", notificationType.get("READ"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.NOTIFICATION.getIndex(), permissionMap);


        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", indoorFloorplanTabType.get("CREATE"), null));
        permissions.add(new Permission("EDIT", "Edit", indoorFloorplanTabType.get("EDIT"), null));
        permissions.add(new Permission("VIEW", "View", indoorFloorplanTabType.get("VIEW"), null));
        permissions.add(new Permission("ASSIGN", "Assign", indoorFloorplanTabType.get("ASSIGN"), null));
        permissions.add(new Permission("BOOKING", "Booking", indoorFloorplanTabType.get("BOOKING"), null));

        List<Permission> view_assignment = new ArrayList<>();
        view_assignment.add(new Permission("VIEW_ASSIGNMENT", "All", indoorFloorplanTabType.get("VIEW_ASSIGNMENT"), null));
        view_assignment.add(new Permission("VIEW_ASSIGNMENT_DEPARTMENT", "Department", indoorFloorplanTabType.get("VIEW_ASSIGNMENT_DEPARTMENT"), null));
        view_assignment.add(new Permission("VIEW_ASSIGNMENT_OWN", "Own", indoorFloorplanTabType.get("VIEW_ASSIGNMENT_OWN"), null));
        permissions.add(new PermissionGroup("View assignments",view_assignment ));
        List<Permission> view_booking = new ArrayList<>();
        view_booking.add(new Permission("VIEW_BOOKING", "All", indoorFloorplanTabType.get("VIEW_BOOKING"), null));
        view_booking.add(new Permission("VIEW_BOOKING_DEPARTMENT", "Department", indoorFloorplanTabType.get("VIEW_BOOKING_DEPARTMENT"), null));
        view_booking.add(new Permission("VIEW_BOOKING_OWN", "Own", indoorFloorplanTabType.get("VIEW_BOOKING_OWN"), null));

        permissions.add(new PermissionGroup("View bookings", view_booking));
        permissionMap.put("*", permissions);
        permissionList.put(Type.INDOOR_FLOORPLAN.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", homepageTabType.get("CREATE"), null));
        permissions.add(new Permission("READ", "Edit", homepageTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "View", homepageTabType.get("UPDATE"), null));
        permissions.add(new Permission("DELETE", "Assign", homepageTabType.get("DELETE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.HOMEPAGE.getIndex(), permissionMap);

        permissions = new ArrayList<>();
        permissionMap = new HashMap<>();
        permissions.add(new Permission("CREATE", "Create", serviceCatalogTabType.get("CREATE"), null));
        permissions.add(new Permission("READ", "Edit", serviceCatalogTabType.get("READ"), null));
        permissions.add(new Permission("UPDATE", "View", serviceCatalogTabType.get("UPDATE"), null));
        permissions.add(new Permission("DELETE", "Assign", serviceCatalogTabType.get("DELETE"), null));
        permissionMap.put("*", permissions);
        permissionList.put(Type.SERVICE_CATALOG.getIndex(), permissionMap);

        for(Type type : Type.values()) {
            if(type.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
                permissions = new ArrayList<>();
                permissionMap = new HashMap<>();
                permissions.add(new Permission("ALL", "Allow", setupPermissionMap.get("ALL"), null));
                permissionMap.put("*", permissions);
                permissionList.put(type.getIndex(), permissionMap);
            }
        }
    }

    //Whenever adding a new permission set here add in AppModulePermissionUtil.java also for backward compatability


    public static List<Permission> getPermissions(int tabType, String moduleName){
        Map<String, List<Permission>> stringListMap = permissionList.get(tabType);
        if(stringListMap!=null && !stringListMap.isEmpty()) {
            if (stringListMap.containsKey(moduleName)) {
                return stringListMap.get(moduleName);
            } else {
                return stringListMap.get("*");
            }
        }
        return null;
    }

    public static List<Permission> getPermissionFromConfig(int tabType, JSONObject configJSON) {
		Map<String, List<Permission>> stringListMap = permissionList.get(tabType);
    	String configType = String.valueOf(configJSON.get("type"));
    	if(configType!=null
                && !configType.equals("") && !configType.equalsIgnoreCase("null")) {
    		if(stringListMap.containsKey(configType)) {
    			return stringListMap.get(configType);
			}
		}
    	return null;
	}

    public static PermissionGroup getPermissionGroup(int tabType, String module, String actionName) {
        List<Permission> permissions = getPermissions(tabType, module);
        if (permissions != null) {
            for (Permission p : permissions) {
                if (p instanceof PermissionGroup) {
                    if (((PermissionGroup) p).getDisplayName().equalsIgnoreCase(actionName)) {
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
        WebTabContext.Type webTabType = WebTabContext.Type.valueOf(tabType);
        if(webTabType != null && webTabType.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
            return setupPermissionMap.getOrDefault(action,-1);
        }
        switch (tabType) {
            case 1:
                return moduleTabType.getOrDefault(action, -1);
            case 2:
                return approvalTabType.getOrDefault(action, -1);
            case 3:
                return calendarTabType.getOrDefault(action, -1);
            case 4:
                return reportTabType.getOrDefault(action, -1);
            case 5:
                return analyticsTabType.getOrDefault(action, -1);
            case 6:
                return kpiTabType.getOrDefault(action, -1);
            case 7:
                return dashboardTabType.getOrDefault(action, -1);
            case 8:
                return customTabType.getOrDefault(action, -1);
            case 9:
                return appTabType.getOrDefault(action, -1);
            case 10:
                return settingsTabType.containsKey(action) ? settingsTabType.getOrDefault(action,  -1L) : -1;
            case 11:
                return customTabType.getOrDefault(action, -1);
            case 12:
                return portalOverviewType.getOrDefault(action, -1);
            case 14:
                return indoorFloorplanTabType.getOrDefault(action, -1);
            case 15:
                return homepageTabType.getOrDefault(action, -1);
            case 16:
                return serviceCatalogTabType.getOrDefault(action, -1);
            case 83:
                return shiftPlannerTabType.getOrDefault(action, -1);
            case 84:
                return myAttendanceTabType.getOrDefault(action, -1);
            case 85:
                return attendanceTabType.getOrDefault(action, -1);
            case 87:
                return newKpiTabType.getOrDefault(action, -1);

            default:
                return -1;
        }
    }

    public static boolean hasSetupPermission() throws Exception {
        try {
            boolean hasSetupPermission = false;
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_SETUP)) {
                List<Integer> setupTypes = new ArrayList<>();
                for (WebTabContext.Type type : WebTabContext.Type.values()) {
                    if (type.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
                        setupTypes.add(type.getIndex());
                    }
                }
                Map<String, FacilioField> webTabFieldsMap = FieldFactory.getAsMap(FieldFactory.getWebTabFields());
                Map<String, FacilioField> newPermissionFieldsMap = FieldFactory.getAsMap(FieldFactory.getNewPermissionFields());
                ApplicationContext currentApplication = AccountUtil.getCurrentApp();

                if (currentApplication != null) {
                    GenericSelectRecordBuilder layoutBuilder = new GenericSelectRecordBuilder()
                            .select(FieldFactory.getCountField())
                            .table(ModuleFactory.getApplicationLayoutModule().getTableName())
                            .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(currentApplication.getId()), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", String.valueOf(ApplicationLayoutContext.LayoutDeviceType.SETUP.getIndex()), NumberOperators.EQUALS));
                    List<Map<String, Object>> layoutResult = layoutBuilder.get();
                    if (CollectionUtils.isNotEmpty(layoutResult)) {
                        long layoutCount = ((Number) layoutResult.get(0).get("count")).longValue();
                        if (!(layoutCount > 0)) {
                            return false;
                        }
                    }
                    Role currentUserRole = null;
                    if (AccountUtil.getCurrentUser() != null) {
                        currentUserRole = AccountUtil.getCurrentUser().getRole();
                        if (currentUserRole.isPrevileged()) {
                            return true;
                        }
                    }
                    GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                            .table(ModuleFactory.getNewPermissionModule().getTableName())
                            .innerJoin(ModuleFactory.getWebTabModule().getTableName())
                            .on(ModuleFactory.getWebTabModule().getTableName() + ".ID = " + ModuleFactory.getNewPermissionModule().getTableName() + ".TAB_ID")
                            .select(FieldFactory.getCountField())
                            .andCondition(CriteriaAPI.getCondition(webTabFieldsMap.get("applicationId"), String.valueOf(currentApplication.getId()), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition(newPermissionFieldsMap.get("roleId"), String.valueOf(currentUserRole.getId()), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition(webTabFieldsMap.get("type"), StringUtils.join(setupTypes, ","), NumberOperators.EQUALS));

                    List<Map<String, Object>> result = selectRecordBuilder.get();
                    if (CollectionUtils.isNotEmpty(result)) {
                        long count = ((Number) result.get(0).get("count")).longValue();
                        if (count > 0) {
                            hasSetupPermission = true;
                        }
                    }
                }
            }
            return hasSetupPermission;
        } catch (Exception e) {
            LOGGER.info(e);
        }
        return false;
    }
    public static Map<String, Long> getPermissions(int tabType) {
        WebTabContext.Type webTabType = WebTabContext.Type.valueOf(tabType);
        if(webTabType != null && webTabType.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
            Map<String, Long> maps = new HashMap<>();
            for (String key : setupPermissionMap.keySet()) {
                maps.put(key, Long.valueOf(setupPermissionMap.get(key).toString()));
            }
            return maps;
        }
        switch (tabType) {
            case 1: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : moduleTabType.keySet()) {
                    maps.put(key, Long.valueOf(moduleTabType.get(key).toString()));
                }
                return maps;
            }
            case 2: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : approvalTabType.keySet()) {
                    maps.put(key, Long.valueOf(approvalTabType.get(key).toString()));
                }
                return maps;
            }
            case 3: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : calendarTabType.keySet()) {
                    maps.put(key, Long.valueOf(calendarTabType.get(key).toString()));
                }
                return maps;
            }
            case 4: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : reportTabType.keySet()) {
                    maps.put(key, Long.valueOf(reportTabType.get(key).toString()));
                }
                return maps;
            }
            case 5: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : analyticsTabType.keySet()) {
                    maps.put(key, Long.valueOf(analyticsTabType.get(key).toString()));
                }
                return maps;
            }
            case 6: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : kpiTabType.keySet()) {
                    maps.put(key, Long.valueOf(kpiTabType.get(key).toString()));
                }
                return maps;
            }
            case 7: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : dashboardTabType.keySet()) {
                    maps.put(key, Long.valueOf(dashboardTabType.get(key).toString()));
                }
                return maps;
            }
            case 8: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : customTabType.keySet()) {
                    maps.put(key, Long.valueOf(customTabType.get(key).toString()));
                }
                return maps;
            }
            case 9: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : appTabType.keySet()) {
                    maps.put(key, Long.valueOf(appTabType.get(key).toString()));
                }
                return maps;
            }
            case 10: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : settingsTabType.keySet()) {
                    maps.put(key, settingsTabType.get(key));
                }
                return maps;
            }
            case 12: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : portalOverviewType.keySet()) {
                    maps.put(key, Long.valueOf(portalOverviewType.get(key).toString()));
                }
                return maps;
            }
            case 13: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : notificationType.keySet()) {
                    maps.put(key, Long.valueOf(notificationType.get(key).toString()));
                }
                return maps;
            }
            case 14: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : indoorFloorplanTabType.keySet()) {
                    maps.put(key, Long.valueOf(indoorFloorplanTabType.get(key).toString()));
                }
                return maps;
            }
            case 15: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : homepageTabType.keySet()) {
                    maps.put(key, Long.valueOf(homepageTabType.get(key).toString()));
                }
                return maps;
            }
            case 16: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : serviceCatalogTabType.keySet()) {
                    maps.put(key, Long.valueOf(serviceCatalogTabType.get(key).toString()));
                }
                return maps;
            }
            case 87: {
                Map<String, Long> maps = new HashMap<>();
                for (String key : newKpiTabType.keySet()) {
                    maps.put(key, Long.valueOf(newKpiTabType.get(key).toString()));
                }
                return maps;
            }
            default:
                Map<String, Long> maps = new HashMap<>();
                for (String key : customTabType.keySet()) {
                    maps.put(key, Long.valueOf(customTabType.get(key).toString()));
                }
                return maps;
        }
    }

    public static List<String> getActionsForPermissionValue(int tabType,long value){
        List<String> actionList = new ArrayList<>();
        Map<String, Long> permissionActionMap = getPermissions(tabType);
        for(Map.Entry<String,Long> entry : permissionActionMap.entrySet()){
            if((entry.getValue() & value) == entry.getValue()){
                actionList.add(entry.getKey());
            }
        }
        return actionList;
    }
}
