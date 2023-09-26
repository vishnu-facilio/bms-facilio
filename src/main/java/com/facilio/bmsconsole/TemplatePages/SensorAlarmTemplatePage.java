package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SensorAlarmTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("alarmDetails", null, null)
                .addWidget("sensorAlarmDetails", "Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM))
                .widgetDone()
                .sectionDone()
                .addSection("secondaryDetails",null,null)
                .addWidget("sensorAlarmLocationDetails", "Location details",PageWidget.WidgetType.LOCATION_DETAILS,"webLocationDetails_14_6",0,0,null,null)
                .widgetDone()
                .addWidget("sensorAlarmDuration", "Time details", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration_14_6", 6,0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("sensorAlarmOccurrenceHistory", "Sensor Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory_58_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField lastOccurredTime = moduleBean.getField("lastOccurredTime", moduleName);
        FacilioField lastCreatedTime = moduleBean.getField("lastCreatedTime", moduleName);
        FacilioField readingField = moduleBean.getField("readingFieldId", moduleName);
        FacilioField resource = moduleBean.getField("resource", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 1, 1, false);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 2, 1, false);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, readingField, 1, 3, 1, false);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 1, 4, 1, false);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, resource, 2, 1, 1, true);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(4);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1, false);
            columnNo++;
            if(columnNo > 4) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("otherDetails");
        allFieldsWidgetGroup.setDisplayName("Other Details");
        allFieldsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan, Boolean isOneLevelField) throws Exception {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if(isOneLevelField){
                summaryField.setName("category");
                summaryField.setDisplayName("Category");
                summaryField.setParentLookupFieldId(field.getFieldId());
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioField assetCategory = modBean.getField("category", FacilioConstants.ContextNames.ASSET);
                summaryField.setFieldId(assetCategory.getFieldId());
            }
            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
