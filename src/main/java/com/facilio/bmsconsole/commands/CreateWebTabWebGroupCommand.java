package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CreateWebTabWebGroupCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long groupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
        List<WebTabContext> webTabs = (List<WebTabContext>) context.get(FacilioConstants.ContextNames.WEB_TABS);
        if(CollectionUtils.isNotEmpty(webTabs) && groupId != null && groupId > 0){
        int order = 1;
        List<WebtabWebgroupContext> tabGroups = new ArrayList<>();
            for (WebTabContext webTabContext : webTabs) {
                WebtabWebgroupContext tabGroup = new WebtabWebgroupContext();
                tabGroup.setOrder(order++);
                tabGroup.setWebTabGroupId(groupId);
                tabGroup.setWebTabId(webTabContext.getId());
                tabGroups.add(tabGroup);
            }
            context.put(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP, tabGroups);
        }
        return false;
    }
}
