package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserNotificationAPI {

    public static Map<String, Object> getUserNotificationMapping (long userId) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getUserNotificationSeenMapping().getTableName())
                .select(FieldFactory.getUserNotificationSeenMappingFields()).
                        andCondition(CriteriaAPI.getCondition(
                                "USER", "user",String.valueOf(userId), NumberOperators.EQUALS));

        List<Map<String, Object>> resultMap = selectBuilder.get();

        return CollectionUtils.isNotEmpty(resultMap) ? resultMap.get(0) : null;
    }

    public static void insertUserNotificationMapping(long userId, long lastSeenTime) throws Exception {
        List<Map<String, Object>> propMapList = new ArrayList<>();
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("user", userId);
        propMap.put("lastSeen", lastSeenTime);
        propMapList.add(propMap);


        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getUserNotificationSeenMapping().getTableName())
                .fields(FieldFactory.getUserNotificationSeenMappingFields())
                .addRecords(propMapList);

        insertBuilder.save();

    }

    public static int getUpdateUserNotificationMapping (Long userId) throws Exception {

        FacilioModule module = ModuleFactory.getUserNotificationSeenMapping();
        List<FacilioField> fields = FieldFactory.getUserNotificationSeenMappingFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(
                "USER", "user","" + userId, NumberOperators.EQUALS));

        Map<String, Object> prop = new HashMap<>();
        prop.put("lastSeen",  System.currentTimeMillis());

        int count = builder.update(prop);

        return count;

    }

    public static int getUpdateStatusByUser (Long userId) throws Exception {
        FacilioModule module = ModuleFactory.getModule("usernotification");

        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .fields(module.getFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
//                .andCondition(CriteriaAPI.getOrgIdCondition())
                .andCondition(CriteriaAPI.getCondition(
                        "USER", "user","" + userId, NumberOperators.EQUALS));

        Map<String, Object> prop = new HashMap<>();
        prop.put("lastRead",  System.currentTimeMillis());
        int count = builder.update(prop);
        return count;
    }
}
