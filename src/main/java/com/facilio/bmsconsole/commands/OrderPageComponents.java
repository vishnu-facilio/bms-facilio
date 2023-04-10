package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class OrderPageComponents extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
//        Map<Long, List<WidgetGroupSectionWidgetsContext>> widgetGroupSectionWidgets = (Map<Long, List<WidgetGroupSectionWidgetsContext>>) context.getOrDefault(FacilioConstants.CustomPage.WIDGETGROUP_WIDGETS,new HashMap<>());
//        Map<Long,List<WidgetGroupSectionsContext>> widgetGroupSections = (Map<Long, List<WidgetGroupSectionsContext>>) context.getOrDefault(FacilioConstants.CustomPage.WIDGETGROUP_SECTIONS,new HashMap<>());
//        if(widgetGroupSections!=null && !widgetGroupSections.isEmpty()){
//            widgetGroupSections.forEach((key, values) -> values.stream()
//                    .filter(f->CollectionUtils.isNotEmpty(widgetGroupSectionWidgets.get(f.getId())))
//                    .forEach(f -> f.setWidgetGroupWidgets(widgetGroupSectionWidgets.get(f.getId()))));//setting widgetGroupSectionWidgets in widgetGroupSections
//        }
//        Map<Long,WidgetGroupConfigContext> widgetGroupConfig = (Map<Long, WidgetGroupConfigContext>) context.getOrDefault(FacilioConstants.CustomPage.WIDGETGROUP_CONFIGS,new HashMap<>());
//        Map<Long,List<PageSectionWidgetsContext>> widgets = (Map<Long, List<PageSectionWidgetsContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_SECTION_WIDGETS,new HashMap<>());
//        if(widgets!=null && !widgets.isEmpty()){
//            for(Map.Entry<Long,List<PageSectionWidgetsContext>> widget:widgets.entrySet()){
//                if(widgetGroupConfig!=null && !widgetGroupConfig.isEmpty()) {
//                    widget.getValue().stream().filter(f -> widgetGroupConfig.get(f.getId()) != null)
//                            .forEach(e -> e.getWidgetGroup().setWidgetGroupConfig(widgetGroupConfig.get(e.getId())));
//                }
//                if(widgetGroupSections!=null && !widgetGroupSections.isEmpty() ) {
//                    widget.getValue().stream().filter(f -> widgetGroupSections.get(f.getId()) != null)
//                            .forEach(e -> e.getWidgetGroup().setWidgetGroupSections(widgetGroupSections.get(e.getId())));
//                }
//            }
//        }

//        Map<Long,List<PageSectionContext>> sections = (Map<Long, List<PageSectionsContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_SECTIONS,new HashMap<>());
//        if(sections!=null && !sections.isEmpty()){
//            sections.forEach((key, value) -> value.stream()
//                    .filter(f->CollectionUtils.isNotEmpty(widgets.get(f.getId())))
//                    .forEach(f -> f.setWidgets(widgets.get(f.getId()))));
//        }
        /*
         *temp line
         */
        Map<Long,List<PageSectionContext>> sections = (Map<Long, List<PageSectionContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_SECTIONS,new HashMap<>());
        Map<Long,List<PageColumnContext>> columns = (Map<Long, List<PageColumnContext>>) context.getOrDefault(FacilioConstants.CustomPage.PAGE_COLUMNS,new HashMap<>());

        if(columns!=null && !columns.isEmpty()){
            columns.forEach((key,value) -> value.stream()
                    .filter(f->CollectionUtils.isNotEmpty(sections.get(f.getId())))
                    .forEach(f->f.setSections(sections.get(f.getId()))));
        }


        List<PageTabContext> tabs =  (List<PageTabContext>) context.get(FacilioConstants.CustomPage.PAGE_TABS);
        if(tabs!=null && !tabs.isEmpty()){
            tabs.stream().filter(f->CollectionUtils.isNotEmpty(columns.get(f.getId()))).forEach(f->f.setColumns(columns.get(f.getId())));
        }
        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        if(customPage!=null){
            if(tabs!=null && !tabs.isEmpty()){
                customPage.setTabs(tabs);
            }
        }
        return false;
    }
}