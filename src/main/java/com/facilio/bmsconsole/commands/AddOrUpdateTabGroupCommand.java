package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
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
import java.util.Collections;
import java.util.Map;

public class AddOrUpdateTabGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WebTabGroupContext tabGroup = (WebTabGroupContext) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);
        if (tabGroup != null) {
            if (tabGroup.getId() <= 0) {
                int lastOrderNumber = getLastOrderNumber(tabGroup.getLayoutId());
                tabGroup.setOrder(lastOrderNumber + 1);
            }
            else {
                tabGroup.setOrder(-1);
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
                long id = builder.insert(FieldUtil.getAsMapList(Collections.singletonList(tabGroup), WebTabGroupContext.class).get(0));
                tabGroup.setId(id);
                context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID, id);
            }
            context.put(FacilioConstants.ContextNames.WEB_TAB_GROUP, tabGroup);

            ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(tabGroup.getLayoutId()));
        }
        return false;
    }



    private int getLastOrderNumber(long layoutId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabGroupModule().getTableName())
                .select(Arrays.asList(FieldFactory.getField("order", "MAX(TABGROUP_ORDER)", FieldType.NUMBER)));

        if (layoutId <= 0) {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", "", CommonOperators.IS_EMPTY));
        }
        else {
            builder.andCondition(CriteriaAPI.getCondition("LAYOUT_ID", "layoutId", String.valueOf(layoutId), NumberOperators.EQUALS));
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
