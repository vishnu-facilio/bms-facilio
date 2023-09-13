package com.facilio.bmsconsole.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetConfigUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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

public class AddPageSectionWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
        PagesContext.PageLayoutType layoutType = (PagesContext.PageLayoutType) context.get(FacilioConstants.CustomPage.LAYOUT_TYPE);
        layoutType = layoutType != null ? layoutType : PagesContext.PageLayoutType.WEB;

        Map<Long, List<PageSectionWidgetContext>> sectionWidgetsMap = (Map<Long, List<PageSectionWidgetContext>>) context.get(FacilioConstants.CustomPage.SECTION_WIDGETS_MAP);

        FacilioModule widgetsModule = ModuleFactory.getPageSectionWidgetsModule();
        List<PageSectionWidgetContext> widgets = new ArrayList<>();

        if (MapUtils.isNotEmpty(sectionWidgetsMap)) {
            long currentUser = AccountUtil.getCurrentUser().getId();
            long currentTime = System.currentTimeMillis();
            FacilioField sectionIdField = FieldFactory.getNumberField("sectionId", "SECTION_ID", widgetsModule);

            Criteria fetchExistingNamesCriteria = new Criteria();
            fetchExistingNamesCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(sectionIdField, sectionWidgetsMap.keySet().stream().map(String::valueOf).collect(Collectors.joining(", "))));
            Map<Long, List<String>> existingNamesMap = CustomPageAPI.getExistingNameListAsMap(widgetsModule, fetchExistingNamesCriteria, sectionIdField);

            for (Map.Entry<Long, List<PageSectionWidgetContext>> entry : sectionWidgetsMap.entrySet()) {
                long sectionId = entry.getKey();
                List<PageSectionWidgetContext> sectionWidgets = entry.getValue();

                if (CollectionUtils.isNotEmpty(sectionWidgets)) {
                    Criteria sectionIdCriteria = new Criteria();
                    sectionIdCriteria.addAndCondition(CriteriaAPI.getEqualsCondition(sectionIdField, String.valueOf(sectionId)));
                    double sequenceNumber = CustomPageAPI.getMaxSequenceNumber(widgetsModule, sectionIdCriteria);

                    for (PageSectionWidgetContext widget : sectionWidgets) {
                        widget.setSectionId(sectionId);

                        String name = CustomPageAPI.getLinkNameFromObjectOrDefault(widget, "widget");
                        name = CustomPageAPI.generateUniqueName(name, existingNamesMap.get(sectionId), isSystem);
                        if ((isSystem != null && isSystem) && StringUtils.isNotEmpty(widget.getName()) && !widget.getName().equalsIgnoreCase(name)) {
                            throw new IllegalArgumentException("linkName already exists or given linkName for widget is invalid");
                        }
                        CustomPageAPI.addNameToMap(name, sectionId, existingNamesMap);
                        widget.setName(name);

                        CustomPageAPI.validatePageWidget(widget, WidgetWrapperType.DEFAULT);
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

            CustomPageAPI.insertPageSectionWidgetsToDB(widgets);

            //below codes update sysModifiedTime in parent tables
            Map<String, Object> sysModifiedProps = new HashMap<>();
            sysModifiedProps.put("sysModifiedBy", currentUser);
            sysModifiedProps.put("sysModifiedTime", currentTime);
            CustomPageAPI.updateSysModifiedFields(widgets.get(0).getSectionId(), sysModifiedProps, CustomPageAPI.PageComponent.SECTION);


            //for adding list of widgetDetails
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
            for (PageSectionWidgetContext widget : widgets) {
                if (widget.getWidgetDetail() != null) {
                    WidgetConfigUtil.addWidgetDetail(appId, moduleName, layoutType, widget, WidgetWrapperType.DEFAULT, isSystem);
                }
            }
        }
        return false;
    }

    public static double getColumnWidth(long sectionId) throws Exception {
        if (sectionId > 0) {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getPageColumnsModule().getTableName())
                    .select(Arrays.asList(FieldFactory.getNumberField("width", "WIDTH", ModuleFactory.getPageColumnsModule())))
                    .innerJoin(ModuleFactory.getPageSectionsModule().getTableName())
                    .on("Page_Columns.ID = Page_Sections.COLUMN_ID")
                    .andCondition(CriteriaAPI.getEqualsCondition(FieldFactory.getIdField(ModuleFactory.getPageSectionsModule()), String.valueOf(sectionId)));

            Map<String, Object> map = builder.fetchFirst();

            if (MapUtils.isNotEmpty(map)) {
                return (((Long) map.get("width")));
            }
        }
        return 0D;
    }
}