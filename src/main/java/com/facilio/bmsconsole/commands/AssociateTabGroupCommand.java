package com.facilio.bmsconsole.commands;

import com.facilio.beans.WebTabBean;
import com.facilio.bmsconsole.context.WebTabGroupContext;
import com.facilio.bmsconsole.context.WebtabWebgroupContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssociateTabGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WebtabWebgroupContext> tabsGroups = (List<WebtabWebgroupContext>) context.get(FacilioConstants.ContextNames.WEB_TAB_WEB_GROUP);

        if(CollectionUtils.isNotEmpty(tabsGroups)){
            WebTabBean tabBean = (WebTabBean) BeanFactory.lookup("TabBean");
            for(WebtabWebgroupContext tabGroup : tabsGroups) {
                tabBean.addWebtabWebtabGroup(tabGroup);
            }
            // Increment layout version
            Long groupId = (Long) context.get(FacilioConstants.ContextNames.WEB_TAB_GROUP_ID);
            WebTabGroupContext webTabGroup = tabBean.getWebTabGroup(groupId);
            if (webTabGroup == null) {
                throw new IllegalArgumentException("Invalid web group");
            }
            ApplicationApi.incrementLayoutVersionByIds(Collections.singletonList(webTabGroup.getLayoutId()));
        }

        return false;
    }
}
