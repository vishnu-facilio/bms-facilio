package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetGroupContext;
import com.facilio.bmsconsole.context.WidgetGroupSectionContext;
import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class GetWidgetGroupWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> widgetGroupSectionIds = (List<Long>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS);
        boolean isFetchForClone = (boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_FETCH_FOR_CLONE, false);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        WidgetGroupContext widgetGroup = (WidgetGroupContext) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(CollectionUtils.isNotEmpty(widgetGroupSectionIds)){
            Map<Long,List<WidgetGroupWidgetContext>> widgetMaps = WidgetGroupUtil.fetchWidgetGroupWidgets(recordId, appId, moduleName, widgetGroupSectionIds, isFetchForClone);
            if(MapUtils.isNotEmpty(widgetMaps)) {
                for(WidgetGroupSectionContext section : widgetGroup.getSections()) {
                    if(widgetMaps.get(section.getId()) != null ) {
                        section.setWidgets(widgetMaps.get(section.getId()));
                    }
                }
            }
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, widgetGroup);
        return false;
    }
}
