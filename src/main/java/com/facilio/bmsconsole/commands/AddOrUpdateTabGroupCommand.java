package com.facilio.bmsconsole.commands;

import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.metamigration.util.MetaMigrationConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class AddOrUpdateTabGroupCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        WebTabGroupContext tabGroup = (WebTabGroupContext) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP);
        boolean useOrderFromContext = (boolean) context.getOrDefault(MetaMigrationConstants.USE_ORDER_FROM_CONTEXT, false);
        if (tabGroup != null) {
            if (tabGroup.getId() <= 0) {
                if (!useOrderFromContext) {
                    int lastOrderNumber = getLastOrderNumber(tabGroup.getLayoutId());
                    tabGroup.setOrder(lastOrderNumber + 1);
                }
            }
            else {
                tabGroup.setOrder(-1);
            }

            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            if (tabGroup.getId() > 0) {
                tabBean.updateWebTabGroup(tabGroup);
            }
            else {
                long id = tabBean.addWebTabGroup(tabGroup);
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
