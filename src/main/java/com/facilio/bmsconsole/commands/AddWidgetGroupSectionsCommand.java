package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class   AddWidgetGroupSectionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        Map<Long, List<WidgetGroupSectionContext>> widgetGroupSectionsMap = (Map<Long, List<WidgetGroupSectionContext>>) context.get(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS_MAP);

        FacilioModule widgetGroupSectionModule = ModuleFactory.getWidgetGroupSectionsModule();
        List<WidgetGroupSectionContext> sections = new ArrayList<>();

        if (MapUtils.isNotEmpty(widgetGroupSectionsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField widgetIdField = FieldFactory.getNumberField("widgetId", "WIDGET_ID", widgetGroupSectionModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, widgetGroupSectionsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(widgetGroupSectionModule, fetchExistingNamesCriteria, widgetIdField);

            for (Map.Entry<Long, List<WidgetGroupSectionContext>> entry : widgetGroupSectionsMap.entrySet()) {
                long widgetId = entry.getKey();
                List<WidgetGroupSectionContext> widgetGroupSections = entry.getValue();

                if (CollectionUtils.isNotEmpty(widgetGroupSections)) {
                    Criteria widgetIdCriteria = new Criteria();
                    widgetIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetIdField, String.valueOf(widgetId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetGroupSectionModule, widgetIdCriteria);

                    for (WidgetGroupSectionContext widgetGroupSection : widgetGroupSections) {
                        widgetGroupSection.setWidgetId(widgetId);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(widgetGroupSection, "widgetGroupSection");
                        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(widgetId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(widgetGroupSection.getName()) && !widgetGroupSection.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists, given linkName for widgetGroupSection is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, widgetId, existingNamesMap);

                        widgetGroupSection.setName(name);
                        if (widgetGroupSection.getSequenceNumber() <= 0) {
                            widgetGroupSection.setSequenceNumber(sequenceNumber += 10);
                        }
                        widgetGroupSection.setSysCreatedBy(currentUser);
                        widgetGroupSection.setSysCreatedTime(currentTime);

                        sections.add(widgetGroupSection);
                    }
                }
            }
        }
        WidgetGroupUtil.insertWidgetGroupSectionsToDB(sections);

        Map<Long, List<WidgetGroupWidgetContext>> widgetGroupSectionWidgetsMap = new HashMap<>();
        for (WidgetGroupSectionContext section : widgetGroupSectionsMap.values().stream().flatMap(List::stream).collect(Collectors.toList())) {
            widgetGroupSectionWidgetsMap.put(section.getId(), section.getWidgets());
        }

        context.put(FacilioConstants.CustomPage.SECTION_WIDGETS_MAP, widgetGroupSectionWidgetsMap);

        return false;
    }
}
