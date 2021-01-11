package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class DeleteTabForGroupCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long groupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
        List<WebTabContext> webTabs = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.WEB_TABS);
        if(CollectionUtils.isNotEmpty(webTabs) && groupId != null && groupId > 0) {
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getWebTabWebGroupModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("WEBTAB_GROUP_ID", "tab_groupId", String.valueOf(groupId), NumberOperators.EQUALS));
            builder.delete();
        }
        return false;
    }
}
