package com.facilio.bmsconsole.commands;

import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssociateTabsWithGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WebtabWebgroupContext> tabsGroups = (List<WebtabWebgroupContext>) context.get(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP);

        if(CollectionUtils.isNotEmpty(tabsGroups)){
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");

            List<Long> groupIds = new ArrayList<>();
            for(WebtabWebgroupContext tabGroup : tabsGroups) {
                groupIds.add(tabGroup.getWebTabGroupId());
                tabBean.addWebtabWebtabGroup(tabGroup);
            }

            // Increment layout version
            for (Long groupId : groupIds) {
                WebTabGroupContext webTabGroup = tabBean.getWebTabGroup(groupId);
                if (webTabGroup == null) {
                    throw new IllegalArgumentException("Invalid web group");
                }
                ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(webTabGroup.getLayoutId()));
            }
        }
        return false;
    }
}
