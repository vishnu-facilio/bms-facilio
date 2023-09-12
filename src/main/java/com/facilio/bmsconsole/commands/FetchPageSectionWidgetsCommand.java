package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchPageSectionWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> sectionIds = (List<Long>) context.get(FacilioConstants.CustomPage.SECTION_IDS);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (CollectionUtils.isNotEmpty(sectionIds)) {

            Map<Long, List<PageSectionWidgetContext>> widgets = CustomPageAPI.fetchPageSectionWidgets(sectionIds);

            if (widgets != null) {
                Map<Long, PageSectionWidgetContext> widgetIdMap = new HashMap<>();

                for (Map.Entry<Long, List<PageSectionWidgetContext>> widget : widgets.entrySet()) {
                    List<PageSectionWidgetContext> connectedAppWidgetToRemove = new ArrayList<>();
                    List<PageSectionWidgetContext> pageSectionWidgetContextList = new ArrayList<>(widget.getValue());

                    if (CollectionUtils.isNotEmpty(pageSectionWidgetContextList)) {
                        for (PageSectionWidgetContext wid : pageSectionWidgetContextList) {
                            if (!isFetchForClone && wid.getWidgetType().getFeatureId() != -1 && !hasLicenseEnabled(wid.getWidgetType().getFeatureId())) {
                                wid.setHasLicenseEnabled(false);
                            } else {
                                if (wid.getWidgetType().getFeatureId() != -1) wid.setHasLicenseEnabled(true);
                                widgetIdMap.put(wid.getId(), wid);
                                if (!isFetchForClone) {
                                    if (wid.getWidgetType() == PageWidget.WidgetType.CONNNECTED_APP) {
                                        boolean canShowWidget = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONNECTEDAPPS) &&
                                                CustomPageAPI.validateConnectedAppWidgetCriteria(recordId, moduleName, wid.getWidgetParams() != null ? (Long) wid.getWidgetParams().get("widgetId") : null);
                                        if (!(canShowWidget)) {
                                            connectedAppWidgetToRemove.add(wid);
                                        }
                                    }
                                }
                            }
                        }
                        widget.getValue().removeIf(connectedAppWidgetToRemove::contains);
                    }
                }

                WidgetConfigUtil.setWidgetDetailForWidgets(recordId, appId, moduleName, widgetIdMap, WidgetWrapperType.DEFAULT, isFetchForClone);
            }
            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS, widgets);
        }
        return false;
    }

    @SneakyThrows
    private boolean hasLicenseEnabled(int featureId) {
        AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.getFeatureLicense(featureId);
        boolean isEnabled = true;
        if (license != null) {
            isEnabled = AccountUtil.isFeatureEnabled(license);
        }
        return isEnabled;
    }
}