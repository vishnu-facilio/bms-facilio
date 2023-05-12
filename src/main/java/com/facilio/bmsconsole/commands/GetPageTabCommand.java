package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.ApplicationLayoutContext;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GetPageTabCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        String tabName = (String) context.get(FacilioConstants.CustomPage.TAB_NAME);
        Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        Long layoutId = -1L;

        if(!(id != null && id > 0) || (pageId != null && pageId > 0)) {
             layoutId = CustomPageAPI.getLayoutIdForPageId(pageId, layoutType);
        }

        PageTabContext tab = CustomPageAPI.getTab(id, layoutId, tabName);
        FacilioUtil.throwIllegalArgumentException(tab == null, "Tab doesn't exists");
        context.put(FacilioConstants.CustomPage.TAB, tab);
        context.put(FacilioConstants.CustomPage.PAGE_TABS, new ArrayList<>(Arrays.asList(tab)));
        context.put(FacilioConstants.CustomPage.TAB_ID, new ArrayList<>(Arrays.asList(tab.getId())));
        return false;
    }
}