package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.metamigration.util.MetaMigrationConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CreateWebTabWebGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long groupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
        List<WebTabContext> webTabs = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.WEB_TABS);
        boolean useOrderFromContext = (boolean) context.getOrDefault(MetaMigrationConstants.USE_ORDER_FROM_CONTEXT, false);
        if(CollectionUtils.isNotEmpty(webTabs) && groupId != null && groupId > 0){

            List<WebtabWebgroupContext> tabGroups = new ArrayList<>();
            int lastOrderNumber = getLastOrderNumber(groupId) + 1;

            for (WebTabContext webTabContext : webTabs) {
                WebtabWebgroupContext tabGroup = new WebtabWebgroupContext();
                if (useOrderFromContext) {
                    tabGroup.setOrder(webTabContext.getOrder());
                } else {
                    tabGroup.setOrder(lastOrderNumber++);
                }
                tabGroup.setWebTabGroupId(groupId);
                tabGroup.setWebTabId(webTabContext.getId());
                tabGroups.add(tabGroup);
            }
            context.put(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP, tabGroups);
        }
        return false;
    }

    private int getLastOrderNumber(Long groupId) throws Exception {
        if (groupId == null) {
            throw new IllegalArgumentException("Group Id cannot be empty");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                .select(Arrays.asList(FieldFactory.getField("order", "MAX(TAB_ORDER)", FieldType.NUMBER)))
                .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "tab_groupId", String.valueOf(groupId),
                        NumberOperators.EQUALS));
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
