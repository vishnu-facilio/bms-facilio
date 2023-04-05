package com.facilio.permission.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.wmsv2.handler.AuditLogHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;

import static com.facilio.bmsconsole.util.AuditLogUtil.sendAuditLogs;

public class PermissionSetUtil {
    public static final String KEY_SEPARATOR = "#";

    public static List<FacilioField> getAllExtendModuleFields(FacilioModule module) {
        Map<String, FacilioModule> extendModuleMap = PermissionSetConstants.EXTENDED_MODULE_REL;
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule extendModule = module;
        while (extendModule != null) {
            fields.addAll(PermissionSetFieldFactory.MODULE_VS_FIELDS.get(extendModule.getName()));
            extendModule = extendModuleMap.get(extendModule.getName());
        }
        return fields;
    }

    public static boolean getPermissionValueForKey(Map<String, Object> prop, String key) {
        if (MapUtils.isNotEmpty(prop) && prop.containsKey(key)) {
            return Boolean.valueOf(String.valueOf(prop.get(key)));
        }
        return false;
    }

    public static String getPropUniqueKey(Map<String, Object> prop, List<String> queryConditionFields) {
        String key = PermissionSetUtil.KEY_SEPARATOR;
        for (String conditionField : queryConditionFields) {
            key = key + prop.get(conditionField) + PermissionSetUtil.KEY_SEPARATOR;
        }
        return key;
    }

    public static String getPropKey(Map<String, Long> prop, List<String> queryConditionFields) {
        String key = PermissionSetUtil.KEY_SEPARATOR;
        for (String conditionField : queryConditionFields) {
            key = key + prop.get(conditionField) + PermissionSetUtil.KEY_SEPARATOR;
        }
        return key;
    }

    public static void constructTableJoin(FacilioModule module, GenericSelectRecordBuilder selectRecordBuilder) {
        FacilioModule extendModule = module;
        while (extendModule != null && extendModule.getExtendModule() != null) {
            selectRecordBuilder
                    .innerJoin(extendModule.getExtendModule().getTableName())
                    .on(extendModule.getTableName() + ".ID = " + extendModule.getExtendModule().getTableName() + ".ID");
            extendModule = extendModule.getExtendModule();
        }
    }

    public static void constructTableJoin(FacilioModule module, GenericDeleteRecordBuilder deleteRecordBuilder) {
        FacilioModule extendModule = module;
        while (extendModule != null && extendModule.getExtendModule() != null) {
            deleteRecordBuilder
                    .innerJoin(extendModule.getExtendModule().getTableName(), true)
                    .on(extendModule.getTableName() + ".ID = " + extendModule.getExtendModule().getTableName() + ".ID");
            extendModule = extendModule.getExtendModule();
        }
    }

    public static boolean hasPermission (PermissionSetType.Type type, Map<String,Long> queryProp, PermissionFieldEnum permissionFieldEnum) throws Exception {
        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            return true;
        }
        if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().isSuperAdmin()) {
            return true;
        }
        List<Long> permissionSetIds = AccountUtil.getPermissionSets();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        if(CollectionUtils.isNotEmpty(permissionSetIds)) {
            for(Long permissionSetId : permissionSetIds) {
                List<Map<String, Object>> propsList = permissionSetBean.getPermissionValues(type, queryProp, permissionFieldEnum, permissionSetId);
                if (CollectionUtils.isNotEmpty(propsList)) {
                    for (Map<String, Object> prop : propsList) {
                        Boolean perm = (Boolean) prop.get("permission");
                        if (perm != null && perm) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public static void addAuditLogs(Long permissionSetId, String action) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        PermissionSetContext permissionSet = permissionSetBean.getPermissionSet(permissionSetId);
        if(permissionSet != null) {
            AuditLogHandler.ActionType actionType = null;
            if (action.equals("added")) {
                actionType = AuditLogHandler.ActionType.ADD;
            } else if (action.equals("updated")) {
                actionType = AuditLogHandler.ActionType.UPDATE;
            } else if (action.equals("deleted")) {
                actionType = AuditLogHandler.ActionType.DELETE;
            }
            sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Permission Set {%s} has been %s.", permissionSet.getDisplayName(), action),
                    String.format("Permission Set  %s  has been %s.", permissionSet.getDisplayName(), action),
                    AuditLogHandler.RecordType.SETTING,
                    "Permission Set", permissionSet.getId())
                    .setActionType(actionType)
                    .setLinkConfig(((Function<Void, String>) o -> {
                        JSONArray array = new JSONArray();
                        JSONObject json = new JSONObject();
                        json.put("id", permissionSet.getId());
                        json.put("name", permissionSet.getDisplayName());
                        json.put("navigateTo", "Data Sharing");
                        array.add(json);
                        return array.toJSONString();
                    }).apply(null))
            );
        }
    }
}