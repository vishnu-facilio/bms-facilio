package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemoveBuilderUsedRelatedLists extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> relatedFieldsVsModulesMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);

        List<RelatedListWidgetContext> usedRelListOfWidget = RelatedListWidgetUtil.getRelatedListsOfWidgetId(widgetId, widgetWrapperType, false);
        if(CollectionUtils.isNotEmpty(relatedFieldsVsModulesMap)) {

            if(usedRelListOfWidget != null) {
                List<Long> usedRelListFieldIds = usedRelListOfWidget.stream()
                        .filter(relList -> relList.getFieldId() != null && relList.getFieldId() > 0)
                        .map(RelatedListWidgetContext::getFieldId)
                        .collect(Collectors.toList());

                relatedFieldsVsModulesMap.removeIf(relFieldModule -> usedRelListFieldIds.contains((Long) (relFieldModule.get("fieldId"))));
            }

            context.put(FacilioConstants.ContextNames.MODULE_LIST, relatedFieldsVsModulesMap);
        }
        List<Map<String, Object>> connectedAppRelListWidgets = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.CONNECTED_APP_WIDGETS);
        if(CollectionUtils.isNotEmpty(connectedAppRelListWidgets)) {

            if(usedRelListOfWidget != null) {
                List<Long> usedConnectedAppWidgetIds = usedRelListOfWidget.stream()
                        .filter(relList-> relList.getConnectedAppWidgetId() != null && relList.getConnectedAppWidgetId() > 0)
                        .map(RelatedListWidgetContext::getFieldId)
                        .collect(Collectors.toList());

                connectedAppRelListWidgets.removeIf(connectedAppRelListWidget -> usedConnectedAppWidgetIds.contains((Long) (connectedAppRelListWidget.get("connectedAppWidgetId"))));
            }
            context.put(FacilioConstants.ContextNames.CONNECTED_APP_RELATED_LISTS, connectedAppRelListWidgets);
        }
        return false;
    }
}
