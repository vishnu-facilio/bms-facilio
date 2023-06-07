package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WidgetGroupSectionContext;
import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class    AddWidgetGroupSectionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WidgetGroupSectionContext> sections = (List<WidgetGroupSectionContext>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);

        FacilioModule module = ModuleFactory.getWidgetGroupSectionsModule();

        if(CollectionUtils.isNotEmpty(sections)) {
            FacilioField widgetGroupIdField = FieldFactory.getNumberField("widgetId", "WIDGET_ID", module);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetGroupIdField, String.valueOf(widgetId)));
            Map<Long, List<String>> existingSectionName = CustomPageAPI.getExistingNameListAsMap(module,criteria,widgetGroupIdField);
            double sequenceNumber = 0;

            for(WidgetGroupSectionContext section : sections){
                if(section.getSequenceNumber() <= 0){
                    section.setSequenceNumber(sequenceNumber+=10);
                }
                section.setWidgetId(widgetId);
                section.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
                section.setSysCreatedTime(System.currentTimeMillis());

                Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
                String name = StringUtils.isNotEmpty(section.getName()) ? section.getName() :
                        StringUtils.isNotEmpty(section.getDisplayName())? section.getDisplayName(): "widgetGroupSection";;
                name = name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
                name = existingSectionName.get(section.getWidgetId())!=null?CustomPageAPI.generateUniqueName(name,existingSectionName.get(section.getWidgetId()),isSystem):name;
                if((isSystem != null && isSystem) && StringUtils.isNotEmpty(section.getName()) && !section.getName().equalsIgnoreCase(name)) {
                    throw new IllegalArgumentException("linkName already exists, given linkName for section is invalid");
                }
                section.setName(name);

                if(existingSectionName.containsKey(section.getWidgetId())){
                    existingSectionName.get(section.getWidgetId()).add(name);
                }
                else {
                    List<String> nameslist = new ArrayList<>();
                    nameslist.add(name);
                    existingSectionName.put(section.getWidgetId(), nameslist);
                }
            }

            sections = WidgetGroupUtil.insertWGSectionsToDB(sections);
            List<Long> sectionIds = sections.stream()
                    .map(WidgetGroupSectionContext::getId)
                    .collect(Collectors.toList());
            List<WidgetGroupWidgetContext> widgets = WidgetGroupUtil.setWGSectionIdAndGetWidgets(sections);
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_WIDGETS, widgets);
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTION_IDS,sectionIds);
        }
        return false;
    }
}
