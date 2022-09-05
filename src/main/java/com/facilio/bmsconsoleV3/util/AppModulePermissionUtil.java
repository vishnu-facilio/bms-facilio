package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleAppPermissionChild;
import com.facilio.bmsconsoleV3.context.ModuleAppPermission;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class AppModulePermissionUtil {

    public enum PermissionMapping implements FacilioIntEnum {
        GROUP1PERMISSION(1, FacilioConstants.PermissionKeys.PERMISSION1),
        GROUP2PERMISSION(2, FacilioConstants.PermissionKeys.PERMISSION2);

        private int groupId;
        private String permissionKey;

        public String getPermissionKey() {
            return permissionKey;
        }

        public void setPermissionKey(String permissionKey) {
            this.permissionKey = permissionKey;
        }

        PermissionMapping(int groupId, String permissionKey) {
            this.groupId = groupId;
            this.permissionKey = permissionKey;
        }

        public int getGroupId() {
            return groupId;
        }
    }

    private static List<Permission> permissions = Arrays.asList(
            new Permission(getExponentValue(1), "CREATE", "Create", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(2), "IMPORT", "Import", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(3), "READ", "Read", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(4), "UPDATE", "Update", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(5), "DELETE", "Delete", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(6), "EXPORT", "Export", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(7), "READ_TEAM", "Read Team", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(8), "READ_OWN", "Read Own", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(9), "UPDATE_TEAM", "Update Team", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(10), "UPDATE_OWN", "Update Own", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(11), "UPDATE_CHANGE_OWNERSHIP", "Change Ownership", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(12), "UPDATE_CLOSE_WORKORDER", "Close Workorder", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(13), "UPDATE_TASK", "Update Task", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(14), "DELETE_TEAM", "Delete Team", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(15), "DELETE_OWN", "Delete Own", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(16), "CAN_APPROVE", "Can Approve", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(17), "CALENDAR", "Calender", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(18), "PLANNER", "Planner", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(19), "VIEW", "View", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(20), "SAVE_AS_REPORT", "Save As Report", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(21), "SHARE", "Share", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(22), "COMPANY_PROFILE", "Company Profile", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(23), "PORTALS", "Portals", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(24), "DEVICES", "Devices", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(25), "VISITOR_SETTINGS", "Visitor Setting", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(26), "SERVICE_CATALOGS", "Serivce Catalogs", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(27), "USERS", "Users", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(28), "TEAMS", "Teams", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(29), "ROLES", "Roles", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(30), "REQUESTERS", "Requesters", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(31), "LABOUR", "Labour", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(32), "ASSIGNMENT_RULES", "Assignment Rules", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(33), "EMAIL_SETTINGS", "Email Settings", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(34), "WORKORDER_CUSTOMIZATION", "Workorder Customization", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(35), "EVENT_FILTERING", "Event Filtering", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(36), "ALARM_FIELDS", "Alram Fields", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(37), "READINGS", "Readings", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(38), "SPACE_ASSET_CUSTOMIZATION", "Space Asset Customization", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(39), "SPACE_CATEGORIES", "Space Categories", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(40), "OPERATING_HOURS", "Operating Hours", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(41), "WORKFLOWS", "Workflows", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(42), "NOTIFICATIONS", "Notifications", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(43), "STATEFLOWS", "Stateflows", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(44), "SLA_POLICIES", "SLA Policies", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(45), "SCHEDULER", "Scheduler", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(46), "MODULES", "Modules", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(47), "CONNECTED_APPS", "Connected Apps", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(48), "CONNECTORS", "Connectors", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(49), "FUNCTIONS", "Functions", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(50), "ENERGY_METERS", "Energy Meters", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(51), "BASELINE", "Baseline", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(52), "AGENTS", "Agents", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(53), "CONTROLLERS", "Controllers", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(54), "LOGS", "Logs", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(55), "CONFIGURATION", "Configuration", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(56), "COMMISSIONING", "Commissioning", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(57), "FEEDBACK_AND_COMPLAINTS", "Feedback And Complaints", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(58), "SMART_CONTROLS", "Smart Controls", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(59), "ASSIGN", "Assign", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(60), "BOOKING", "Booking", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(61), "VIEW_ASSIGNMENT", "View Assignment", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(62), "VIEW_ASSIGNMENT_DEPARTMENT", "View Assignment Department", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(63), "ENABLE", "Enable", PermissionMapping.GROUP1PERMISSION),
            new Permission(getExponentValue(1), "VIEW_ASSIGNMENT_OWN", "View Assignment Own", PermissionMapping.GROUP2PERMISSION),
            new Permission(getExponentValue(2), "VIEW_BOOKING", "View Booking", PermissionMapping.GROUP2PERMISSION),
            new Permission(getExponentValue(3), "VIEW_BOOKING_DEPARTMENT", "View Booking Department", PermissionMapping.GROUP2PERMISSION),
            new Permission(getExponentValue(4), "VIEW_BOOKING_OWN", "View Booking Own", PermissionMapping.GROUP2PERMISSION),
            new Permission(getExponentValue(5), "EDIT", "Edit", PermissionMapping.GROUP2PERMISSION),
            new Permission(getExponentValue(6), "CREATE_EDIT", "Create/Edit", PermissionMapping.GROUP2PERMISSION));

    private static long getExponentValue(int exponent) {
        return (long) Math.pow(2, (exponent - 1));
    }

    private static final ImmutableMap<String, Permission> permissionMap = ImmutableMap.copyOf(getPermissionsMap());

    public static Map<String, ModuleAppPermission> getPermissionMapGroup() throws Exception {
        Map<String, ModuleAppPermission> modulePermissionGroupMap = new HashMap<>();

        List<Permission> permissionTypes = new ArrayList<>();

        //All Module Type
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("READ"), permissionMap.get("UPDATE"), permissionMap.get("DELETE"), permissionMap.get("IMPORT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(getMapKey(FacilioConstants.ContextNames.ALL_PERMISSIONS), new ModuleAppPermission(FacilioConstants.ContextNames.ALL_PERMISSIONS, null, permissionTypes));

        //Workorder
        Permission readPerm = FieldUtil.cloneBean(permissionMap.get("READ"), Permission.class);
        readPerm.setDisplayName("All");
        Permission readOwnPerm = FieldUtil.cloneBean(permissionMap.get("READ_OWN"), Permission.class);
        readOwnPerm.setDisplayName("Own");
        Permission readTeamPerm = FieldUtil.cloneBean(permissionMap.get("READ_TEAM"), Permission.class);
        readTeamPerm.setDisplayName("Team");
        Permission updatePerm = FieldUtil.cloneBean(permissionMap.get("UPDATE"), Permission.class);
        updatePerm.setDisplayName("All");
        Permission updateOwnPerm = FieldUtil.cloneBean(permissionMap.get("UPDATE_OWN"), Permission.class);
        updateOwnPerm.setDisplayName("Own");
        Permission updateTeamPerm = FieldUtil.cloneBean(permissionMap.get("UPDATE_TEAM"), Permission.class);
        updateTeamPerm.setDisplayName("Team");
        Permission deletePerm = FieldUtil.cloneBean(permissionMap.get("DELETE"), Permission.class);
        deletePerm.setDisplayName("All");
        Permission deleteOwnPerm = FieldUtil.cloneBean(permissionMap.get("DELETE_OWN"), Permission.class);
        deleteOwnPerm.setDisplayName("Own");
        Permission deleteTeamPerm = FieldUtil.cloneBean(permissionMap.get("DELETE_TEAM"), Permission.class);
        deleteTeamPerm.setDisplayName("Team");

        permissionTypes = Arrays.asList(
                permissionMap.get("CREATE"),
                permissionMap.get("IMPORT"),
                new PermissionGroup("Read", Arrays.asList(readPerm, readOwnPerm, readTeamPerm)),
                new PermissionGroup("Update", Arrays.asList(updatePerm, updateOwnPerm, updateTeamPerm)),
                permissionMap.get("UPDATE_CHANGE_OWNERSHIP"),
                permissionMap.get("UPDATE_CLOSE_WORKORDER"),
                permissionMap.get("UPDATE_TASK"),
                new PermissionGroup("Delete", Arrays.asList(deletePerm, deleteOwnPerm, deleteTeamPerm)),
                permissionMap.get("EXPORT")
        );
        modulePermissionGroupMap.put(getMapKey(FacilioConstants.ContextNames.WORK_ORDER), new ModuleAppPermission(FacilioConstants.ContextNames.WORK_ORDER, null, permissionTypes));

        permissionTypes = Arrays.asList(
                permissionMap.get("CREATE"),
                permissionMap.get("IMPORT"),
                readPerm,
                updatePerm,
                permissionMap.get("UPDATE_CHANGE_OWNERSHIP"),
                permissionMap.get("UPDATE_CLOSE_WORKORDER"),
                permissionMap.get("UPDATE_TASK"),
                deletePerm,
                permissionMap.get("EXPORT")
        );

        //Preventive Maintenance
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("READ"), permissionMap.get("UPDATE"), permissionMap.get("DELETE"), permissionMap.get("IMPORT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, new ModuleAppPermission(null, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, permissionTypes));

        //Approval
        permissionTypes = Arrays.asList(permissionMap.get("CAN_APPROVE"));
        modulePermissionGroupMap.put(WebTabContext.Type.APPROVAL.name(), new ModuleAppPermission(null, WebTabContext.Type.APPROVAL.name(), permissionTypes));

        //Calender
        permissionTypes = Arrays.asList(permissionMap.get("CALENDAR"), permissionMap.get("PLANNER"));
        modulePermissionGroupMap.put(WebTabContext.Type.CALENDAR.name(), new ModuleAppPermission(null, WebTabContext.Type.CALENDAR.name(), permissionTypes));

        //Report
        permissionTypes = Arrays.asList(permissionMap.get("VIEW"), permissionMap.get("CREATE_EDIT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(WebTabContext.Type.REPORT.name(), new ModuleAppPermission(null, WebTabContext.Type.REPORT.name(), permissionTypes));

        //Analytics
        permissionTypes = Arrays.asList(permissionMap.get("SAVE_AS_REPORT"), permissionMap.get("VIEW"));
        modulePermissionGroupMap.put(WebTabContext.Type.ANALYTICS.name(), new ModuleAppPermission(null, WebTabContext.Type.ANALYTICS.name(), permissionTypes));

        //KPI
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.KPI.name(), new ModuleAppPermission(null, WebTabContext.Type.KPI.name(), permissionTypes));

        //Dashboard
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"), permissionMap.get("SHARE"));
        modulePermissionGroupMap.put(WebTabContext.Type.DASHBOARD.name(), new ModuleAppPermission(null, WebTabContext.Type.DASHBOARD.name(), permissionTypes));

        //Custom
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.CUSTOM.name(), new ModuleAppPermission(null, WebTabContext.Type.CUSTOM.name(), permissionTypes));

        //Timeline
        permissionTypes = Arrays.asList(permissionMap.get("READ"), permissionMap.get("UPDATE"));
        modulePermissionGroupMap.put(WebTabContext.Type.TIMELINE.name(), new ModuleAppPermission(null, WebTabContext.Type.TIMELINE.name(), permissionTypes));

        //Apps
        permissionTypes = Arrays.asList(permissionMap.get("VIEW"), permissionMap.get("UPDATE"));
        modulePermissionGroupMap.put(WebTabContext.Type.APPS.name(), new ModuleAppPermission(null, WebTabContext.Type.APPS.name(), permissionTypes));

        //Setup
        for (WebTabContext.Type type : WebTabContext.Type.values()) {
            if (type.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
                permissionTypes = Arrays.asList(permissionMap.get("ENABLE"));
                modulePermissionGroupMap.put(type.name(), new ModuleAppPermission(null, type.name(), permissionTypes));
            }
        }

        //Portal Overview
        permissionTypes = Arrays.asList(permissionMap.get("READ"));
        modulePermissionGroupMap.put(WebTabContext.Type.PORTAL_OVERVIEW.name(), new ModuleAppPermission(null, WebTabContext.Type.PORTAL_OVERVIEW.name(), permissionTypes));

        //Notification
        permissionTypes = Arrays.asList(permissionMap.get("READ"));
        modulePermissionGroupMap.put(WebTabContext.Type.NOTIFICATION.name(), new ModuleAppPermission(null, WebTabContext.Type.NOTIFICATION.name(), permissionTypes));

        //Homepage
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.HOMEPAGE.name(), new ModuleAppPermission(null, WebTabContext.Type.HOMEPAGE.name(), permissionTypes));

        //Service catalog
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.SERVICE_CATALOG.name(), new ModuleAppPermission(null, WebTabContext.Type.SERVICE_CATALOG.name(), permissionTypes));

        //Indoor floorplan
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("EDIT"), permissionMap.get("VIEW"), permissionMap.get("ASSIGN"), permissionMap.get("BOOKING"), permissionMap.get("VIEW_ASSIGNMENT"), permissionMap.get("VIEW_ASSIGNMENT_OWN"), permissionMap.get("VIEW_ASSIGNMENT_DEPARTMENT"), permissionMap.get("VIEW_BOOKING"), permissionMap.get("VIEW_BOOKING_DEPARTMENT"), permissionMap.get("VIEW_BOOKING_OWN"));
        modulePermissionGroupMap.put("indoorfloorplan", new ModuleAppPermission(null, WebTabContext.Type.INDOOR_FLOORPLAN.name(), permissionTypes));

        return modulePermissionGroupMap;
    }

    private static String getMapKey(String tabOrModName){
        return tabOrModName;
    }
    private static String getMapKey(String tabOrModName , String appLinkName){
        StringBuilder builder = new StringBuilder();
        return builder.append(tabOrModName).append("_").append(appLinkName).toString();
    }

    public static Map < String, Permission > getPermissionsMap() {
        Map < String, Permission > permissionMap = new HashMap < > ();
        for (Permission permission: permissions) {
            permissionMap.put(permission.getActionName(), permission);
        }
        return permissionMap;
    }

    public static String getPermissionActionName(long value,PermissionMapping permissionMapping) {
        for (Permission permission: permissions) {
            if(permission.getValue() == value && permissionMapping.getGroupId() == permission.getPermissionMapping().getGroupId()){
                return permission.getActionName();
            }
        }
        return null;
    }

    public static void addModuleAppPermission(WebTabContext webTab) throws Exception {
        Map<String,ModuleAppPermission> moduleAppPermissionMap = getPermissionMapGroup();
        ApplicationContext app = ApplicationApi.getApplicationForId(webTab.getApplicationId());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ModuleAppPermission moduleAppPermission = new ModuleAppPermission();
        String tabOrModName = Strings.EMPTY;
        Long moduleId = null;
        String specialLinkName = null;
        if (webTab.getTypeEnum().getIndex() == WebTabContext.Type.MODULE.getIndex()) {
            List < String > moduleNames = ApplicationApi.getModulesForTab(webTab.getId());
            if (CollectionUtils.isNotEmpty(webTab.getSpecialTypeModules())) {
                moduleNames.addAll(webTab.getSpecialTypeModules());
            }
            if (CollectionUtils.isNotEmpty(moduleNames)) {
                tabOrModName = moduleNames.get(0);
            }
            FacilioModule module = modBean.getModule(tabOrModName);
            if(module != null && module.getModuleId() > 0){
                moduleId = module.getModuleId();
            } else {
                specialLinkName = tabOrModName;
            }
        } else {
            tabOrModName = webTab.getTypeEnum().name();
            specialLinkName = tabOrModName;
        }
        if(moduleAppPermissionMap.containsKey(getMapKey(tabOrModName,app.getLinkName()))){
            moduleAppPermission = moduleAppPermissionMap.get(getMapKey(tabOrModName,app.getLinkName()));
        } else if(moduleAppPermissionMap.containsKey(getMapKey(tabOrModName))){
            moduleAppPermission = moduleAppPermissionMap.get(getMapKey(tabOrModName));
        } else if(moduleAppPermissionMap.containsKey(getMapKey(webTab.getTypeEnum().name()))){
            moduleAppPermission = moduleAppPermissionMap.get(getMapKey(webTab.getTypeEnum().name()));
        } else {
            moduleAppPermission = moduleAppPermissionMap.get(getMapKey(FacilioConstants.ContextNames.ALL_PERMISSIONS));
        }
        moduleAppPermission.setApplicationId(app.getId());
        moduleAppPermission.setModuleId(moduleId);
        moduleAppPermission.setSpecialLinkName(specialLinkName);
        List<Permission> permissionList = moduleAppPermission.getPermissionList();
        if(CollectionUtils.isNotEmpty(permissionList)) {
            for (Permission permission: permissionList) {
                ModuleAppPermission moduleAppPermissionForPermission = getModuleAppPermissionForPermission(moduleAppPermission,permission);
                if (permission instanceof PermissionGroup) {
                    Long parentId = addParentModuleAppPermission(moduleAppPermissionForPermission);
                    List < Permission > childPermissions = ((PermissionGroup) permission).getPermissions();
                    if (CollectionUtils.isNotEmpty(childPermissions)) {
                        for(Permission childPermission : childPermissions){
                            ModuleAppPermissionChild moduleAppPermissionChild = getModuleAppPermissionChildForPermission(childPermission,parentId);
                            addModuleAppPermissionChild(moduleAppPermissionChild);
                        }
                    }
                }
                else if(permission instanceof Permission){
                    addParentModuleAppPermission(moduleAppPermissionForPermission);
                }
            }
        }
    }
    private static ModuleAppPermissionChild getModuleAppPermissionChildForPermission(Permission permission, Long parentId) {
        ModuleAppPermissionChild moduleAppPermissionChild = new ModuleAppPermissionChild();
        if(permission != null) {
            if(permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()){
                moduleAppPermissionChild.setChildPermission1(permission.getValue());
            } else if(permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()){
                moduleAppPermissionChild.setChildPermission2(permission.getValue());
            }
            moduleAppPermissionChild.setDisplayName(permission.getDisplayName());
            moduleAppPermissionChild.setParentId(parentId);
            return moduleAppPermissionChild;
        }
        return null;
    }

    private static ModuleAppPermission getModuleAppPermissionForPermission(ModuleAppPermission parentPermission , Permission permission) throws Exception {
        ModuleAppPermission moduleAppPermission = new ModuleAppPermission();
        if(permission != null) {
            if(permission.getPermissionMapping() != null) {
                if (permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()) {
                    moduleAppPermission.setPermission1(permission.getValue());
                } else if (permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()) {
                    moduleAppPermission.setPermission2(permission.getValue());
                }
            }
            moduleAppPermission.setDisplayName(permission.getDisplayName());
            moduleAppPermission.setModuleId(parentPermission.getModuleId());
            moduleAppPermission.setModuleName(parentPermission.getModuleName());
            moduleAppPermission.setSpecialLinkName(parentPermission.getSpecialLinkName());
            moduleAppPermission.setApplicationId(parentPermission.getApplicationId());
            return moduleAppPermission;
        }
        return null;
    }

    public static List<ModuleAppPermission> addOrUpdateModuleAppPermissions(List<ModuleAppPermission> moduleAppPermissionList) throws Exception {
        if(CollectionUtils.isNotEmpty(moduleAppPermissionList)) {
            for (ModuleAppPermission moduleAppPermission : moduleAppPermissionList) {
                Long parentId = null;
                if(moduleAppPermission.getId() > 0){
                    parentId = updateParentModuleAppPermission(moduleAppPermission);
                } else{
                    parentId = addParentModuleAppPermission(moduleAppPermission);
                }
                List<ModuleAppPermissionChild> moduleAppPermissionChildren = moduleAppPermission.getModuleAppPermissionChildren();
                if(CollectionUtils.isNotEmpty(moduleAppPermissionChildren)){
                    for(ModuleAppPermissionChild modulePermisssionChild : moduleAppPermissionChildren){
                        modulePermisssionChild.setParentId(parentId);
                        if(modulePermisssionChild.getId() > 0){
                            updateModuleAppPermissionChild(modulePermisssionChild);
                        } else {
                            addModuleAppPermissionChild(modulePermisssionChild);
                        }
                    }
                }
            }
        }
        return null;
    }


    public static List<ModuleAppPermission> listAppPermissions(Long appId,Long moduleId,String specialLinkName) throws Exception {

        List<ModuleAppPermission> moduleAppPermissionList = getModuleAppPermission( appId, moduleId, specialLinkName);
        if(CollectionUtils.isNotEmpty(moduleAppPermissionList)) {
            for (ModuleAppPermission moduleAppPermission : moduleAppPermissionList) {
                List<ModuleAppPermissionChild> moduleAppPermissionChildren = getModuleAppPermissionChildren(moduleAppPermission.getId());
                if(CollectionUtils.isNotEmpty(moduleAppPermissionChildren)){
                    moduleAppPermission.setModuleAppPermissionChildren(moduleAppPermissionChildren);
                }
            }
            return moduleAppPermissionList;
        }
        return new ArrayList<>();
    }

    public static void deleteAppPermissions(List<Long> ids) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            GenericDeleteRecordBuilder deleteParentRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getModuleAppPermissionModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getModuleAppPermissionModule()));
            deleteParentRecordBuilder.delete();
        }
    }
    public static void deleteAppChildPermissions(List<Long> ids) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            GenericDeleteRecordBuilder deleteChildRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getModuleAppPermissionChildModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getModuleAppPermissionChildModule()));
            deleteChildRecordBuilder.delete();
        }
    }

    private static ModuleAppPermission getModuleAppPermissionForModulePermission(ModuleAppPermission modulePermission, long applicationId) throws Exception {
        if(modulePermission != null) {
            ModuleAppPermission moduleAppPermission = new ModuleAppPermission();
            moduleAppPermission.setPermission1(modulePermission.getPermission1());
            moduleAppPermission.setPermission2(modulePermission.getPermission2());
            moduleAppPermission.setModuleId(modulePermission.getModuleId());
            moduleAppPermission.setDisplayName(modulePermission.getDisplayName());
            moduleAppPermission.setSpecialLinkName(modulePermission.getSpecialLinkName());
            moduleAppPermission.setApplicationId(applicationId);
            moduleAppPermission.setModulePermissionParentId(modulePermission.getId());
            return moduleAppPermission;
        }
        return null;
    }

    private static Long addParentModuleAppPermission(ModuleAppPermission moduleAppPermission) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(moduleAppPermission);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getModuleAppPermissionModule().getTableName())
                .fields(FieldFactory.moduleAppPermissionFields())
                .addRecord(prop);
        insertBuilder.save();
        if(prop != null){
            if(prop.containsKey("id")){
                return (Long) prop.get("id");
            }
        }
        return null;
    }

    public static void addModuleAppPermissionChild(ModuleAppPermissionChild moduleAppPermissionChild) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(moduleAppPermissionChild);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getModuleAppPermissionChildModule().getTableName())
                .fields(FieldFactory.moduleAppPermissionChildFields())
                .addRecord(prop);
        insertBuilder.save();
    }

    private static Long updateParentModuleAppPermission(ModuleAppPermission moduleAppPermission) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(moduleAppPermission);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getModuleAppPermissionModule().getTableName())
                .fields(FieldFactory.moduleAppPermissionFields())
                .andCondition(CriteriaAPI.getIdCondition(moduleAppPermission.getId(),ModuleFactory.getModuleAppPermissionModule()));
        updateRecordBuilder.update(prop);
        if(prop != null){
            if(prop.containsKey("id")){
                return (Long) prop.get("id");
            }
        }
        return null;
    }

    public static void updateModuleAppPermissionChild(ModuleAppPermissionChild moduleAppPermissionChild) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(moduleAppPermissionChild);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getModuleAppPermissionChildModule().getTableName())
                .fields(FieldFactory.moduleAppPermissionChildFields())
                .andCondition(CriteriaAPI.getIdCondition(moduleAppPermissionChild.getId(),ModuleFactory.getModuleAppPermissionChildModule()));
        updateRecordBuilder.update(prop);
    }

    public static List < Permission > getPermissionValue(WebTabContext webtab) throws Exception {
        return getPermissionValue(webtab, null);
    }

    public static List < Permission > getPermissionValue(WebTabContext webtab, String moduleName) throws Exception {

        GenericSelectRecordBuilder builder = getCriteriaForWebTabPermissions(webtab, moduleName);

        List < Map < String, Object >> props = builder.get();
        List < Permission > permissions = new ArrayList <>();
        if (CollectionUtils.isNotEmpty(props)) {
            List < ModuleAppPermission > moduleAppPermissions = FieldUtil.getAsBeanListFromMapList(props, ModuleAppPermission.class);
            for (ModuleAppPermission moduleAppPermission: moduleAppPermissions) {
                if(moduleAppPermission.getPermission1() > 0 || moduleAppPermission.getPermission2() > 0){
                    Permission permission = getPermissionForModuleAppPermission(moduleAppPermission);
                    permissions.add(permission);
                }
                else {
                    PermissionGroup permissionGroup = new PermissionGroup();
                    permissionGroup.setDisplayName(moduleAppPermission.getDisplayName());
                    List<ModuleAppPermissionChild> childModuleAppPermissions = getModuleAppPermissionChildren(moduleAppPermission.getId());
                    List<Permission> childPermissions = new ArrayList<>();
                    if(CollectionUtils.isNotEmpty(childModuleAppPermissions)){
                        for(ModuleAppPermissionChild childModuleAppPermission : childModuleAppPermissions){
                            Permission permission = getPermissionForModuleAppPermissionChild(childModuleAppPermission);
                            childPermissions.add(permission);
                        }
                    }
                    permissionGroup.setPermissions(childPermissions);
                    permissions.add(permissionGroup);
                }
            }
            return permissions;
        }
        return null;
    }


    private static Permission getPermissionForModuleAppPermission(ModuleAppPermission moduleAppPermission) {
        if(moduleAppPermission != null) {
            Permission permission = new Permission();
            if (moduleAppPermission.getPermission1() > 0) {
                permission.setPermissionMapping(PermissionMapping.GROUP1PERMISSION);
                permission.setValue(moduleAppPermission.getPermission1());
            } else if (moduleAppPermission.getPermission2() > 0) {
                permission.setPermissionMapping(PermissionMapping.GROUP2PERMISSION);
                permission.setValue(moduleAppPermission.getPermission2());
            }
            permission.setDisplayName(moduleAppPermission.getDisplayName());
            permission.setActionName(getPermissionActionName(permission.getValue(), permission.getPermissionMapping()));
            return permission;
        }
        return null;
    }

    private static Permission getPermissionForModuleAppPermissionChild(ModuleAppPermissionChild moduleAppPermissionChild) {
        if(moduleAppPermissionChild != null) {
            Permission permission = new Permission();
            if (moduleAppPermissionChild.getChildPermission1() > 0) {
                permission.setPermissionMapping(PermissionMapping.GROUP1PERMISSION);
                permission.setValue(moduleAppPermissionChild.getChildPermission1());
            } else if (moduleAppPermissionChild.getChildPermission2() > 0) {
                permission.setPermissionMapping(PermissionMapping.GROUP2PERMISSION);
                permission.setValue(moduleAppPermissionChild.getChildPermission2());
            }
            permission.setDisplayName(moduleAppPermissionChild.getDisplayName());
            permission.setActionName(getPermissionActionName(permission.getValue(), permission.getPermissionMapping()));
            return permission;
        }
        return null;
    }

    private static GenericSelectRecordBuilder getCriteriaForWebTabPermissions(WebTabContext webtab, String moduleName) throws Exception {
        long appId = webtab.getApplicationId();
        Map < String, FacilioField > fieldsMap = FieldFactory.getAsMap(FieldFactory.moduleAppPermissionFields());
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(ModuleFactory.getModuleAppPermissionModule().getTableName()).select(FieldFactory.moduleAppPermissionFields()).andCondition(CriteriaAPI.getCondition(fieldsMap.get("applicationId"), Collections.singletonList(appId), NumberOperators.EQUALS));

        if (webtab.getTypeEnum().getIndex() == WebTabContext.Type.MODULE.getIndex()) {
            List < String > modulesNames = ApplicationApi.getModulesForTab(webtab.getId());
            if (CollectionUtils.isEmpty(modulesNames)) {
                modulesNames = new ArrayList < > ();
            }
            List < Long > modIds = new ArrayList < > ();
            if (moduleName != null && !moduleName.equals(FacilioConstants.ContextNames.ALL_PERMISSIONS)) {
                modulesNames.add(moduleName);
            }
            if (CollectionUtils.isNotEmpty(modulesNames)) {
                for (String modName: modulesNames) {
                    FacilioModule module = modBean.getModule(modName);
                    if (module != null && module.getModuleId() > 0) {
                        modIds.add(module.getModuleId());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(modIds)) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), StringUtils.join(modIds, ","), NumberOperators.EQUALS));
            } else if (moduleName != null && !moduleName.equals(FacilioConstants.ContextNames.ALL_PERMISSIONS)) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("specialLinkName"), moduleName, StringOperators.IS));
            }
            if (CollectionUtils.isNotEmpty(webtab.getSpecialTypeModules())) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("specialLinkName"), StringUtils.join(webtab.getSpecialTypeModules(), ","), StringOperators.IS));
            }
        } else {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("specialLinkName"), StringUtils.join(webtab.getTypeEnum().name(), ","), StringOperators.IS));
        }
        return builder;
    }

    public static List < ModuleAppPermissionChild > getModuleAppPermissionChildren(long id) throws Exception {
        Map < String, FacilioField > fieldsMap = FieldFactory.getAsMap(FieldFactory.moduleAppPermissionChildFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(ModuleFactory.getModuleAppPermissionChildModule().getTableName()).select(FieldFactory.moduleAppPermissionChildFields()).andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), Collections.singletonList(id), NumberOperators.EQUALS));
        List < Map < String, Object >> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List < ModuleAppPermissionChild > moduleAppPermissionChildren = FieldUtil.getAsBeanListFromMapList(props, ModuleAppPermissionChild.class);
            return moduleAppPermissionChildren;
        }
        return null;
    }

    public static List < ModuleAppPermission > getModuleAppPermission(Long appId,Long moduleId,String specialLinkName) throws Exception {
        if(appId == null){
            throw new IllegalArgumentException("App cannot be null");
        }
        Map < String, FacilioField > fieldsMap = FieldFactory.getAsMap(FieldFactory.moduleAppPermissionFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModuleAppPermissionModule().getTableName())
                .select(FieldFactory.moduleAppPermissionFields())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("applicationId"), Collections.singletonList(appId), NumberOperators.EQUALS));

        if(moduleId != null){
            builder
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), Collections.singletonList(moduleId), NumberOperators.EQUALS));
        }
        if(specialLinkName != null){
            builder
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("specialLinkName"), specialLinkName, StringOperators.IS));
        }

        List < Map < String, Object >> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List < ModuleAppPermission > moduleAppPermission = FieldUtil.getAsBeanListFromMapList(props, ModuleAppPermission.class);
            return moduleAppPermission;
        }
        return null;
    }

    public static PermissionGroup getPermissionGroup(WebTabContext webTab, String moduleName, String actionName) throws Exception {
        List < Permission > permissions = getPermissionValue(webTab, moduleName);
        if (permissions != null) {
            for (Permission p: permissions) {
                if (p instanceof PermissionGroup) {
                    if (((PermissionGroup) p).getDisplayName().equalsIgnoreCase(actionName)) {
                        return (PermissionGroup) p;
                    }
                }
            }
        }
        return null;
    }

}