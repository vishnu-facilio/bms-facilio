package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class FetchPageTabsCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(FetchPageTabsCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean excludeTabs = (boolean) context.getOrDefault(FacilioConstants.CustomPage.EXCLUDE_TABS, false);
        Long pageId = (Long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        Objects.requireNonNull(pageId, "Invalid pageId");
        if (!excludeTabs) {

            PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
            Long layoutId = CustomPageAPI.getLayoutIdForPageId(pageId,layoutType);
            Objects.requireNonNull(layoutId, "Layout does not exists for page --"+ pageId);

            PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
            customPage.setLayoutId(layoutId);

            boolean isBuilderRequest = (boolean) context.get(FacilioConstants.CustomPage.IS_BUILDER_REQUEST);
            String name = (String) context.get(FacilioConstants.CustomPage.TAB_NAME);

            List<PageTabContext> tabs;
            Long tabId = 0L;

            if (layoutId > 0) {
                tabs = CustomPageAPI.fetchTabs(layoutId, isBuilderRequest);

                if (CollectionUtils.isNotEmpty(tabs)) {

                    if (name != null && !name.isEmpty()) {
                        tabId = tabs.stream()
                                .filter(tab -> tab.getName().equals(name))
                                .map(tab -> {
                                    tab.setIsSelected(true);
                                    return tab.getId();
                                }).findFirst().orElse(tabs.get(0).getId());
                    }

                    if(tabId <= 0){
                        tabId = tabs.stream().filter(tab -> tab.getStatus() != null && tab.getStatus()).findFirst().orElse(tabs.get(0)).getId();
                    }
                }
                context.put(FacilioConstants.CustomPage.PAGE_TABS, tabs);
                context.put(FacilioConstants.CustomPage.TAB_ID, new ArrayList<>(Arrays.asList(tabId)));

                boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
                if(isFetchForClone) {
                    List<Long> tabIds = tabs.stream().map(PageTabContext::getId).collect(Collectors.toList());
                    context.put(FacilioConstants.CustomPage.TAB_ID, tabIds);
                }
            }
        }
            return false;
    }
}