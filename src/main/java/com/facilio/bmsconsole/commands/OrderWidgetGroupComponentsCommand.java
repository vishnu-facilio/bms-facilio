package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderWidgetGroupComponentsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WidgetGroupSectionContext>  sections = (List<WidgetGroupSectionContext>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS);
        WidgetGroupContext widgetGroup = (WidgetGroupContext) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP);
        Map<Long,List<WidgetGroupWidgetContext>> widgetMaps = (Map<Long, List<WidgetGroupWidgetContext>>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_WIDGETS_MAP);

        if(widgetGroup != null) {
            if(CollectionUtils.isNotEmpty(sections)) {
                sections = sections.stream()
                        .filter(Objects::nonNull)
                        .peek(f->f.setWidgets(widgetMaps.get(f.getId())))
                        .collect(Collectors.toList());
                widgetGroup.setSections(sections);
            }
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, widgetGroup);
        return false;
    }
}
