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

public class ReadingAlarmTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.NEW_READING_ALARM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return new PagesContext(null, null, "", null, true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "SUMMARY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("alarmDetails", null, null)
                .addWidget("bmsAlarmDetails", "Faults Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.NEW_READING_ALARM))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("insight", "INSIGHT", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("impactDetails", null, null)
                .addWidget("mtba","Mean time between occurrences", PageWidget.WidgetType.MTBA_CARD, "webMtba-14*6",0,0, null, null)
                .widgetDone()
                .addWidget("mttc", "Mean time to clear", PageWidget.WidgetType.MTTC_CARD, "webMttc-14*6", 6, 0, null, null)
                .widgetDone()
                .addWidget("alarmDuration", "No. of occurrences", PageWidget.WidgetType.ALARM_DURATION, "webAlarmDuration-12*6", 0, 14, null, null)
                .widgetDone()
                .addWidget("impactInfo", "Impact template", PageWidget.WidgetType.IMPACT_INFO, "webImpactInfo-12*6", 6, 14, null, null)
                .widgetDone()
                .addWidget("costImpact", "Cost impact", PageWidget.WidgetType.COST_IMPACT, "webCostImpact-14*6", 0,26, null, null)
                .widgetDone()
                .addWidget("energyImpact", "Energy impact (KWH)", PageWidget.WidgetType.ENERGY_IMPACT, "webEnergyImpact-14*6", 6,26, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("impactChart", null, null)
                .addWidget("impactReport", "Impact report", PageWidget.WidgetType.IMPACT_REPORT, "webImpactReport-51", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "HISTORY", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("occurrenceHistory", null, null)
                .addWidget("readingAlarmOccurrenceHistory", "Faults", PageWidget.WidgetType.OCCURRENCE_HISTORY, "webOccurrenceHistory-58*12", 0, 0, null, null)
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
        FacilioField rule = moduleBean.getField("rule", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup primaryDetailsWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastOccurredTime, 1, 1, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, lastCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(primaryDetailsWidgetGroup, rule, 1, 3, 1);

        primaryDetailsWidgetGroup.setName("primaryDetails");
        primaryDetailsWidgetGroup.setDisplayName("Primary Details");
        primaryDetailsWidgetGroup.setColumns(3);

        SummaryWidgetGroup otherDetailsWidgetGroup = new SummaryWidgetGroup();

        FacilioField resource = moduleBean.getField("resource", moduleName);

        addSummaryFieldInWidgetGroup(otherDetailsWidgetGroup, resource, 1, 1, 1);

        otherDetailsWidgetGroup.setName("otherDetails");
        otherDetailsWidgetGroup.setDisplayName("Other Details");
        otherDetailsWidgetGroup.setColumns(1);

        SummaryWidgetGroup allFieldsWidgetGroup = new SummaryWidgetGroup();

        List<FacilioField> fields = moduleBean.getAllFields(moduleName);
        int columnNo = 1, rowNo = 1;
        for (FacilioField field : fields) {
            addSummaryFieldInWidgetGroup(allFieldsWidgetGroup, field, rowNo, columnNo, 1);
            columnNo++;
            if(columnNo > 3) {
                columnNo = 1;
                rowNo ++;
            }
        }

        allFieldsWidgetGroup.setName("fields");
        allFieldsWidgetGroup.setDisplayName("Fields");
        allFieldsWidgetGroup.setColumns(3);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(primaryDetailsWidgetGroup);
        widgetGroupList.add(otherDetailsWidgetGroup);
        widgetGroupList.add(allFieldsWidgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) throws Exception {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
//            if(field.getName().equals("resource")) {
//                summaryField.setParentLookupFieldId(field.getFieldId());
//                ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//                FacilioField fieldToDisplay = moduleBean.getField("description", FacilioConstants.ContextNames.RESOURCE);
//                summaryField.setFieldId(fieldToDisplay.getFieldId());
//            }
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
