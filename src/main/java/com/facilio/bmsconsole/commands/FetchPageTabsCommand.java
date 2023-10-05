package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.NonNull;
import lombok.SneakyThrows;
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
            Long layoutId = CustomPageAPI.getLayoutIdForPageId(pageId, layoutType);
            Objects.requireNonNull(layoutId, "Layout does not exists for page --" + pageId);

            PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
            customPage.setLayoutId(layoutId);

            boolean isBuilderRequest = (boolean) context.get(FacilioConstants.CustomPage.IS_BUILDER_REQUEST);
            String name = (String) context.get(FacilioConstants.CustomPage.TAB_NAME);

            List<PageTabContext> tabs;

            if (layoutId > 0) {
                tabs = CustomPageAPI.fetchTabs(layoutId, isBuilderRequest);
                tabs.stream().filter(tab->tab.getTabType() == null).forEach(tab -> tab.setTabType(PageTabContext.TabType.SIMPLE));

                if (CollectionUtils.isNotEmpty(tabs)) {

                    boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);

                    if (isFetchForClone) {
                        List<Long> tabIds = tabs.stream().map(PageTabContext::getId).collect(Collectors.toList());
                        context.put(FacilioConstants.CustomPage.TAB_ID, tabIds);
                    } else {
                        Long tabId = 0L;

                        tabs.removeIf(tab -> !hasLicenseEnabled(tab.getFeatureLicense()));

                        if (name != null && !name.isEmpty()) {
                            tabId = tabs.stream()
                                    .filter(tab -> tab.getName().equals(name))
                                    .map(tab -> {
                                        tab.setIsSelected(true);
                                        return tab.getId();
                                    }).findFirst().orElse(tabs.get(0).getId());
                        }

                        if (tabId <= 0) {
                            tabId = tabs.stream().filter(tab -> tab.getStatus() != null && tab.getStatus()).findFirst().orElse(tabs.get(0)).getId();
                        }

                        List<Long> tabIds = new ArrayList<>(Arrays.asList(tabId));
                        tabIds.addAll(getConnectedTabIds(tabs));
                        context.put(FacilioConstants.CustomPage.TAB_ID, tabIds);
                    }

                    context.put(FacilioConstants.CustomPage.PAGE_TABS, tabs);
                }

            }
        }
        return false;
    }

    private static List<Long> getConnectedTabIds(@NonNull List<PageTabContext> tabs) {
        return tabs.stream().filter(tab->tab.getTabType() == PageTabContext.TabType.CONNECTED_TAB).map(PageTabContext::getId).collect(Collectors.toList());
    }
    @SneakyThrows
    private boolean hasLicenseEnabled(int featureLicense) {
        AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.getFeatureLicense(featureLicense);
        boolean isEnabled = true;
        if (license != null) {
            isEnabled = AccountUtil.isFeatureEnabled(license);
        }
        return isEnabled;
    }
}