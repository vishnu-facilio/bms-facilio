package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.Permission;
import com.facilio.bmsconsole.context.PermissionGroup;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsoleV3.ModuleAppPermissionChild;
import com.facilio.bmsconsoleV3.context.ModuleAppPermission;
import com.facilio.bmsconsoleV3.context.ModulePermission;
import com.facilio.bmsconsoleV3.context.ModulePermisssionChild;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ModulePermissionUtil {

    private static final ImmutableMap< String, Permission > permissionMap = ImmutableMap.copyOf(AppModulePermissionUtil.getPermissionsMap());

    private Map<String,Permission> getPermissionMap(){
        return AppModulePermissionUtil.getPermissionsMap();
    }

    public static void addInitialModulePermission() throws Exception {
        addPermissionForModule(null);
    }

    public static void addPermissionForModule(FacilioModule module) throws Exception {
        Map<String,ModulePermission> modulePermissions = generatePermissionMapGroup();
        if(module != null){
            ModulePermission modulePermission;
            if(modulePermissions.containsKey(module.getName())){
                modulePermission = modulePermissions.get(module.getName());
            }else{
                modulePermission = modulePermissions.get(FacilioConstants.ContextNames.ALL_PERMISSIONS);
            }
            modulePermission.setModuleId(module.getModuleId());
            modulePermissions = new HashMap<>();
            modulePermissions.put(module.getName(),modulePermission);
        }
        for(String key : modulePermissions.keySet()){
            ModulePermission modulePermission = modulePermissions.get(key);
            if(modulePermission.getSpecialLinkName() != null || modulePermission.getModuleId() != null){
                List < Permission > permissionList = modulePermission.getPermissionList();
                if (CollectionUtils.isNotEmpty(permissionList)) {
                    for (Permission permission: permissionList) {
                        ModulePermission modulePermissionForPermission = getModulePermissionForPermission(modulePermission,permission);
                        if (permission instanceof PermissionGroup) {
                            Long parentId = addModulePermission(modulePermissionForPermission);
                            List < Permission > childPermissions = ((PermissionGroup) permission).getPermissions();
                            if (CollectionUtils.isNotEmpty(childPermissions)) {
                                for(Permission childPermission : childPermissions){
                                    ModulePermisssionChild modulePermissionChild = getModulePermissionChildForPermission(childPermission,parentId);
                                    addModulePermissionChild(modulePermissionChild);
                                }
                            }
                        }
                        else if(permission instanceof Permission){
                            addModulePermission(modulePermissionForPermission);
                        }
                    }
                }
            }
        }
    }



    private static ModulePermisssionChild getModulePermissionChildForPermission(Permission permission, Long parentId) {
        ModulePermisssionChild modulePermissionChild = new ModulePermisssionChild();
        if(permission != null) {
            if(permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()){
                modulePermissionChild.setChildPermission1(permission.getValue());
            } else if(permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()){
                modulePermissionChild.setChildPermission2(permission.getValue());
            }
            modulePermissionChild.setDisplayName(permission.getDisplayName());
            modulePermissionChild.setParentId(parentId);
            return modulePermissionChild;
        }
        return null;
    }

    private static ModulePermission getModulePermissionForPermission(ModulePermission parentPermission , Permission permission) throws Exception {
        ModulePermission modulePermission = new ModulePermission();
        if(permission != null) {
            if(permission.getPermissionMapping() != null) {
                if (permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP1PERMISSION.getIndex()) {
                    modulePermission.setPermission1(permission.getValue());
                } else if (permission.getPermissionMapping().getIndex() == AppModulePermissionUtil.PermissionMapping.GROUP2PERMISSION.getIndex()) {
                    modulePermission.setPermission2(permission.getValue());
                }
            }
            modulePermission.setDisplayName(permission.getDisplayName());
            modulePermission.setModuleId(parentPermission.getModuleId());
            modulePermission.setModuleName(parentPermission.getModuleName());
            modulePermission.setSpecialLinkName(parentPermission.getSpecialLinkName());
            return modulePermission;
        }
        return null;
    }

    private static Long addModulePermission (ModulePermission modulePermission) throws Exception{
        Map < String, Object > prop = FieldUtil.getAsProperties(modulePermission);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getModulePermissionModule().getTableName())
                .fields(FieldFactory.modulePermissionFields())
                .addRecord(prop);
        insertBuilder.save();
        if(prop != null){
            if(prop.containsKey("id")){
                return (Long) prop.get("id");
            }
        }
        return null;
    }

    public static void addModulePermissionChild(ModulePermisssionChild modulePermissionChild) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(modulePermissionChild);
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getModulePermissionChildModule().getTableName())
                .fields(FieldFactory.modulePermissionChildFields())
                .addRecord(prop);
        insertBuilder.save();
    }

    public static List<ModulePermission> getModulePermissions(Long moduleId) throws Exception{
        return getModulePermissions(moduleId,null);
    }
    public static List<ModulePermission> getModulePermissions(String specialLinkName) throws Exception{
        return getModulePermissions(null,specialLinkName);
    }
    public static List<ModulePermission> getModulePermissions(Long moduleId,String specialLinkName) throws Exception{
        Map < String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.modulePermissionFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModulePermissionModule().getTableName())
                .select(FieldFactory.modulePermissionFields());
        if(moduleId != null){
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), Collections.singletonList(moduleId), NumberOperators.EQUALS));
        }
        if(specialLinkName != null){
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("specialLinkName"), specialLinkName, StringOperators.IS));
        }
        List < Map < String, Object >> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ModulePermission> modulePermissions = FieldUtil.getAsBeanListFromMapList(props, ModulePermission.class);
            return modulePermissions;
        }
        return null;
    }

    public static List<ModulePermisssionChild> getModulePermissionsChild(long parentId) throws Exception{
        Map < String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.modulePermissionChildFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModulePermissionChildModule().getTableName())
                .select(FieldFactory.modulePermissionChildFields());
        builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("parentId"), Collections.singletonList(parentId), NumberOperators.EQUALS));

        List < Map < String, Object >> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ModulePermisssionChild> modulePermissionsChild = FieldUtil.getAsBeanListFromMapList(props, ModulePermisssionChild.class);
            return modulePermissionsChild;
        }
        return null;
    }

    public static Map< String, ModulePermission> generatePermissionMapGroup() throws Exception {
        Map < String, ModulePermission> modulePermissionGroupMap = new HashMap < > ();

        List < Permission > permissionTypes = new ArrayList < > ();

        //All Module Type
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("READ"), permissionMap.get("UPDATE"), permissionMap.get("DELETE"), permissionMap.get("IMPORT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(FacilioConstants.ContextNames.ALL_PERMISSIONS, new ModulePermission(FacilioConstants.ContextNames.ALL_PERMISSIONS, null,permissionTypes));

        //Workorder
        Permission readPerm = FieldUtil.cloneBean(permissionMap.get("READ"),Permission.class);
        readPerm.setDisplayName("All");
        Permission readOwnPerm = FieldUtil.cloneBean(permissionMap.get("READ_OWN"),Permission.class);
        readOwnPerm.setDisplayName("Own");
        Permission readTeamPerm = FieldUtil.cloneBean(permissionMap.get("READ_TEAM"),Permission.class);
        readTeamPerm.setDisplayName("Team");
        Permission updatePerm = FieldUtil.cloneBean(permissionMap.get("UPDATE"),Permission.class);
        updatePerm.setDisplayName("All");
        Permission updateOwnPerm = FieldUtil.cloneBean(permissionMap.get("UPDATE_OWN"),Permission.class);
        updateOwnPerm.setDisplayName("Own");
        Permission updateTeamPerm = FieldUtil.cloneBean(permissionMap.get("UPDATE_TEAM"),Permission.class);
        updateTeamPerm.setDisplayName("Team");
        Permission deletePerm = FieldUtil.cloneBean(permissionMap.get("DELETE"),Permission.class);
        deletePerm.setDisplayName("All");
        Permission deleteOwnPerm =FieldUtil.cloneBean( permissionMap.get("DELETE_OWN"),Permission.class);
        deleteOwnPerm.setDisplayName("Own");
        Permission deleteTeamPerm = FieldUtil.cloneBean(permissionMap.get("DELETE_TEAM"),Permission.class);
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
        modulePermissionGroupMap.put(FacilioConstants.ContextNames.WORK_ORDER, new ModulePermission(FacilioConstants.ContextNames.WORK_ORDER, null,permissionTypes));

        //Preventive Maintenance
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("READ"), permissionMap.get("UPDATE"), permissionMap.get("DELETE"), permissionMap.get("IMPORT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, new ModulePermission(null, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE,permissionTypes));

        //Approval
        permissionTypes = Arrays.asList(permissionMap.get("CAN_APPROVE"));
        modulePermissionGroupMap.put(WebTabContext.Type.APPROVAL.name(), new ModulePermission(null, WebTabContext.Type.APPROVAL.name(), permissionTypes));

        //Calender
        permissionTypes = Arrays.asList(permissionMap.get("CALENDAR"), permissionMap.get("PLANNER"));
        modulePermissionGroupMap.put(WebTabContext.Type.CALENDAR.name(), new ModulePermission(null, WebTabContext.Type.CALENDAR.name(), permissionTypes));

        //Report
        permissionTypes = Arrays.asList(permissionMap.get("VIEW"), permissionMap.get("CREATE_EDIT"), permissionMap.get("EXPORT"));
        modulePermissionGroupMap.put(WebTabContext.Type.REPORT.name(), new ModulePermission(null, WebTabContext.Type.REPORT.name(), permissionTypes));

        //Analytics
        permissionTypes = Arrays.asList(permissionMap.get("SAVE_AS_REPORT"), permissionMap.get("VIEW"));
        modulePermissionGroupMap.put(WebTabContext.Type.ANALYTICS.name(), new ModulePermission(null, WebTabContext.Type.ANALYTICS.name(), permissionTypes));

        //KPI
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.KPI.name(), new ModulePermission(null, WebTabContext.Type.KPI.name(),permissionTypes));

        //Dashboard
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"), permissionMap.get("SHARE"));
        modulePermissionGroupMap.put(WebTabContext.Type.DASHBOARD.name(), new ModulePermission(null, WebTabContext.Type.DASHBOARD.name(),permissionTypes));

        //Custom
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.CUSTOM.name(), new ModulePermission(null, WebTabContext.Type.CUSTOM.name(),permissionTypes));

        //Timeline
        permissionTypes = Arrays.asList(permissionMap.get("READ"), permissionMap.get("UPDATE"));
        modulePermissionGroupMap.put(WebTabContext.Type.TIMELINE.name(), new ModulePermission(null, WebTabContext.Type.TIMELINE.name(),permissionTypes));

        //Apps
        permissionTypes = Arrays.asList(permissionMap.get("VIEW"), permissionMap.get("UPDATE"));
        modulePermissionGroupMap.put(WebTabContext.Type.APPS.name(), new ModulePermission(null, WebTabContext.Type.APPS.name(),permissionTypes));

        //Setup
        for (WebTabContext.Type type: WebTabContext.Type.values()) {
            if (type.getTabType().getIndex() == WebTabContext.TabType.SETUP.getIndex()) {
                permissionTypes = Arrays.asList(permissionMap.get("ENABLE"));
                modulePermissionGroupMap.put(type.name(), new ModulePermission(null, type.name(),permissionTypes));
            }
        }

        //Portal Overview
        permissionTypes = Arrays.asList(permissionMap.get("READ"));
        modulePermissionGroupMap.put(WebTabContext.Type.PORTAL_OVERVIEW.name(), new ModulePermission(null, WebTabContext.Type.PORTAL_OVERVIEW.name(),permissionTypes));

        //Notification
        permissionTypes = Arrays.asList(permissionMap.get("READ"));
        modulePermissionGroupMap.put(WebTabContext.Type.NOTIFICATION.name(), new ModulePermission(null, WebTabContext.Type.NOTIFICATION.name(),permissionTypes));

        //Homepage
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.HOMEPAGE.name(), new ModulePermission(null, WebTabContext.Type.HOMEPAGE.name(),permissionTypes));

        //Service catalog
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("UPDATE"), permissionMap.get("READ"), permissionMap.get("DELETE"));
        modulePermissionGroupMap.put(WebTabContext.Type.SERVICE_CATALOG.name(), new ModulePermission(null, WebTabContext.Type.SERVICE_CATALOG.name(),permissionTypes));

        //Indoor floorplan
        permissionTypes = Arrays.asList(permissionMap.get("CREATE"), permissionMap.get("EDIT"), permissionMap.get("VIEW"), permissionMap.get("ASSIGN"), permissionMap.get("BOOKING"), permissionMap.get("VIEW_ASSIGNMENT"), permissionMap.get("VIEW_ASSIGNMENT_OWN"), permissionMap.get("VIEW_ASSIGNMENT_DEPARTMENT"), permissionMap.get("VIEW_BOOKING"), permissionMap.get("VIEW_BOOKING_DEPARTMENT"), permissionMap.get("VIEW_BOOKING_OWN"));
        modulePermissionGroupMap.put("indoorfloorplan", new ModuleAppPermission(null, WebTabContext.Type.INDOOR_FLOORPLAN.name(), null, permissionTypes));

        return modulePermissionGroupMap;
    }


    public static List<ModulePermission> addOrUpdateModulePermissions(List<ModulePermission> modulePermissionList) throws Exception {
        if(CollectionUtils.isNotEmpty(modulePermissionList)) {
            for (ModulePermission modulePermission : modulePermissionList) {
                Long parentId = null;
                if(modulePermission.getId() > 0){
                    parentId = updateParentModulePermission(modulePermission);
                } else{
                    parentId = addModulePermission(modulePermission);
                }
                List<ModulePermisssionChild> modulePermissionChildren = modulePermission.getModulePermisssionChildList();
                if(CollectionUtils.isNotEmpty(modulePermissionChildren)){
                    for(ModulePermisssionChild modulePermissionChild : modulePermissionChildren){
                        modulePermissionChild.setParentId(parentId);
                        if(modulePermissionChild.getId() > 0){
                            updateModulePermissionChild(modulePermissionChild);
                        } else {
                            addModulePermissionChild(modulePermissionChild);
                        }
                    }
                }
            }
        }
        return null;
    }


    public static List<ModulePermission> listModulePermissions(Long moduleId,String specialLinkName) throws Exception {

        List<ModulePermission> modulePermissionList = getModulePermission(moduleId, specialLinkName);
        if(CollectionUtils.isNotEmpty(modulePermissionList)) {
            for (ModulePermission modulePermission : modulePermissionList) {
                List<ModulePermisssionChild> modulePermissionChildren = getModulePermissionsChild(modulePermission.getId());
                if(CollectionUtils.isNotEmpty(modulePermissionChildren)){
                    modulePermission.setModulePermisssionChildList(modulePermissionChildren);
                }
            }
            return modulePermissionList;
        }
        return new ArrayList<>();
    }

    public static void deleteModulePermissions(List<Long> ids) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            GenericDeleteRecordBuilder deleteParentRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getModulePermissionModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getModulePermissionModule()));
            deleteParentRecordBuilder.delete();
        }
    }
    public static void deleteModuleChildPermissions(List<Long> ids) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            GenericDeleteRecordBuilder deleteChildRecordBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getModulePermissionChildModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids,ModuleFactory.getModulePermissionChildModule()));
            deleteChildRecordBuilder.delete();
        }
    }

    public static List < ModulePermission > getModulePermission(Long moduleId,String specialLinkName) throws Exception {
        Map < String, FacilioField > fieldsMap = FieldFactory.getAsMap(FieldFactory.modulePermissionFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModulePermissionModule().getTableName())
                .select(FieldFactory.modulePermissionFields());

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
            List < ModulePermission > modulePermission = FieldUtil.getAsBeanListFromMapList(props, ModulePermission.class);
            return modulePermission;
        }
        return null;
    }

    private static Long updateParentModulePermission(ModulePermission modulePermission) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(modulePermission);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getModulePermissionModule().getTableName())
                .fields(FieldFactory.modulePermissionFields())
                .andCondition(CriteriaAPI.getIdCondition(modulePermission.getId(),ModuleFactory.getModulePermissionModule()));
        updateRecordBuilder.update(prop);
        if(prop != null){
            if(prop.containsKey("id")){
                return (Long) prop.get("id");
            }
        }
        return null;
    }

    public static void updateModulePermissionChild(ModulePermisssionChild modulePermissionChild) throws Exception {
        Map < String, Object > prop = FieldUtil.getAsProperties(modulePermissionChild);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getModulePermissionChildModule().getTableName())
                .fields(FieldFactory.modulePermissionChildFields())
                .andCondition(CriteriaAPI.getIdCondition(modulePermissionChild.getId(),ModuleFactory.getModulePermissionChildModule()));
        updateRecordBuilder.update(prop);
    }
}
