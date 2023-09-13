package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.context.WidgetGroupWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AddWidgetGroupWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType != null ? layoutType : PagesContext.PageLayoutType.WEB;

        Map<Long, List<WidgetGroupWidgetContext>> widgetGroupWidgetsMap = (Map<Long, List<WidgetGroupWidgetContext>>) context.get(FacilioConstants.CustomPage.SECTION_WIDGETS_MAP);

        FacilioModule widgetGroupWidgetsModule = ModuleFactory.getWidgetGroupWidgetsModule();
        List<WidgetGroupWidgetContext> widgets = new ArrayList<>();

        if (MapUtils.isNotEmpty(widgetGroupWidgetsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField widgetGroupSectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", widgetGroupWidgetsModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetGroupSectionIdField, widgetGroupWidgetsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(widgetGroupWidgetsModule, fetchExistingNamesCriteria, widgetGroupSectionIdField);

            for (Map.Entry<Long, List<WidgetGroupWidgetContext>> entry : widgetGroupWidgetsMap.entrySet()) {
                long sectionId = entry.getKey();
                List<WidgetGroupWidgetContext> sectionWidgets = entry.getValue();

                if (CollectionUtils.isNotEmpty(sectionWidgets)) {
                    Criteria widgetGroupSectionIdCriteria = new Criteria();
                    widgetGroupSectionIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(widgetGroupSectionIdField, String.valueOf(sectionId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetGroupWidgetsModule, widgetGroupSectionIdCriteria);

                    for (WidgetGroupWidgetContext widget : sectionWidgets) {
                        widget.setSectionId(sectionId);
                        CustomPageAPI.validatePageWidget(widget, WidgetWrapperType.WIDGET_GROUP);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(widget, "widgetgroupwidget");
                        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(sectionId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists or given linkName for widget is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, sectionId, existingNamesMap);
                        widget.setName(name);

                        WidgetAPI.setConfigDetailsForWidgets(layoutType, widget);
                        widget.setSysCreatedBy(currentUser);
                        widget.setSysCreatedTime(currentTime);
                        if (widget.getSequenceNumber() <= 0) {
                            widget.setSequenceNumber(sequenceNumber += 10);
                        }
                        widgets.add(widget);
                    }
                }
            }
            WidgetGroupUtil.insertWidgetGroupWidgetsToDB(widgets);


            //for adding list of widgetsDetails
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
            for (WidgetGroupWidgetContext widget : widgets) {
                if (widget.getWidgetDetail() != null) {
                    WidgetConfigUtil.addWidgetDetail(appId, moduleName, layoutType, widget, WidgetWrapperType.WIDGET_GROUP, isSystem);
                }
            }
        }
        return false;
    }
}
