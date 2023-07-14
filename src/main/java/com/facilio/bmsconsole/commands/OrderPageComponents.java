package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class OrderPageComponents extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<Long, List<PageSectionWidgetContext>> widgets = (Map<Long, List<PageSectionWidgetContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS, new HashMap<>());
        Map<Long, List<PageSectionContext>> sections = (Map<Long, List<PageSectionContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_SECTIONS, new HashMap<>());
        if (MapUtils.isNotEmpty(sections) && MapUtils.isNotEmpty(widgets)) {
            sections.forEach((key, value) -> value.stream()
                    .filter(section -> CollectionUtils.isNotEmpty(widgets.get(section.getId())))
                    .forEach(section -> section.setWidgets(widgets.get(section.getId()))));
        }

        Map<Long, List<PageColumnContext>> columns = (Map<Long, List<PageColumnContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_COLUMNS, new HashMap<>());
        if (MapUtils.isNotEmpty(columns) && MapUtils.isNotEmpty(sections)) {
            columns.forEach((key, value) -> value.stream()
                    .filter(column -> CollectionUtils.isNotEmpty(sections.get(column.getId())))
                    .forEach(column -> column.setSections(sections.get(column.getId()))));
        }


        List<PageTabContext> tabs = (List<PageTabContext>) context.get(FacilioConstants.CustomPage.PAGE_TABS);
        if (tabs != null && !tabs.isEmpty() && MapUtils.isNotEmpty(columns)) {
            tabs.stream().filter(f -> CollectionUtils.isNotEmpty(columns.get(f.getId()))).forEach(f -> f.setColumns(columns.get(f.getId())));
        }

        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType != null? layoutType : PagesContext.PageLayoutType.WEB;

        PagesContext.PageLayoutType finalLayoutType = layoutType;
        Map<String, List<PageTabContext>> layoutTabMap = new HashMap<String, List<PageTabContext>>() {{
            put(finalLayoutType.name(), tabs);
        }};

        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        if (customPage != null) {
            if (CollectionUtils.isNotEmpty(tabs)) {
                customPage.setLayouts(layoutTabMap);
            }
        }

        return false;
    }
}