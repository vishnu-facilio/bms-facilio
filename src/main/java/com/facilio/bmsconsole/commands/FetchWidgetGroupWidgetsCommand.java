package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.WidgetGroupContext;
import com.facilio.bmsconsole.context.WidgetGroupSectionContext;
import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchWidgetGroupWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> widgetGroupSectionIds = (List<Long>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        WidgetGroupContext widgetGroup = (WidgetGroupContext) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP);
        Boolean isBuilderRequest = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_BUILDER_REQUEST, false);
        if(CollectionUtils.isNotEmpty(widgetGroupSectionIds)){
            Map<Long,List<WidgetGroupWidgetContext>> widgetMaps = WidgetGroupUtil.fetchWidgetGroupWidgets(recordId, appId, moduleName, widgetGroupSectionIds, isFetchForClone, isBuilderRequest);

            if (MapUtils.isNotEmpty(widgetMaps)) {
                Map<Long, WidgetGroupWidgetContext> widgetIdMap = new HashMap<>();

                for (Map.Entry<Long, List<WidgetGroupWidgetContext>> widget : widgetMaps.entrySet()) {
                    List<WidgetGroupWidgetContext> widgetGroupWidgets = new ArrayList<>(widget.getValue());

                    if (CollectionUtils.isNotEmpty(widgetGroupWidgets)) {
                        for (WidgetGroupWidgetContext wid : widgetGroupWidgets) {
                            if (!isFetchForClone && wid.getWidgetType().getFeatureId() != -1 && !CustomPageAPI.hasLicenseEnabled(wid.getWidgetType().getFeatureId())) {
                                wid.setHasLicenseEnabled(false);
                            } else {
                                if (wid.getWidgetType().getFeatureId() != -1) wid.setHasLicenseEnabled(true);
                                widgetIdMap.put(wid.getId(), wid);
                            }
                        }
                    }
                }

                WidgetConfigUtil.setWidgetDetailForWidgets(recordId, appId, moduleName, widgetIdMap, WidgetWrapperType.WIDGET_GROUP, isFetchForClone, isBuilderRequest);
            }
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_WIDGETS_MAP,  widgetMaps);
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, widgetGroup);
        return false;
    }

}
