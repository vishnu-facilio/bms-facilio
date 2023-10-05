package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveEmptyConnectedAppTab extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        List<Long> tabsIdsToRemove = new ArrayList<>();
        if(!isFetchForClone && customPage != null) {
            for (Map.Entry<String, List<PageTabContext>> layout : customPage.getLayouts().entrySet()) {
                List<PageTabContext> tabs = layout.getValue();
                if (CollectionUtils.isNotEmpty(tabs)) {
                    for (PageTabContext tab : tabs) {
                        if (tab.getTabType() == PageTabContext.TabType.CONNECTED_TAB) {
                            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONNECTEDAPPS)) {
                                List<PageSectionWidgetContext> widgets = tab.getColumns().get(0)
                                        .getSections().get(0)
                                        .getWidgets();

                                if (CollectionUtils.isEmpty(widgets)) {
                                    tabsIdsToRemove.add(tab.getId());
                                }
                            }
                        }
                    }
                    layout.getValue().removeIf(t -> tabsIdsToRemove.contains(t.getId()));
                }
            }
        }
        return false;
    }
}
