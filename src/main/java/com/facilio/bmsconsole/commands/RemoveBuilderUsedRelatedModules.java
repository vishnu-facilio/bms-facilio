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

public class RemoveBuilderUsedRelatedModules extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> relatedFieldsVsModulesMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);

        if(CollectionUtils.isNotEmpty(relatedFieldsVsModulesMap)) {
            List<RelatedListWidgetContext> usedRelListOfWidget = RelatedListWidgetUtil.getRelatedListsOfWidgetId(widgetId, widgetWrapperType, false);

            if(usedRelListOfWidget != null) {
                List<Long> usedRelListFieldId = usedRelListOfWidget.stream()
                        .map(RelatedListWidgetContext::getFieldId)
                        .collect(Collectors.toList());

                relatedFieldsVsModulesMap.removeIf(relFieldModule -> usedRelListFieldId.contains((Long) (relFieldModule.get("fieldId"))));

            }

            context.put(FacilioConstants.ContextNames.MODULE_LIST, relatedFieldsVsModulesMap);        }
        return false;
    }
}
