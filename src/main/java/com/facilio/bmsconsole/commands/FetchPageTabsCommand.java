package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class FetchPageTabsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FetchPageTabsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean excludeTabs = (boolean) context.getOrDefault(FacilioConstants.CustomPage.EXCLUDE_TABS, false);
        if (!excludeTabs) {

            Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
            boolean isBuilderRequest = (boolean) context.get(FacilioConstants.CustomPage.IS_BUILDER_REQUEST);
            String name = (String) context.get(FacilioConstants.CustomPage.TAB_NAME);
            PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);

            List<PageTabContext> tabs;
            Long tabId = 0L;

            if (pageId > 0) {
                tabs = CustomPageAPI.fetchTabs(pageId, isBuilderRequest);

                if (CollectionUtils.isNotEmpty(tabs)) {

                    customPage.setTabs(tabs);

                    if (name != null && !name.isEmpty()) {
                        tabId = tabs.stream()
                                .filter(tab -> tab.getName().equals(name))
                                .map(tab -> {
                                    tab.setIsSelected(true);
                                    return tab.getId();
                                }).findFirst().orElse(tabs.get(0).getId());
                    }

                    if(tabId <= 0){
                        tabId = tabs.get(0).getId();
                    }
                }
                context.put(FacilioConstants.CustomPage.PAGE_TABS, tabs);
                context.put(FacilioConstants.CustomPage.TAB_ID, tabId);
            }
        }
            return false;
    }
}