package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

public class AddOrUpdateTabGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WebTabGroupContext tabGroup = (WebTabGroupContext) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);
        if (tabGroup != null) {
            if (StringUtils.isEmpty(tabGroup.getName())) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            if (tabGroup.getId() <= 0) {
                int lastOrderNumber = getLastOrderNumber(tabGroup.getAppId());
                tabGroup.setOrder(lastOrderNumber + 1);
            }

            if (checkRouteAlreadyFound(tabGroup)) {
                throw new IllegalArgumentException("Route is already found for this app");
            }

            if (tabGroup.getId() > 0) {
                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getWebTabGroupModule().getTableName())
                        .fields(FieldFactory.getWebTabGroupFields())
                        .andCondition(CriteriaAPI.getIdCondition(tabGroup.getId(), ModuleFactory.getWebTabGroupModule()));
                builder.update(FieldUtil.getAsProperties(tabGroup));
            }
            else {
                GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getWebTabGroupModule().getTableName())
                        .fields(FieldFactory.getWebTabGroupFields());
                builder.insert(FieldUtil.getAsProperties(tabGroup));
            }
        }
        return false;
    }

    private boolean checkRouteAlreadyFound(WebTabGroupContext tabGroup) throws Exception {
        if (StringUtils.isEmpty(tabGroup.getRoute())) {
            throw new IllegalArgumentException("Route cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(Arrays.asList(FieldFactory.getField("order", "MAX(TABGROUP_ORDER)", FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getCondition("ROUTE", "route", tabGroup.getRoute(), StringOperators.IS));

        if (tabGroup.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(tabGroup.getId()), NumberOperators.NOT_EQUALS));
        }

        if (tabGroup.getAppId() == -1) {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", "", CommonOperators.IS_EMPTY));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(tabGroup.getAppId()), NumberOperators.EQUALS));
        }
        Map<String, Object> map = builder.fetchFirst();
        return map != null;
    }

    private int getLastOrderNumber(long appId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(Arrays.asList(FieldFactory.getField("order", "MAX(TABGROUP_ORDER)", FieldType.NUMBER)));
        if (appId == -1) {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", "", CommonOperators.IS_EMPTY));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(appId), NumberOperators.EQUALS));
        }
        Map<String, Object> map = builder.fetchFirst();
        if (MapUtils.isNotEmpty(map)) {
            Integer order = (Integer) map.get("order");
            if (order != null) {
                return order;
            }
        }
        return 0;
    }
}
