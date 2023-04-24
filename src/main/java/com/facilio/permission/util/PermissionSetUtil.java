package com.facilio.permission.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
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
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        List<PermissionSetContext> permissionSets = AccountUtil.getPermissionSets();
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        if(CollectionUtils.isNotEmpty(permissionSets)) {
            for(PermissionSetContext permissionSet : permissionSets) {
                if(permissionSet.isPrivileged()) {
                    return true;
                }
                if(permissionSet != null) {
                    List<Map<String, Object>> propsList = permissionSetBean.getPermissionValues(type, queryProp, permissionFieldEnum, permissionSet.getId());
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

    public static String constructLinkName(String linkName, String displayName) throws Exception {
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        List<PermissionSetContext> existingPermissionSetList = permissionSetBean.getPermissionSetsList(-1,-1,null,true);
        if (CollectionUtils.isEmpty(existingPermissionSetList)) {
            if (StringUtils.isNotEmpty(linkName)) {
                return linkName;
            }
            if (StringUtils.isNotEmpty(displayName)) {
                return displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
        }
        if (CollectionUtils.isNotEmpty(existingPermissionSetList)) {
            List<String> existingNames = existingPermissionSetList.stream().map(PermissionSetContext::getLinkName).collect(Collectors.toList());
            String foundName = null;
            if (StringUtils.isNotEmpty(linkName)) {
                foundName = linkName;
            } else if (StringUtils.isNotEmpty(displayName)) {
                foundName = displayName.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
            }
            if (StringUtils.isEmpty(foundName)) {
                throw new IllegalArgumentException("Unable to construct link name for permission set");
            }
            int i = 0;
            String constructedName = foundName;
            while (true) {
                if (existingNames.contains(constructedName)) {
                    constructedName = foundName + "_" + (++i);
                } else {
                    return constructedName;
                }
            }
        }
        throw new IllegalArgumentException("Unable to construct link name for permission set");
    }
}