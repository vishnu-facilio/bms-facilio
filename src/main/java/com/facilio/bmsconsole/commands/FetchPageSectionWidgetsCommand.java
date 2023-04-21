package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FetchPageSectionWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> sectionIds = (List<Long>) context.get(FacilioConstants.CustomPage.SECTION_IDS);
        if(CollectionUtils.isNotEmpty(sectionIds)){

            Map<Long,List<PageSectionWidgetContext>> widgets = CustomPageAPI.fetchPageSectionWidgets(sectionIds);

            if(widgets != null) {
                Map<Long, PageSectionWidgetContext> widgetIdMap = widgets.values().stream().flatMap(List::stream)
                        .collect(Collectors.toMap(PageSectionWidgetContext::getId, Function.identity()));

                WidgetConfigUtil.setWidgetDetailForWidgets(widgetIdMap);
            }
            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS,widgets);
        }
        return false;
    }
}