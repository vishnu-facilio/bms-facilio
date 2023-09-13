package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetGroupContext;
import com.facilio.bmsconsole.context.WidgetGroupSectionContext;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FetchWidgetGroupSectionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        List<Long> widgetGroupSectionIds = new ArrayList<>();

        if (widgetId != null) {
            List<WidgetGroupSectionContext> sections = WidgetGroupUtil.fetchWidgetGroupsSections(widgetId);
            if (CollectionUtils.isNotEmpty(sections)) {
                widgetGroupSectionIds = sections.stream()
                        .map(WidgetGroupSectionContext::getId)
                        .collect(Collectors.toList());
            }
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS, sections);
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS, widgetGroupSectionIds);
        }
        return false;
    }
}
