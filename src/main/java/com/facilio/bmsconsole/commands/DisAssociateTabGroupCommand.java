package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DisAssociateTabGroupCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WebTabContext> tabs = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.WEB_TABS);
        Long groupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);

        if(CollectionUtils.isNotEmpty(tabs) && groupId != null && groupId > 0){
            List<Long> ids = new ArrayList<>();
            for(WebTabContext wt : tabs){
                ids.add(wt.getId());
            }
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("WEBTAB_ID", "webTabId", StringUtils.join(ids, ","), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "webTabGroupId", String.valueOf(groupId), NumberOperators.EQUALS));

            builder.delete();
        }

        return false;
    }
}
