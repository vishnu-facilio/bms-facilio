package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class GetPageTabCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        String tabName = (String) context.get(FacilioConstants.CustomPage.TAB_NAME);
        Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);

        PageTabContext tab = CustomPageAPI.getTab(id, pageId, tabName);

        FacilioUtil.throwIllegalArgumentException(tab == null, "Tab doesn't exists");
        context.put(FacilioConstants.CustomPage.TAB, tab);
        context.put(FacilioConstants.CustomPage.PAGE_TABS, Collections.singletonList(tab));
        context.put(FacilioConstants.CustomPage.TAB_ID, tab.getId());
        return false;
    }
}